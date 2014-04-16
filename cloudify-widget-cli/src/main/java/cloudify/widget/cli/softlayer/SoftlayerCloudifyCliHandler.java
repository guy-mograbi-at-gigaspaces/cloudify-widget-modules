package cloudify.widget.cli.softlayer;

import cloudify.widget.cli.ICloudBootstrapDetails;
import cloudify.widget.cli.ICloudifyCliHandler;
import cloudify.widget.common.StringUtils;
import org.apache.commons.io.FileUtils;

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

    private String cloudifyHomeDir;

    private String cloudifyCloudsFolderRelativePath = "clouds";

    private String cloudPropertiesFilenameSuffix = "-cloud.properties";

    @Override
    public File createNewCloud(ICloudBootstrapDetails details) {


        if ( ! ( details instanceof SoftlayerCloudBootstrapDetails ) ){
            throw new RuntimeException("expected SoftlayerBootstrapDetails implementation");
        }

        SoftlayerCloudBootstrapDetails bootstrapDetails = (SoftlayerCloudBootstrapDetails) details ;
        String cloudifyCloudFoldername = bootstrapDetails.driverName;
        String userId = bootstrapDetails.getUsername();
        String secretKey = bootstrapDetails.getApiKey();

        File cloudsFolder = new File(cloudifyHomeDir, cloudifyCloudsFolderRelativePath);
        File origFolder = new File(cloudsFolder, cloudifyCloudFoldername);
        File destFolder = new File(cloudsFolder, cloudifyCloudFoldername + System.currentTimeMillis());


        try {
            FileUtils.copyDirectory(origFolder, destFolder);
            File propertiesFile = new File(destFolder, cloudifyCloudFoldername + cloudPropertiesFilenameSuffix);

            // GUY - Important - Note - Even though this is the "properties" files, it is not used for "properties" per say
            // we are actually writing a groovy file that defines variables.
            Collection<String> newLines = new LinkedList<String>();
            newLines.add("");
            newLines.add("user=" + StringUtils.wrapWithQuotes(userId));
            newLines.add("apiKey=" + StringUtils.wrapWithQuotes(secretKey));
            FileUtils.writeLines(propertiesFile, newLines, true);

            return destFolder;
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

    public String getCloudifyHomeDir() {
        return cloudifyHomeDir;
    }

    public void setCloudifyHomeDir(String cloudifyHomeDir) {
        this.cloudifyHomeDir = cloudifyHomeDir;
    }

    public String getCloudifyCloudsFolderRelativePath() {
        return cloudifyCloudsFolderRelativePath;
    }

    public void setCloudifyCloudsFolderRelativePath(String cloudifyCloudsFolderRelativePath) {
        this.cloudifyCloudsFolderRelativePath = cloudifyCloudsFolderRelativePath;
    }

    public String getCloudPropertiesFilenameSuffix() {
        return cloudPropertiesFilenameSuffix;
    }

    public void setCloudPropertiesFilenameSuffix(String cloudPropertiesFilenameSuffix) {
        this.cloudPropertiesFilenameSuffix = cloudPropertiesFilenameSuffix;
    }

}
