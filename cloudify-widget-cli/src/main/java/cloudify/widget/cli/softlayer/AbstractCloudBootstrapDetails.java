package cloudify.widget.cli.softlayer;

import cloudify.widget.cli.ICloudBootstrapDetails;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 4/1/14
 * Time: 3:24 PM
 */
public abstract class AbstractCloudBootstrapDetails implements ICloudBootstrapDetails {

    public String cloudDirectoryAbsolutePath;
    public File propertiesFile;

    @Override
    public void setCloudDirectory(String cloudDirectoryAbsolutePath) {
        this.cloudDirectoryAbsolutePath = cloudDirectoryAbsolutePath;
    }

    @Override
    public String getCloudDirectory() {
        return cloudDirectoryAbsolutePath;
    }

    @Override
    public void setCloudPropertiesFile(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    @Override
    public File getCloudPropertiesFile() {
        return propertiesFile;
    }
}
