package cloudify.widget.common.ssh;

import cloudify.widget.api.clouds.CloudExecResponse;
import cloudify.widget.api.clouds.ISshOutputHandler;
import cloudify.widget.common.CloudExecResponseImpl;
import cloudify.widget.common.CloudExecResponsePojo;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.compute.domain.ExecChannel;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/4/14
 * Time: 3:04 PM
 */
public class MachineScriptRunner {

    private static Logger logger = LoggerFactory.getLogger(MachineScriptRunner.class);

    public CloudExecResponse runScriptOnMachine( HostAndPort hostAndPort,
                                                 LoginCredentials loginCredentials,
                                                 String script,
                                                 ISshOutputHandler outputHandler
    ){
        Injector i = Guice.createInjector(new SshjSshClientModule(), new NullLoggingModule());
        SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
        SshClient sshConnection = factory.create( hostAndPort, loginCredentials );
        ExecResponse execResponse = null;




        try{
            sshConnection.connect();
            logger.info("ssh connected, executing");

            StringBuilder stringBuilder = new StringBuilder();


            ExecChannel execChannel = sshConnection.execChannel(script);

            BufferedReader buffReader = new BufferedReader(new InputStreamReader(execChannel.getOutput()));
            while ( execChannel.getExitStatus().get() == null ){
//                    buffReader.ready();
            }

            logger.info("finished execution");
//            return new CloudExecResponseImpl( execResponse );

//            CloudExecResponsePojo result = new CloudExecResponsePojo( );
//            result.setExitStatus( );
            return null;
        }catch(Exception e){
            logger.error("error while running ssh",e);
        }
        finally{
            if (sshConnection != null)
                sshConnection.disconnect();
        }

        // in case all fails
        CloudExecResponsePojo result = new CloudExecResponsePojo();
        result.setExitStatus(-1);
        result.setOutput("something horrible happened and the SSH could not execute...");
        return result;

    }
}
