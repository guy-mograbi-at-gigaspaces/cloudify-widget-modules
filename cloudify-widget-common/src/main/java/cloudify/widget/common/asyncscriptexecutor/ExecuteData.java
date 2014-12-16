package cloudify.widget.common.asyncscriptexecutor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/11/14
 * Time: 5:23 PM
 *
 *
 *
 *  {"arguments":"173.192.204.42,/usr/lib/cloudify-widget/recipes/17cbea0a-c577-449b-948b-8408363053b5/3215907096270093/solo/apps/blustratus-chef/blustratus-single,install-service,blusolo",
 *  "executable":"/usr/lib/cloudify-widget/bin/deployer.sh",
 *  "serverNodeId":"2285","cloudifyHome":"/usr/lib/cloudify-widget/cloudify-folder"}
 *
 */
public class ExecuteData {

    public List<String> arguments = new LinkedList<String>();

    public String executable;

    public String serverNodeId;

    public String cloudifyHome;

    public boolean sendEmail;

    public String managerIp;

    public String serviceName;

    public String applicationName;

    public String action;

    public String privateKey; // in case there's a private key

    public String bcc;


    public MandrillDetails mandrill = new MandrillDetails();


    public static class MandrillDetails{

        public String apiKey;

        public String templateName;

        public MandrillData data;

        public List<MandrillEmailAddressItem> to = new LinkedList<MandrillEmailAddressItem>();


    }

    // placeholders in the template
    public static class MandrillData{
        public MandrillDataItem name = new MandrillDataItem("name");
        public MandrillDataItem firstName = new MandrillDataItem("firstName");
        public MandrillDataItem lastName = new MandrillDataItem("lastName");
        public MandrillDataItem link = new MandrillDataItem("link");
        public MandrillDataItem linkTitle = new MandrillDataItem("linkTitle");
        public MandrillDataItem publicIp = new MandrillDataItem("publicIp");

    }

    public static class MandrillDataItem{
        public String name;
        public String content;

        public MandrillDataItem(String name) {
            this.name = name;
        }

        public MandrillDataItem(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }



    public static class MandrillEmailAddressItem{
        public String email;
        public String name;
        public String type;

        public MandrillEmailAddressItem(String email, String name, String type) {
            this.email = email;
            this.name = name;
            this.type = type;
        }


    }
}
