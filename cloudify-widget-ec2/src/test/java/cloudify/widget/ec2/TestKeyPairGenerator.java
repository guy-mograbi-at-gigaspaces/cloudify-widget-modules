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
    }

    @Autowired
    TestDetails testDetails;

    private static Logger logger = LoggerFactory.getLogger(TestKeyPairGenerator.class);

    @Test
    public void testKeyPairGenerator(){
        Ec2KeyPairGenerator gen = new Ec2KeyPairGenerator();
         String output = gen.generate( testDetails.getKey(), testDetails.getSecretKey() );
         logger.info(output);
    }

    public void setTestDetails(TestDetails testDetails) {
        this.testDetails = testDetails;
    }
}
