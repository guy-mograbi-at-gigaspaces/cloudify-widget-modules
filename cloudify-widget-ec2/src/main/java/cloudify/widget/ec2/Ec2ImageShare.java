package cloudify.widget.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.LaunchPermission;
import com.amazonaws.services.ec2.model.LaunchPermissionModifications;
import com.amazonaws.services.ec2.model.ModifyImageAttributeRequest;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 7/29/14
 * Time: 11:59 AM
 */
public class Ec2ImageShare {



    public static enum Operation{
        ADD, REMOVE
    }

    private AmazonEC2 getClient( String user, String apiKey ){
        final AWSCredentials credentials = new BasicAWSCredentials(user, apiKey);
        return new AmazonEC2Client(credentials);
    }


    private LaunchPermissionModifications getLaunchPermissionMod( String account ){
        // adding launch permissions to request
        LaunchPermissionModifications launchPermissionMod = new LaunchPermissionModifications();
        LaunchPermission lp = new LaunchPermission();
        lp.withUserId(account);
        Collection<LaunchPermission> lpList = new ArrayList<LaunchPermission>();
        lpList.add(lp);
        launchPermissionMod.setAdd(lpList);
        return launchPermissionMod;
    }


    /**
     * adds/removes image permissions
     *
     *
     * @param user - the user that has the image right now
     * @param apiKey - apiKey for user
     * @param imageId - the image in question
     * @param operation - add/remove
     * @param account - the account we want to add/remove permissions
     */
    private void setPermissions(final String user,
                                final String apiKey,
                                final String imageId,
                                final Operation operation,
                                final String account) {
        // init the aws ec2 client.
        AmazonEC2 ec2 = getClient(user, apiKey);


        // create modify permissions request
        ModifyImageAttributeRequest modImgAttrRequest = new ModifyImageAttributeRequest();
        modImgAttrRequest.setImageId(imageId);
        modImgAttrRequest.setAttribute("launchPermission");
        modImgAttrRequest.setOperationType(operation.name().toLowerCase());


        LaunchPermissionModifications launchPermissionMod = getLaunchPermissionMod(account);
        modImgAttrRequest.setLaunchPermission(launchPermissionMod);


        // invoke request.
        ec2.modifyImageAttribute(modImgAttrRequest);
    }
}
