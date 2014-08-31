package cloudify.widget.common;

import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
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
 * Date: 8/26/14
 * Time: 7:40 PM
 */
public class TestMandrillSender {


    private static Logger logger = LoggerFactory.getLogger(TestMandrillSender.class);

    public String getString( String classpathUrl ){
        try {
            return FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(classpathUrl).getFile()));
        }catch(Exception e){
            throw new RuntimeException("unable to read file from classpath", e);
        }
    }

    public MandrillSender.MandrillEmailDetails toDetails( String content ){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.getSerializationConfig().addMixInAnnotations(MandrillMessage.class, MandrillMessageMixin.class);
            mapper.getDeserializationConfig().addMixInAnnotations(MandrillMessage.class, MandrillMessageMixin.class);
            return mapper.readValue(content, MandrillSender.MandrillEmailDetails.class);
        }catch(Exception e){
            throw new RuntimeException("unable to convert to object",e);
        }

    }

    @Test
    public void testJsonSerialization(){
        String basicJson = getString("mandrillEmailDetails/basic.json");
         logger.info("MandrillMessageDetails = [{}]", toDetails(basicJson));
    }

    @Test
    public void MandrillMessageToJsonTest() throws IOException {
         MandrillMessage mandrillMessage = new MandrillMessage();

        LinkedList<MandrillMessage.Recipient> to = new LinkedList<MandrillMessage.Recipient>();
        MandrillMessage.Recipient e = new MandrillMessage.Recipient();
        e.setEmail("me@email.com");
        e.setName("my name");
        e.setType(MandrillMessage.Recipient.Type.TO);
        to.add(e);
        mandrillMessage.setTo(to);

        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(mandrillMessage);
        logger.info("s = [{}]",s);
    }


    public static class MandrillMessageMixin{
        @JsonIgnore
        public void setTags(String... tags) {

        }
    }




}
