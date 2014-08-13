package cloudify.widget.common.asyncscriptexecutor;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/11/14
 * Time: 5:11 PM
 */
public interface IAsyncExecutionDetails {

    public File getNewScriptsDir();

    public void setNewScriptsDir( File dir );

    public File getTaskFile();

    public void setTaskFile( File file );

    public void setOutputFile( File file );

    public File getOutputFile();

    public void setStatusFile( File file );

    public File getStatusFile( );
}
