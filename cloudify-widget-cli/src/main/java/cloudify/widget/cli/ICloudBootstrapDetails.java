package cloudify.widget.cli;

import java.io.File;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 2:00 PM
 */
public interface ICloudBootstrapDetails {

    public void setCloudDirectory (String cloudDirectoryAbsolutePath);

    public String getCloudDirectory( );

    public void setCloudPropertiesFile( File propertiesFile );

    public File  getCloudPropertiesFile( );

    /**
     *
     * @return a collection of properties for cloud driver;
     *
     * The output should be a valid property line we can inject into the cloud provider properties file.
     * You should use the example below to make sure it is valid
     *
     * for example
     * newLines.add("user=" + StringUtils.wrapWithQuotes(userId));
     *
     */
    public Collection<String> getProperties( );
}
