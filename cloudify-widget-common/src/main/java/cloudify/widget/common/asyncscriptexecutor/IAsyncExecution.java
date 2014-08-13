package cloudify.widget.common.asyncscriptexecutor;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/11/14
 * Time: 5:11 PM
 */
public interface IAsyncExecution {
    public void setDetails(IAsyncExecutionDetails details);

    public void writeTask( ExecuteData data );

    public String getOutput();

    public List<String> getOutputAsList();

    public AsyncExecutionStatus getStatus();

    public boolean isFinished();


}
