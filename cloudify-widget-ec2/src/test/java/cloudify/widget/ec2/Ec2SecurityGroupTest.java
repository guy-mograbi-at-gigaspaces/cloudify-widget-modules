package cloudify.widget.ec2;

import cloudify.widget.common.CidrUtils;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.KeyPair;
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
