package cloudify.widget.common;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/17/14
 * Time: 6:22 PM
 */
public class TestResourceWalker {

    private static Logger logger = LoggerFactory.getLogger(TestResourceWalker.class);

    @Test
    public void testResourceWalker() throws IOException {
        ResourceWalker walker = new ResourceWalker();
        LinkedList<ResourceWalker.ResourceWalkResult> result = new LinkedList<ResourceWalker.ResourceWalkResult>();
        walker.walkResource(new File("."), result);
        logger.info("this is result [{}]",new ObjectMapper().writeValueAsString(result));

    }

}
