package cloudify.widget.allclouds.executiondata;

import cloudify.widget.allclouds.advancedparams.IServerApiFactory;
import cloudify.widget.api.clouds.CloudProvider;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.cli.ICloudBootstrapDetails;
import cloudify.widget.common.StringUtils;
import cloudify.widget.ec2.executiondata.AwsEc2ExecutionModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/25/14
 * Time: 2:35 PM
 */
public class ExecutionDataModel {

    private static Logger logger = LoggerFactory.getLogger(ExecutionDataModel.class);
    private IServerApiFactory serverApiFactory;

    private String raw;
    private String encryptionKey;

    public static enum JsonKeys {
        ADVANCED_DATA("advancedData"),
        RECIPE_PROPERTIES("recipeProperties"),
        LOGIN_DETAILS("loginDetails"),
        LEAD_DETAILS("leadDetails"),
        AWS_EC2_EXECUTION_DETAILS("ec2ExecutionDetails");

        public String value;

        JsonKeys ( String value ){
            this.value = value;
        }
    }


    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String encrypt(){
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(encryptionKey);
        return encryptor.encrypt(raw);
    }

    public void decrypt( String encrypted ){
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(encryptionKey);
        raw = encryptor.decrypt(encrypted);
    }


    private JsonNode asJson(){
        try {
            String toParse = raw;
            if ( StringUtils.isEmptyOrSpaces(raw)) {
                toParse = "{}";
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(toParse);

        }
        catch (Exception e){
            throw new RuntimeException("unable to read execution data",e);
        }
    }




    private boolean hasKey( JsonNode jsonNode, String keyName ){
        return jsonNode != null && jsonNode.has(keyName) && !StringUtils.isEmptyOrSpaces(jsonNode.get(keyName).toString());
    }


    public boolean has( JsonKeys key ){ return hasKey( asJson(), key.value ); }

    public AwsEc2ExecutionModel getAwsEc2ExecutionModel(){
        if ( has ( JsonKeys.AWS_EC2_EXECUTION_DETAILS) ){
            return getFieldAsClass(JsonKeys.AWS_EC2_EXECUTION_DETAILS,AwsEc2ExecutionModel.class);
        }else{
            return null;
        }
    }


    public String getFieldAsString( JsonKeys jsonKey ){
        return getFieldAsJson(jsonKey).toString();
    }

    public JsonNode getFieldAsJson ( JsonKeys jsonKey ){ return asJson().get(jsonKey.value); }

    private <T> T getFieldAsClass( JsonKeys jsonKey, Class<T> clzz ){
        try{
            return new ObjectMapper().readValue( getFieldAsString(jsonKey), clzz);
        }catch(Exception e){
            throw new RuntimeException("unable to read execution data for class [" + clzz +"]", e );
        }
    }

    public IConnectDetails advancedDataToConnectDetails( CloudProvider cloudProvider ){
        return serverApiFactory.advancedParamsToConnectDetails( cloudProvider, getFieldAsString(JsonKeys.ADVANCED_DATA));
    }


    public LoginDetails getLoginDetails(){
        try {
            return new ObjectMapper().readValue(getFieldAsString(JsonKeys.LOGIN_DETAILS), LoginDetails.class);
        }catch(Exception e){
            throw new RuntimeException("unable to get login details",e);
        }
    }

    public CloudServerApi advancedDataToCloudServerApi( CloudProvider cloudProvider ){
        return serverApiFactory.advancedParamsToServerApi( cloudProvider, getFieldAsString(JsonKeys.ADVANCED_DATA));
    }

    public ICloudBootstrapDetails getCloudBootstrapDetails( CloudProvider cloudProvider ){
        try {
            ICloudBootstrapDetails cloudBootstrapDetails = serverApiFactory.createCloudBootstrapDetails(cloudProvider);
            ObjectMapper mapper = new ObjectMapper();
            mapper.readerForUpdating(cloudBootstrapDetails).readValue(asJson().get(JsonKeys.ADVANCED_DATA.value).get("params"));
            return cloudBootstrapDetails;
        }catch(UnsupportedOperationException e){
            throw e;
        }catch(Exception e){
            logger.info("unable to parse json [{}]", asJson().get(JsonKeys.ADVANCED_DATA.value));
            throw new RuntimeException("unable to get cloud bootstrap details",e);
        }
    }

    public IServerApiFactory getServerApiFactory() {
        return serverApiFactory;
    }



    public void setServerApiFactory(IServerApiFactory serverApiFactory) {
        this.serverApiFactory = serverApiFactory;
    }

    public static class LoginDetails{
        public String userId;
        public String email;
        public String name;
        public String lastName;
    }
}
