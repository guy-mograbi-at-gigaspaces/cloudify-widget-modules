package cloudify.widget.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetUserResult;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/11/14
 * Time: 1:53 PM
 *
 *
 * retrieves API credentials, and retrieves the account id.
 *
 */
public class Ec2AccountIdReader {

    // this is the recommended method of getting the accountId
    // see: https://forums.aws.amazon.com/message.jspa?messageID=252875
    public String getAccountId( String apiKey, String apiSecretKey ){
        final AWSCredentials credentials = new BasicAWSCredentials(apiKey, apiSecretKey);
        AmazonIdentityManagementClient client = new AmazonIdentityManagementClient(credentials);
        GetUserResult user = client.getUser();
        String arn = user.getUser().getArn();
        String accountId = arn.split("::")[1].split(":")[0];
        return accountId;
    }
}
