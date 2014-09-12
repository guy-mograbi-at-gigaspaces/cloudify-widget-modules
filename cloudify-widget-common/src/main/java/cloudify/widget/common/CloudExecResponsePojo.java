package cloudify.widget.common;

import cloudify.widget.api.clouds.CloudExecResponse;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/4/14
 * Time: 3:11 PM
 */
public class CloudExecResponsePojo implements CloudExecResponse {

    private String output;

    private int exitStatus;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }
}
