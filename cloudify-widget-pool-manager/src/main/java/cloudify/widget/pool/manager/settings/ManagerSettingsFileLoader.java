package cloudify.widget.pool.manager.settings;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * User: eliranm
 * Date: 2/27/14
 * Time: 3:33 PM
 */
public class ManagerSettingsFileLoader {

    private static Logger logger = LoggerFactory.getLogger(ManagerSettingsFileLoader.class);

    private String managerSettingsFilePath;

    private String content;


    public String asString() {
        if (content == null) {
            content = fromFile();
        }
        return content;
    }

    private String fromFile() {
        if (managerSettingsFilePath == null) {
            throw new RuntimeException("manager settings file not configured, please fix configuration for managerSettingsFilePath");
        }

        ClassPathResource resource = new ClassPathResource(managerSettingsFilePath);
        String content = null;
        try {
            content = FileUtils.readFileToString(resource.getFile());
        } catch (IOException e) {
            logger.error(String.format("failed to read manager settings file from path [%s]", managerSettingsFilePath), e);
        }
        logger.debug("manager settings file read, content is [{}]", content);
        return content;
    }

    public void setManagerSettingsFilePath(String managerSettingsFilePath) {
        this.managerSettingsFilePath = managerSettingsFilePath;
    }

}
