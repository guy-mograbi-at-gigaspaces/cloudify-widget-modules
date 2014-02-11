package cloudify.widget.cli;

import cloudify.widget.api.clouds.ServerIp;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 2:04 PM
 */
public interface ICloudBootstrapOutput {

    public String getManagementIp();

    public boolean isDone();

    public boolean isSuccess();

    public boolean isError();

    public String getErrorMessage();
}
