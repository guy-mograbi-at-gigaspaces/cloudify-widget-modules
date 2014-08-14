package cloudify.widget.common.asyncscriptexecutor;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/11/14
 * Time: 5:12 PM
 *
 *
 *
 *  {"arguments":"173.192.204.42,/usr/lib/cloudify-widget/recipes/17cbea0a-c577-449b-948b-8408363053b5/3215907096270093/solo/apps/blustratus-chef/blustratus-single,install-service,blusolo","executable":"/usr/lib/cloudify-widget/bin/deployer.sh","serverNodeId":"2285","cloudifyHome":"/usr/lib/cloudify-widget/cloudify-folder"}
 *
 *
 */
public class AsyncExecutionDetails implements IAsyncExecutionDetails {

    private File newScriptsDir;
    private File taskFile;
    private File outputFile;
    private File statusFile;

    @Override
    public File getNewScriptsDir() {
        return newScriptsDir;
    }

    @Override
    public void setNewScriptsDir(File dir) {
        this.newScriptsDir = dir;
    }

    @Override
    public File getTaskFile() {
        return this.taskFile ;
    }

    @Override
    public void setTaskFile(File file) {
        this.taskFile = file;
    }

    @Override
    public void setOutputFile(File file){
        this.outputFile = file;
    }

    @Override
    public File getOutputFile(){
        return outputFile;
    }

    @Override
    public void setStatusFile(File file) {
        this.statusFile = file;
    }

    @Override
    public File getStatusFile() {
        return statusFile;
    }
}
