package cloudify.widget.allclouds;

import cloudify.widget.allclouds.executiondata.ExecutionDataModel;
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
 * Date: 10/29/14
 * Time: 8:32 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:dev/me-context.xml"})
public class TestExecutionModelEncryption {

    private static Logger logger = LoggerFactory.getLogger(TestExecutionModelEncryption.class);

    @Autowired
    TestConfiguration configuration;

    @Test
    public void testEncryption(){

    }


    @Test
    public void testDecryption(){

        ExecutionDataModel model = new ExecutionDataModel();

        model.setEncryptionKey(configuration.key);

        model.decrypt(configuration.content);

        logger.info(model.getRaw());
    }


}
