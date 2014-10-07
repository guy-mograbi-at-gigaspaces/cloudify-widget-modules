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

    public String getAccountId( String apiKey, String apiSecretKey ){
        final AWSCredentials credentials = new BasicAWSCredentials(apiKey, apiSecretKey);
        AmazonIdentityManagementClient client = new AmazonIdentityManagementClient(credentials);
        GetUserResult user = client.getUser();
        return user.getUser().getUserId();
    }
}
