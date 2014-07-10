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
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpObject;
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
import java.util.List;

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

        MailChimpLoginDetails loginDetails = null;
        if (widgetLoginDetails instanceof MailChimpLoginDetails) {
            loginDetails = (MailChimpLoginDetails) widgetLoginDetails;
            logger.info("got mailchimp login details");

        }else{
            logger.info("not mailchimp login details. skipping");
            return;
        }

        logger.info("sending email [{}] to mailchimp", loginDetails.getEmail() );

        if ( StringUtils.isEmptyOrSpaces(apiKey) || StringUtils.isEmptyOrSpaces(listId) ){
            logger.error("unable to send email to mailchimp because bean not initialized properly. [{}] :: [{}]", apiKey, listId);
            return;
        }else{
            logger.info("using [{}] :: [{}] as api credentials/ list id ", apiKey, listId);
        }

        ListSubscribeMethod listSubscribeMethod = new ListSubscribeMethod();
        listSubscribeMethod.apikey = apiKey;
        listSubscribeMethod.update_existing = true;
        listSubscribeMethod.double_optin = false;
        listSubscribeMethod.email_address = loginDetails.getEmail();
        listSubscribeMethod.update_existing = true;
        listSubscribeMethod.email_type = EmailType.html;
        listSubscribeMethod.merge_vars = new MailChimpObject();
        listSubscribeMethod.merge_vars.put("NAME", loginDetails.getFirstName());
        listSubscribeMethod.merge_vars.put("LASTNAME", loginDetails.getLastName());

        listSubscribeMethod.replace_interests = false;
        listSubscribeMethod.send_welcome = false;
        listSubscribeMethod.id = listId;

        try{
            mailChimpClient.execute(listSubscribeMethod);
        }catch(Exception e){
            logger.error("unable to subscribe", e);
        }

        logger.info("mail sent successfully");
    }

    public static void main(String[] args) throws IOException, MailChimpException {
        MailChimpClient mclient = new MailChimpClient();

        MailChimpWidgetLoginHandler loginHandler = new MailChimpWidgetLoginHandler();
        String apiKey = System.getProperty("apiKey");
        String listId = System.getProperty("listId");

        loginHandler.setApiKey(apiKey);
        loginHandler.setListId(listId);
//        loginHandler.handleWidgetLogin(new IWidgetLoginDetails() {
//            @Override
//            public String getEmail() {
//                return "someemail@example.com";
//            }
//        });



        ListsMethod method = new ListsMethod();
        method.apikey = apiKey;


        ListsResult execute = mclient.execute(method);
        List<ListInformation> data = execute.data;
        for (ListInformation listInformation : data) {
            logger.info(listInformation.id + " :: " + listInformation.name);
        }



        ListMembersMethod method1 = new ListMembersMethod();
        method1.apikey = apiKey;
        method1.id = listId;

        logger.info("listing members in [{}] ", method1.id);

        ListMembersResult execute1 = mclient.execute(method1);
        for (ShortMemberInfo info : execute1.data) {
            logger.info(info.email);
        }
    }

    public static interface MailChimpLoginDetails extends IWidgetLoginDetails{
        public String getFirstName();
        public String getLastName();
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
