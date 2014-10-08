package cloudify.widget.common.asyncscriptexecutor;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/11/14
 * Time: 5:12 PM
 */
public class AsyncExecutionImpl implements IAsyncExecution {

    IAsyncExecutionDetails details;

    private static Logger logger = LoggerFactory.getLogger(AsyncExecutionImpl.class);

    /**
     * write the task to the
     */
    public void writeTask( ExecuteData data ){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String dataStr = mapper.writeValueAsString( data );

            File newScriptsFolder = details.getNewScriptsDir();
            if( !newScriptsFolder.exists() ){
                newScriptsFolder.mkdirs();
            }

            FileUtils.write(details.getTaskFile(), dataStr );

        } catch (IOException e) {
            throw new RuntimeException("unable to write data to file",e);
        }
    }

    public boolean isFinished(){
        return getStatus() != null;
    }

    public IAsyncExecutionDetails getDetails() {
        return details;
    }

    public void setDetails(IAsyncExecutionDetails details) {
        this.details = details;
    }

    private String readFileIfExists( File file, String defaultValue ){
        if( !details.getOutputFile().exists() ){
            return defaultValue;
        }

        try {
            return FileUtils.readFileToString( file );
        }
        catch(FileNotFoundException e){
            return defaultValue;
        }
        catch( IOException e ) {
            logger.error("unable to read output. returning empty string.", e);
            return defaultValue;
        }


    }

    @Override
    public String getOutput(){
        return readFileIfExists( details.getOutputFile(), "");
    }

    @Override
    public List<String> getOutputAsList(){
        return Arrays.asList(getOutput().split("\n"));
    }


    @Override
    public AsyncExecutionStatus getStatus() {
        try {
            String statusStr = readFileIfExists(details.getStatusFile(), null);
            if ( statusStr != null ){
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue( statusStr, AsyncExecutionStatus.class );
            }
        }catch( Exception e){
            logger.info("unable to read status returning null",e);
        }
        return null;
    }
}
