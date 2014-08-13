package cloudify.widget.allclouds.advancedparams;

import cloudify.widget.api.clouds.CloudProvider;
import cloudify.widget.api.clouds.CloudServerApi;
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

        switch( cloudProviderByLabel ){

            case HP: {
                return createHpcloudServerApi(advancedParams);
            }
            case AWS_EC2: {
                return createEc2CloudServerApi(advancedParams);
            }
            case SOFTLAYER: {
                return createSoftlayerServerapi(advancedParams);
            }
            case NA: {
                throw new RuntimeException("cloud provider NA is unsupported. ");
            }
            default: {
                throw new RuntimeException("it seems we forgot to add support for this cloud provider.. :( ");
            }
        }

    }


    public CloudServerApi createSoftlayerServerapi( AdvancedParams advancedParams ){
        CloudServerApi cloudServerApi = new SoftlayerCloudServerApi();
        SoftlayerConnectDetails connectDetails = new SoftlayerConnectDetails();
        connectDetails.setKey( advancedParams.params.get("apiKey"));
        connectDetails.setUsername(advancedParams.params.get("username"));
        connectDetails.setNetworkId("274");
        connectDetails.isApiKey = true;
        cloudServerApi.connect( connectDetails );
        return cloudServerApi;
    }

    public CloudServerApi createHpcloudServerApi( AdvancedParams advancedParams ){
        throw new UnsupportedOperationException("we stopped supporting hp cloud for now");
    }

    public CloudServerApi createEc2CloudServerApi( AdvancedParams advancedParams ){
        throw new UnsupportedOperationException("this feature is still being developed");
    }
}
