package cloudify.widget.ec2;

import cloudify.widget.common.CidrUtils;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.apache.commons.net.util.SubnetUtils;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeService;
import org.jclouds.aws.ec2.features.AWSSecurityGroupApi;
import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 10/27/14
 * Time: 10:16 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:ec2-sg-context.xml"})
public class Ec2SecurityGroupTest {

    @Autowired
    public Ec2ConnectDetails ec2ConnectDetails;

    private static Logger logger = LoggerFactory.getLogger(Ec2SecurityGroupTest.class);

    @Test
    public void checkCreateSecurityGroup() {
        Ec2SecurityGroup sg = new Ec2SecurityGroup(ec2ConnectDetails);

        List<String> ips = new ArrayList<String>();
        ips.add("10.10.10.10/32");
        ips.add("20.20.20.20/32");

        List<Integer> ports = new ArrayList<Integer>();
        ports.add(100);
        ports.add(200);

        WidgetSecurityGroupData data = new WidgetSecurityGroupData("test-group",ips,ports);

        sg.createSecurityGroup(data);
    }

    @Test
    public void checkOpenedSecurityGroup() {
        Ec2SecurityGroup sg = new Ec2SecurityGroup(ec2ConnectDetails);

        // Check something taht exists
        List<String> ips = new ArrayList<String>();
        ips.add("10.10.10.10");
        ips.add("20.20.20.20");

        List<Integer> ports = new ArrayList<Integer>();
        ports.add(100);
        ports.add(200);

        WidgetSecurityGroupData data = new WidgetSecurityGroupData("default",ips,ports);

        boolean sgOpened = sg.isSecurityGroupOpen(data);

        logger.info("Security group is opened? (should be true) "+sgOpened);
        if (!sgOpened) throw new AssertionError("Expected group to be opened");

        // Check when one port exist, and one doesnt (shouldnt match)
        ports.clear();
        ports.add(22);
        ports.add(33);
        data = new WidgetSecurityGroupData("launch-wizard-2",ips,ports);
        sgOpened = sg.isSecurityGroupOpen(data);

        logger.info("Security group is opened? (should be false) "+sgOpened);
        if (sgOpened) throw new AssertionError("Expected group to not be opened");

        // Check when one port exist, and this one only
        ports.clear();
        ports.add(22);
        data = new WidgetSecurityGroupData("launch-wizard-2",ips,ports);
        sgOpened = sg.isSecurityGroupOpen(data);

        logger.info("Security group is opened? (should be true) "+sgOpened);
        if (!sgOpened) throw new AssertionError("Expected group to be opened");

        // Check no data exists in security group
        data = new WidgetSecurityGroupData("jclouds#ec2blu-mngr-1",ips,ports);
        sgOpened = sg.isSecurityGroupOpen(data);

        logger.info("Security group is opened? (should be false) "+sgOpened);
        if (sgOpened) throw new AssertionError("Expected group to not be opened");

        // Check ports equals range , but ips not in range
        ports.clear();
        ports.add(100);
        ports.add(200);
        data = new WidgetSecurityGroupData("name",ips,ports);
        sgOpened = sg.isSecurityGroupOpen(data);

        logger.info("Security group is opened? (should be false) "+sgOpened);
        if (sgOpened) throw new AssertionError("Expected group to not be opened");

        // Check ports equals range, ips in range
        ips.clear();
        ips.add("1.1.1.1");
        ips.add("1.1.1.12");

        data = new WidgetSecurityGroupData("name",ips,ports);
        sgOpened = sg.isSecurityGroupOpen(data);

        logger.info("Security group is opened? (should be true) "+sgOpened);

        if (!sgOpened) throw new AssertionError("Expected group to be opened");
    }

    @Test
    public void getAllSecurityGroups(){
        logger.info("generating private key");
        final AWSCredentials credentials = new BasicAWSCredentials(ec2ConnectDetails.getAccessId(), ec2ConnectDetails.getSecretAccessKey());
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);

        DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();

        DescribeSecurityGroupsResult describeSecurityGroupsResult = ec2.describeSecurityGroups();

        List<com.amazonaws.services.ec2.model.SecurityGroup> securityGroups = describeSecurityGroupsResult.getSecurityGroups();
        logger.info("got securityGroups [{}]", securityGroups.size());

        for (com.amazonaws.services.ec2.model.SecurityGroup securityGroup : securityGroups) {
            logger.info("securityGroup [{}]", securityGroup.getGroupName());


//            securityGroup.get
            List<com.amazonaws.services.ec2.model.IpPermission> ipPermissions = securityGroup.getIpPermissions();
            for (com.amazonaws.services.ec2.model.IpPermission ipPermission : ipPermissions) {


                try {
                    CidrUtils cidrUtils = new CidrUtils(ipPermission.getIpRanges().iterator().next());
                    boolean inRange = cidrUtils.isInRange("1.1.1.1");


                    logger.info("[{}] :: [{}]", ipPermission.getIpRanges(), inRange);
                }catch(Exception e){
                    logger.error("unable to decide if in range");
                }
            }

        }
    }

    @Test
    public void createSecurityGroup() {
        final AWSCredentials credentials = new BasicAWSCredentials(ec2ConnectDetails.getAccessId(), ec2ConnectDetails.getSecretAccessKey());
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);

        CreateSecurityGroupRequest request = new CreateSecurityGroupRequest("name","desc");
        CreateSecurityGroupResult result = ec2.createSecurityGroup(request);

        // Create ip permissions
        List<com.amazonaws.services.ec2.model.IpPermission> ips = new ArrayList<com.amazonaws.services.ec2.model.IpPermission>();
        com.amazonaws.services.ec2.model.IpPermission p = new com.amazonaws.services.ec2.model.IpPermission();
        p.setIpProtocol("TCP");
        p.setFromPort(10);
        p.setToPort(1000);
        List<String> ranges = new ArrayList<String>();
        ranges.add("1.1.1.1");
        p.setIpRanges(ranges);
        ips.add(p);

        AuthorizeSecurityGroupIngressRequest authRequest = new AuthorizeSecurityGroupIngressRequest("name",ips);
        ec2.authorizeSecurityGroupIngress(authRequest);

    }

        @Test
    public void getAccount(){
        String accountId = new Ec2AccountIdReader().getAccountId(ec2ConnectDetails.getAccessId(), ec2ConnectDetails.getSecretAccessKey());
        logger.info("[{}]",accountId);
    }
    @Test
    public void getAllImages(){
        logger.info("generating private key");


        final AWSCredentials credentials = new BasicAWSCredentials(ec2ConnectDetails.getAccessId(), ec2ConnectDetails.getSecretAccessKey());
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);


        DescribeImagesRequest request = new DescribeImagesRequest();

        DescribeImagesResult describeImagesResult = ec2.describeImages();
        List<Image> images = describeImagesResult.getImages();
        for (Image image : images) {
            if ( image.getImageId().equals("ami-3a824d52")) {
                logger.info("[{}]", image);
            }
        }
    }

    public Ec2ConnectDetails getEc2ConnectDetails() {
        return ec2ConnectDetails;
    }

    public void setEc2ConnectDetails(Ec2ConnectDetails ec2ConnectDetails) {
        this.ec2ConnectDetails = ec2ConnectDetails;
    }
}
