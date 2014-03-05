package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.ManagerSettings;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * User: eliranm
 * Date: 2/24/14
 * Time: 5:14 PM
 */
public class ManagerSettingsJsonHandler implements ManagerSettingsHandler {

    private static Logger logger = LoggerFactory.getLogger(ManagerSettingsJsonHandler.class);

    private ManagerSettingsFileLoader managerSettingsFileLoader;


    @Override
    public ManagerSettings read() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ManagerSettings managerSettings = null;
        try {
            managerSettings = mapper.readValue(managerSettingsFileLoader.asString(), ManagerSettings.class);
            logger.info("manager settings\n[{}]", mapper.writeValueAsString(managerSettings));
        } catch (IOException e) {
            logger.error(String.format("failed to read manager settings file as json from string [%s]", managerSettingsFileLoader.asString()), e);
        }

        return managerSettings;
    }


    public void setManagerSettingsFileLoader(ManagerSettingsFileLoader managerSettingsFileLoader) {
        this.managerSettingsFileLoader = managerSettingsFileLoader;
    }
}
