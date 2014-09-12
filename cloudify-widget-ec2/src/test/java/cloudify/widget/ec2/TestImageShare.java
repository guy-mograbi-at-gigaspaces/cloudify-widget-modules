package cloudify.widget.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/11/14
 * Time: 2:04 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:ec2-context.xml"})
public class TestImageShare {

    private static Logger logger = LoggerFactory.getLogger(TestImageShare.class);

    @Autowired
    TestKeyPairGenerator.TestDetails conf;


    @Test
    public void listAllImages(){
        final AWSCredentials credentials = new BasicAWSCredentials(conf.getKey(), conf.getSecretKey());
        AmazonEC2Client ec2 = new AmazonEC2Client(credentials);
        ec2.setEndpoint("ec2.us-west-2.amazonaws.com");
        DescribeImagesRequest request = new DescribeImagesRequest();
        AmazonIdentityManagement im = new AmazonIdentityManagementClient(credentials);
        String userId = im.getUser().getUser().getUserId();
        logger.info("userId is [{}]", userId);

        request.withOwners( userId );
//        request.withImageIds("ami-3b73320b");
        logger.info("getting images");
        DescribeImagesResult describeImagesResult = ec2.describeImages(request);
        logger.info("got images");
        List<Image> images = describeImagesResult.getImages();

        logger.info("images [{}]",images);
    }

    @Test
    public void testImageShare(){

        String accountId = new Ec2AccountIdReader().getAccountId(conf.getKey(), conf.getSecretKey());

        Ec2ImageShare ec2ImageShare = new Ec2ImageShare();
        ec2ImageShare.setPermissions(conf.getKey(), conf.getSecretKey(), conf.getEndpoint(), "ami-3b73320b", Ec2ImageShare.Operation.ADD, accountId);
    }

    public TestKeyPairGenerator.TestDetails getConf() {
        return conf;
    }

    public void setConf(TestKeyPairGenerator.TestDetails conf) {
        this.conf = conf;
    }
}
