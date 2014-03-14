package cloudify.widget.common;

import cloudify.widget.api.clouds.IWidgetLoginDetails;
import cloudify.widget.api.clouds.IWidgetLoginHandler;
//import com.ecwid.mailchimp.MailChimpClient;
//import com.ecwid.mailchimp.MailChimpException;
//import com.ecwid.mailchimp.MailChimpMethod;
//import com.ecwid.mailchimp.method.v1_3.list.EmailType;
//import com.ecwid.mailchimp.method.v1_3.list.ListInformation;
//import com.ecwid.mailchimp.method.v1_3.list.ListMembersMethod;
//import com.ecwid.mailchimp.method.v1_3.list.ListMembersResult;
//import com.ecwid.mailchimp.method.v1_3.list.ListSubscribeMethod;
//import com.ecwid.mailchimp.method.v1_3.list.ListsMethod;
//import com.ecwid.mailchimp.method.v1_3.list.ListsResult;
//import com.ecwid.mailchimp.method.v1_3.list.ShortMemberInfo;
//import com.ecwid.mailchimp.method.v2_0.lists.MemberInfoMethod;
import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.method.v1_3.list.EmailType;
import com.ecwid.mailchimp.method.v1_3.list.ListInformation;
import com.ecwid.mailchimp.method.v1_3.list.ListMembersMethod;
import com.ecwid.mailchimp.method.v1_3.list.ListMembersResult;
import com.ecwid.mailchimp.method.v1_3.list.ListSubscribeMethod;
import com.ecwid.mailchimp.method.v1_3.list.ListsMethod;
import com.ecwid.mailchimp.method.v1_3.list.ListsResult;
import com.ecwid.mailchimp.method.v1_3.list.ShortMemberInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 3/6/14
 * Time: 5:05 PM
 */
public class MailChimpWidgetLoginHandler implements IWidgetLoginHandler{

    private static Logger logger = LoggerFactory.getLogger(MailChimpWidgetLoginHandler.class);
    MailChimpClient mailChimpClient =  new MailChimpClient();
    private String apiKey  = null;
    private String listId = null;

    @Override
    public void handleWidgetLogin(IWidgetLoginDetails widgetLoginDetails) {

        logger.info("sending email [" + widgetLoginDetails.getEmail() + "] to mailchimp");

        if ( StringUtils.isEmptyOrSpaces(apiKey) || StringUtils.isEmptyOrSpaces(listId) ){
            logger.error("unable to send email to mailchimp because bean not initialized properly. [{}] :: [{}]", apiKey, listId);
            return;
        }

        ListSubscribeMethod listSubscribeMethod = new ListSubscribeMethod();
        listSubscribeMethod.apikey = apiKey;
        listSubscribeMethod.update_existing = true;
        listSubscribeMethod.double_optin = false;
        listSubscribeMethod.email_address = widgetLoginDetails.getEmail();
        listSubscribeMethod.update_existing = true;
        listSubscribeMethod.email_type = EmailType.html;
        listSubscribeMethod.replace_interests = false;
        listSubscribeMethod.send_welcome = false;
        listSubscribeMethod.id = listId;

        try{
            mailChimpClient.execute(listSubscribeMethod);
        }catch(Exception e){
            logger.error("unable to subscribe", e);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }
}
