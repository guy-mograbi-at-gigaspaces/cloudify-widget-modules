package cloudify.widget.pool.manager.settings;

import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.api.clouds.MachineOptions;
import cloudify.widget.hpcloudcompute.HpCloudComputeConnectDetails;
import cloudify.widget.hpcloudcompute.HpCloudComputeMachineOptions;
import cloudify.widget.softlayer.SoftlayerConnectDetails;
import cloudify.widget.softlayer.SoftlayerMachineOptions;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * User: eliranm
 * Date: 2/24/14
 * Time: 5:14 PM
 */
public class SettingsLoader {

    private static Logger logger = LoggerFactory.getLogger(SettingsLoader.class);

    private static String jsonInput;

//    private SettingsFileLoader settingsFileLoader;


    /* JSON model */

    public static class Settings {

        public Collection<Pool> pools;
    }

    public static class Pool {

        public String id;
        public String authKey;
        public int maxNodes;
        public int minNodes;
        public Provider provider;

    }

    @JsonDeserialize(using = MyJsonDeserializer.class)
    public static class Provider {

        public String name;
        public IConnectDetails connectDetails;
        public MachineOptions machineOptions;
    }


    public static class MyJsonDeserializer extends JsonDeserializer<Provider> {
        @Override
        public Provider deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

            Provider provider = null;

            while (jp.hasCurrentToken()) {

                String text = jp.getText();
                if ("{".equals(text)) { // parsing for provider starts now
                    provider = new Provider();
                } else if ("}".equals(text)) { // we parsed the entire provider object! abort! abort!
                    break;
                } else if ("name".equals(jp.getCurrentName())) {
                    provider.name = jp.nextTextValue();
                } else if ("connectDetails".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    IConnectDetails connectDetails = null;
                    // name is already parsed, check it and decide on the required implementations
                    if ("hp".equals(provider.name)) {
                        connectDetails = jp.readValueAs(HpCloudComputeConnectDetails.class);
                    } else if ("softlayer".equals(provider.name)) {
                        connectDetails = jp.readValueAs(SoftlayerConnectDetails.class);
                    }
                    provider.connectDetails = connectDetails;
                } else if ("machineOptions".equals(text)) {
                    jp.nextToken();
                    MachineOptions machineOptions = null;
                    if ("hp".equals(provider.name)) {
                        machineOptions = jp.readValueAs(HpCloudComputeMachineOptions.class);
                    } else if ("softlayer".equals(provider.name)) {
                        machineOptions = jp.readValueAs(SoftlayerMachineOptions.class);
                    }
                    provider.machineOptions = machineOptions;
                }

                jp.nextToken();
            }


            return provider;
        }
    }


    public static void main(String[] args) {


        MyJsonDeserializer deserializer = new MyJsonDeserializer();

        SimpleModule module =
                new SimpleModule("MyDeserializerModule", new Version(1, 0, 0, null));
        module.addDeserializer(Provider.class, deserializer);

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(module);

        try {

            // TODO extract to SettingsFileLoadHandler
            InputStream src = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/dev/manager-settings.json");

            Settings settings = mapper.readValue(src, Settings.class);

//            logger.info("settings json is\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings.toString()));
//            logger.info("settings json is\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings));

/*
            for (Pool pool : settings.pools) {
                Provider provider = pool.provider;
                logger.info("[{}] provider", provider.name);
                IConnectDetails connectDetails = provider.connectDetails;
                if (connectDetails != null) {
                    logger.info("connectDetails: [{}]", connectDetails.getClass());
                }
            }
*/

//            MachineOptions machineOptions = settings.pools.iterator().next().provider.machineOptions;
//            if (machineOptions != null) {
//                logger.info("first pool -> provider -> machineOptions type is [{}]", machineOptions.getClass());
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
