package cloudify.widget.ec2;

import cloudify.widget.common.CidrUtils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 10/27/14
 * Time: 10:16 PM
 */
public class Ec2SecurityGroup {

    private static Logger logger = LoggerFactory.getLogger(Ec2SecurityGroup.class);

    public Ec2ConnectDetails ec2ConnectDetails;

    public Ec2SecurityGroup(Ec2ConnectDetails ec2ConnectDetails) {
        this.ec2ConnectDetails = ec2ConnectDetails;
    }





    /**
     * Check if the security group is opened according to the given security group data
     * @param data
     * @return
     */
    public boolean isSecurityGroupOpen(WidgetSecurityGroupData data, List<SecurityGroup> securityGroups ) {



        // Check all groups with this name (if possible)
        for (SecurityGroup securityGroup : securityGroups) {
            logger.info("Checking securityGroup [{}]", securityGroup.getGroupName());

            // Go over the ip permissions and check if its in range
            List<IpPermission> ipPermissions = securityGroup.getIpPermissions();
            for (IpPermission ipPermission : ipPermissions) {

                logger.info("Check if ips: {} are in range of {} ",Arrays.toString(data.getIps().toArray()), Arrays.toString(ipPermission.getIpRanges().toArray()));

                // Check if ips are in range
                if( areIpsInRange(data.getIps(), ipPermission.getIpRanges())){
                    logger.info("Check if ports {} are in range of {} - {} ",Arrays.toString(data.getPorts().toArray()),ipPermission.getFromPort(),ipPermission.getToPort());

                    // Check if port is in range
                    if (arePortsInRange(data.getPorts(),ipPermission.getFromPort(),ipPermission.getToPort())) {
                        logger.info("Found a match!");

                        // Found a match, no need to continue looking
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public List<SecurityGroup> getSecurityGroups(WidgetSecurityGroupData data) throws AmazonServiceException {
        // Connect to ec2
        final AWSCredentials credentials = new BasicAWSCredentials(ec2ConnectDetails.getAccessId(), ec2ConnectDetails.getSecretAccessKey());
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);

        // Build the query request (according to group name)
        DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();

        List<String> groupNames = new ArrayList<String>();
        groupNames.add(data.getName());
        describeSecurityGroupsRequest.setGroupNames(groupNames);

        // Get the group(s) data
        DescribeSecurityGroupsResult describeSecurityGroupsResult = ec2.describeSecurityGroups(describeSecurityGroupsRequest);

        List<SecurityGroup> securityGroups = describeSecurityGroupsResult.getSecurityGroups();
        logger.info("Found securityGroups [{}] for name {}", securityGroups.size(),data.getName());
        return securityGroups;
    }

    /**
     * Check if all the ports are in range
     * @param ports
     * @param fromPort
     * @param toPort
     * @return
     */
    private boolean arePortsInRange(List<Integer> ports, Integer fromPort, Integer toPort) {
        // Null indicates ALL
        if (fromPort == null && toPort == null) {
            return true;
        }

        // Go over the ports and make sure its all in range
        for (Integer port: ports) {
            if (fromPort > port || toPort < port) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if ALL the requested ips are in range
     * @param ips
     * @param ipRanges
     * @return
     */
    private boolean areIpsInRange(List<String> ips, List<String> ipRanges) {
        // Go over the ranges and build their CidrUtils (the build of this object is heavy, so save those, and afterwords cross the ips with those ranges.
        // (this is becuase we have to check if all the ips exists in any range and not visa versa
        List<CidrUtils> rangesCidrUtils = new ArrayList<CidrUtils>();
        for (String range: ipRanges) {
            logger.info("Checking ip range: {} ", range);
            try {
                CidrUtils cidrUtils = new CidrUtils(range);
                rangesCidrUtils.add(cidrUtils);
            } catch(Throwable t) {
                logger.error("Unable to parse range {}",range);
            }
        }

        // Go over the ips to check and see if we have a match with any of the ranges
        for (String ip: ips) {
            boolean found = false;
            for (CidrUtils range:rangesCidrUtils ) {
                try {
                    if (range.isInRange(ip)) {
                        found = true;
                    }
                } catch (UnknownHostException e) {
                    logger.error("Unable to check ip {} for range", ip);
                }
            }

            // If we didnt found a match for one ip its a no-go
            if (!found) {
                return false;
            }
        }

        // All is good
        return true;
    }

    /**
     * Create a security group according to the given data
     * @param data
     */
    public void createSecurityGroup(WidgetSecurityGroupData data) throws AmazonServiceException{

        // Connect to ec2
        final AWSCredentials credentials = new BasicAWSCredentials(ec2ConnectDetails.getAccessId(), ec2ConnectDetails.getSecretAccessKey());
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);

        // Creating the gruop
        logger.info("Creating group with name: {}",data.getName());
        CreateSecurityGroupRequest request = new CreateSecurityGroupRequest(data.getName(),"Auto created group");

        try {
            ec2.createSecurityGroup(request);
        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals("InvalidGroup.Duplicate")) {
                logger.error("A group with the name already exists. Aborting.");
            } else {
                logger.error("Error creating the security group. error message: {} ", e.getMessage());
            }
            throw e;
        }

        // Create ip permissions list
        List<IpPermission> ips = new ArrayList<IpPermission>();

        for (Integer port : data.getPorts()) {
            IpPermission p = new IpPermission();
            p.setIpProtocol("TCP");
            p.setFromPort(port);
            p.setToPort(port);

            List<String> ranges = new ArrayList<String>();
            for (String range: data.getIps()) {
                ranges.add(range);
            }

            p.setIpRanges(ranges);

            logger.info("Add ip permission for ports {} - {} and ip ranges: {}",port,port,Arrays.toString(ranges.toArray()));
            ips.add(p);
        }

        AuthorizeSecurityGroupIngressRequest authRequest = new AuthorizeSecurityGroupIngressRequest(data.getName(),ips);
        ec2.authorizeSecurityGroupIngress(authRequest);
    }
}
