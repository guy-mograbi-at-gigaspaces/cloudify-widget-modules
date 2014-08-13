package cloudify.widget.cli;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 2:01 PM
 */
public interface ICloudifyCliHandler {

    /**
     * copies a cloud folder and prints the properties in CloudBootstrapDetails
     * @param details -
     * @return new cloud folder
     */
    public void writeBootstrapProperties( ICloudBootstrapDetails details );

    /**
     * Runs a command in the background
     * @param details -
     */
    public void runCommandAsync( ICloudBootstrapDetails details );

    /**
     * gets the output from the background command
     * @param details -
     * @return -
     */
    public String getOutput( ICloudBootstrapDetails details );



}
