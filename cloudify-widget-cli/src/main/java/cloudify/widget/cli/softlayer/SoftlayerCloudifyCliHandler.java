package cloudify.widget.cli.softlayer;

import cloudify.widget.cli.ICloudBootstrapDetails;
import cloudify.widget.cli.ICloudifyCliHandler;
import cloudify.widget.common.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/13/14
 * Time: 1:00 PM
 */
public class SoftlayerCloudifyCliHandler implements ICloudifyCliHandler {

    private static Logger logger = LoggerFactory.getLogger(SoftlayerCloudifyCliHandler.class);

    @Override
    public void writeBootstrapProperties(ICloudBootstrapDetails details) {
        logger.info("creating new cloud for [{}]", details);
        if ( ! ( details instanceof SoftlayerCloudBootstrapDetails ) ){
            throw new RuntimeException("expected SoftlayerBootstrapDetails implementation");
        }

        try {
            File propertiesFile = details.getCloudPropertiesFile();

            // GUY - Important - Note - Even though this is the "properties" files, it is not used for "properties" per say
            // we are actually writing a groovy file that defines variables.
            Collection<String> newLines = new LinkedList<String>();
            newLines.add("");

            newLines.addAll( details.getProperties() );

            FileUtils.writeLines(propertiesFile, newLines, true);
        } catch (Exception e) {
            throw new RuntimeException(String.format("error while writing cloud properties"), e);
        }
    }

    @Override
    public void runCommandAsync(ICloudBootstrapDetails details) {

    }

    @Override
    public String getOutput(ICloudBootstrapDetails details) {
        return null;
    }

}
