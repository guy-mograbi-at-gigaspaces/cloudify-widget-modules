package cloudify.widget.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAccountAttributesRequest;
import com.amazonaws.services.ec2.model.DescribeAccountAttributesResult;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/11/14
 * Time: 1:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(locations = {"classpath:ec2-context.xml"})
public class TestGetAccountId {


    private static Logger logger = LoggerFactory.getLogger(TestGetAccountId.class);
    @Autowired
    TestKeyPairGenerator.TestDetails conf;

    @Test
    public void testAccountId(){
        String accountId = new Ec2AccountIdReader().getAccountId(conf.getKey(), conf.getSecretKey());
        logger.info("accountId = [{}]",accountId) ;
    }


    public TestKeyPairGenerator.TestDetails getConf() {
        return conf;
    }

    public void setConf(TestKeyPairGenerator.TestDetails conf) {
        this.conf = conf;
    }

}
