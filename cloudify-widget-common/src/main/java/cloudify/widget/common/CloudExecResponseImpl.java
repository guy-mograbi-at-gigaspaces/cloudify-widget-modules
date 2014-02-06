package cloudify.widget.common;

import cloudify.widget.api.clouds.CloudExecResponse;
import org.jclouds.compute.domain.ExecResponse;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/6/14
 * Time: 2:26 PM
 */
public class CloudExecResponseImpl implements CloudExecResponse {
    private ExecResponse execResponse;

    public CloudExecResponseImpl(ExecResponse execResponse) {
        this.execResponse = execResponse;
    }

    public String getError() {
        return execResponse.getError();
    }

    public String getOutput() {
        return execResponse.getOutput();
    }

    public int getExitStatus() {
        return execResponse.getExitStatus();
    }

    @Override
    public int hashCode() {
        return execResponse.hashCode();
    }

    @Override
    public String toString() {
        return execResponse.toString();
    }
}
