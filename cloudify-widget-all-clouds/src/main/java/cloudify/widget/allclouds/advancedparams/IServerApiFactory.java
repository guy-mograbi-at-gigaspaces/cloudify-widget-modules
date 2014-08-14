package cloudify.widget.allclouds.advancedparams;

import cloudify.widget.api.clouds.CloudProvider;
import cloudify.widget.api.clouds.CloudServerApi;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/13/14
 * Time: 5:54 PM
 */
public interface IServerApiFactory {

    public CloudServerApi advancedParamsToServerApi( CloudProvider cloudProvider, String advancedData );
}
