package cloudify.widget.allclouds.advancedparams;

import cloudify.widget.api.clouds.CloudProvider;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.cli.ICloudBootstrapDetails;
import cloudify.widget.cli.softlayer.SoftlayerCloudBootstrapDetails;
import cloudify.widget.softlayer.SoftlayerCloudServerApi;
import cloudify.widget.softlayer.SoftlayerConnectDetails;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/13/14
 * Time: 5:20 PM
 */
public class ServerApiFactoryImpl implements IServerApiFactory {
    private static Logger logger = LoggerFactory.getLogger(ServerApiFactoryImpl.class);

    @Override
    public CloudServerApi advancedParamsToServerApi( CloudProvider cloudProvider, String advancedData ){
        logger.info("creating cloud server api from advanced params");
        CloudServerApi cloudServerApi = create(cloudProvider);
        IConnectDetails iConnectDetails = advancedParamsToConnectDetails(cloudProvider, advancedData);
        initialize(cloudServerApi, iConnectDetails);
        return cloudServerApi;
    }

    private CloudServerApi create( CloudProvider  provider ){
        switch(provider){
            case SOFTLAYER: {
                return new SoftlayerCloudServerApi();
            }
            default: {
                throw new UnsupportedOperationException("provider [" + provider + "] is unsupported");
            }
        }

    }

    public CloudServerApi initialize( CloudServerApi cloudServerApi, IConnectDetails connectDetails ){
        cloudServerApi.connect( connectDetails );
        return cloudServerApi;
    }

    public IConnectDetails createSoftlayerConnectDetails( AdvancedParams advancedParams ){
        SoftlayerConnectDetails connectDetails = new SoftlayerConnectDetails();
        connectDetails.setKey( advancedParams.params.get("apiKey"));
        connectDetails.setUsername(advancedParams.params.get("username"));
        connectDetails.setNetworkId("274");
        connectDetails.isApiKey = true;
        return connectDetails;
    }

    public CloudServerApi createHpcloudServerApi( AdvancedParams advancedParams ){
        throw new UnsupportedOperationException("we stopped supporting hp cloud for now");
    }

    public CloudServerApi createEc2CloudServerApi( AdvancedParams advancedParams ){
        throw new UnsupportedOperationException("this feature is still being developed");
    }

    @Override
    public IConnectDetails advancedParamsToConnectDetails(CloudProvider cloudProvider, String advancedData) {

        ObjectMapper objectMapper = new ObjectMapper();
        AdvancedParams advancedParams = null;
        try {
            advancedParams = objectMapper.readValue(advancedData, AdvancedParams.class);
        } catch (IOException e) {
            throw new RuntimeException("unable to bind advancedData string to object",e);
        }


        CloudProvider cloudProviderByLabel = CloudProvider.findByLabel(advancedParams.type);
        if ( cloudProviderByLabel != cloudProvider ){
            throw new RuntimeException("cloudProvider does not match advancedParams.type. This probably means the widget and its display are not sychronized. please fix widget configuration");
        }


        switch(cloudProvider){
            case SOFTLAYER: {
                return createSoftlayerConnectDetails( advancedParams );
            }

            default:{
                throw new UnsupportedOperationException("cloud provider [" + cloudProvider + "] is not supported");
            }
        }
    }

    @Override
    public ICloudBootstrapDetails createCloudBootstrapDetails(CloudProvider cloudProvider) {
            ICloudBootstrapDetails result = null;
            switch( cloudProvider ){
                case SOFTLAYER: {
                    result = new SoftlayerCloudBootstrapDetails();
                    break;
                }
                default:
                    throw new UnsupportedOperationException("cloud provider [" + cloudProvider + "] is not supported ");
            }
            return result;
    }
}
