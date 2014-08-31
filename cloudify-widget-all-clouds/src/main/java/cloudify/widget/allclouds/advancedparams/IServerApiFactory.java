package cloudify.widget.allclouds.advancedparams;

import cloudify.widget.api.clouds.CloudProvider;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.cli.ICloudBootstrapDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/13/14
 * Time: 5:54 PM
 */
public interface IServerApiFactory {

    public CloudServerApi advancedParamsToServerApi( CloudProvider cloudProvider, String advancedData );

    public IConnectDetails advancedParamsToConnectDetails( CloudProvider cloudProvider, String advancedData );

    public ICloudBootstrapDetails createCloudBootstrapDetails( CloudProvider cloudProvider );
}
