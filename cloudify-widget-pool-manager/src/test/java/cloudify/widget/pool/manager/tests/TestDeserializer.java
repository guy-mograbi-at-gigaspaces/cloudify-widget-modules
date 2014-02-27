package cloudify.widget.pool.manager.tests;

import cloudify.widget.hpcloudcompute.HpCloudComputeConnectDetails;
import cloudify.widget.hpcloudcompute.HpCloudComputeMachineOptions;
import cloudify.widget.softlayer.SoftlayerConnectDetails;
import cloudify.widget.softlayer.SoftlayerMachineOptions;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * User: eliranm
 * Date: 2/26/14
 * Time: 4:51 PM
 */
public class TestDeserializer {

    private static Logger logger = LoggerFactory.getLogger(TestDeserializer.class);

    @Test
    public void testDeserializer() throws IOException {
        ClassPathResource resource = new ClassPathResource("conf/dev/manager-settings.json");
        String content = FileUtils.readFileToString(resource.getFile());
        logger.info("content [{}]", content);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Settings settings = mapper.readValue(content, Settings.class);
        logger.info("settings [{}]", settings);
    }

    public static class Settings {
        public List<PoolSettings> pools = new LinkedList<PoolSettings>();

        public List<PoolSettings> getPools() {
            return pools;
        }

        public void setPools(List<PoolSettings> pools) {
            this.pools = pools;
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "pools=" + pools +
                    '}';
        }
    }

    public static class PoolSettings {
        public ProviderSettings provider;

        public ProviderSettings getProvider() {
            return provider;
        }

        public void setProvider(ProviderSettings provider) {
            this.provider = provider;
        }

        @Override
        public String toString() {
            return "PoolSettings{" +
                    "provider=" + provider +
                    '}';
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "name")
    @JsonSubTypes({@JsonSubTypes.Type(value = HpProviderSettings.class, name = "hp"),
            @JsonSubTypes.Type(value = SoftlayerProviderSettings.class, name = "softlayer")})
    public static class ProviderSettings {
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ProviderSettings{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }


    public static class HpProviderSettings extends ProviderSettings {
        public HpCloudComputeConnectDetails connectDetails;
        public HpCloudComputeMachineOptions machineOptions;

        public HpCloudComputeConnectDetails getConnectDetails() {
            return connectDetails;
        }

        public void setConnectDetails(HpCloudComputeConnectDetails connectDetails) {
            this.connectDetails = connectDetails;
        }

        public HpCloudComputeMachineOptions getMachineOptions() {
            return machineOptions;
        }

        public void setMachineOptions(HpCloudComputeMachineOptions machineOptions) {
            this.machineOptions = machineOptions;
        }

        @Override
        public String toString() {
            return "HpProviderSettings{" +
                    "connectDetails=" + connectDetails +
                    ", machineOptions=" + machineOptions +
                    '}';
        }
    }

    public static class SoftlayerProviderSettings extends ProviderSettings {
        public SoftlayerConnectDetails connectDetails;
        public SoftlayerMachineOptions machineOptions;

        public SoftlayerConnectDetails getConnectDetails() {
            return connectDetails;
        }

        public void setConnectDetails(SoftlayerConnectDetails connectDetails) {
            this.connectDetails = connectDetails;
        }

        public SoftlayerMachineOptions getMachineOptions() {
            return machineOptions;
        }

        public void setMachineOptions(SoftlayerMachineOptions machineOptions) {
            this.machineOptions = machineOptions;
        }

        @Override
        public String toString() {
            return "SoftlayerProviderSettings{" +
                    "connectDetails=" + connectDetails +
                    ", machineOptions=" + machineOptions +
                    '}';
        }
    }


}
