package cloudify.widget.ec2;

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
 * Date: 7/10/14
 * Time: 6:03 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:ec2-context.xml"})
public class TestKeyPairGenerator {

    public static class TestDetails{
        String key;
        String secretKey;
        String imageId;
        String accountId;
        String endpoint;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

    @Autowired
    TestDetails testDetails;

    private static Logger logger = LoggerFactory.getLogger(TestKeyPairGenerator.class);

    @Test
    public void testKeyPairGenerator(){
        Ec2KeyPairGenerator gen = new Ec2KeyPairGenerator();
         Ec2KeyPairGenerator.Data data = gen.generate( testDetails.getKey(), testDetails.getSecretKey() );
         logger.info("[{}]",data);
    }

    public void setTestDetails(TestDetails testDetails) {
        this.testDetails = testDetails;
    }
}
