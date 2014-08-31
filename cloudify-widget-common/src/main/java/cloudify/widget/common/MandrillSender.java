package cloudify.widget.common;

import cloudify.widget.api.clouds.IEmailDetails;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/26/14
 * Time: 7:28 PM
 */
public class MandrillSender {

    private static Logger logger = LoggerFactory.getLogger(MandrillSender.class);

    public void sendEmail(IEmailDetails emailDetails) {
        String mandrillMessageStatusesStr = null;
        try {
            if (emailDetails instanceof MandrillEmailDetails) {
                MandrillEmailDetails details = (MandrillEmailDetails) emailDetails;
                MandrillApi mandrillApi = new MandrillApi(details.mandrillApiKey);
                MandrillMessageStatus mandrillMessageStatuses= mandrillApi.messages().sendTemplate(details.templateName, details.templateContent, details.mandrillMessage, details.async)[0];
                if ("invalid".equalsIgnoreCase(mandrillMessageStatuses.getStatus()) || "rejected".equalsIgnoreCase(mandrillMessageStatuses.getStatus())) {
                    mandrillMessageStatusesStr = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(mandrillMessageStatuses);
                }
            }
        }catch(Exception e){
            throw new RuntimeException("unable to send mandrill message : " ,e);
        }
        if ( mandrillMessageStatusesStr != null ){
            throw new RuntimeException("error sending email with mandrill : " + mandrillMessageStatusesStr);
        }
    }



    public static class MandrillEmailDetails implements IEmailDetails{
        public String mandrillApiKey;
        public String templateName;
        public Map<String,String> templateContent;
        public MandrillMessage mandrillMessage;
        public boolean async;


        @Override
        public String toString() {
            try {
                return "MandrillEmailDetails{" +
                        "templateName='" + templateName + '\'' +
                        ", async=" + async +
                        '}';
            }catch(Exception e){
                logger.error("unable to transform to string",e);
                return "ERROR :: " + e.getMessage() ;
            }
        }
    }
}
