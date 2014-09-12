package cloudify.widget.softlayer;

import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.compute.domain.ExecChannel;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/2/14
 * Time: 4:09 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:softlayer-context.xml"})
public class TestAsyncSsh {

    private static Logger logger = LoggerFactory.getLogger(TestAsyncSsh.class);

    @Autowired
    public TestConfig config;
    @Test
    public void testMe() throws IOException {
        logger.info("running test");
        Injector i = Guice.createInjector(new SshjSshClientModule(), new NullLoggingModule());
        logger.info("getting instance");
        SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
        logger.info("building credentials");
        LoginCredentials loginCredentials = LoginCredentials.builder().user(config.username).password(config.password).build();

        logger.info("connecting to " + config.toString() );
        SshClient sshConnection = factory.create( HostAndPort.fromParts(config.ip, config.port), loginCredentials );


        logger.info("sync test");
        ExecResponse exec = sshConnection.exec("echo hello");
        System.out.println("exec.getOutput() = " + exec.getOutput());

        logger.info("async test");
        logger.info("running command");
        ExecChannel execChannel = sshConnection.execChannel(config.command);
        logger.info("got channel");
        InputStream output = execChannel.getOutput();
        InputStreamReader reader = new InputStreamReader(output);
        BufferedReader buffReader = new BufferedReader(reader);

        logger.info("exit status is " + execChannel.getExitStatus().get())  ;
        while ( execChannel.getExitStatus().get() == null ) {
            if ( buffReader.ready() ){
                logger.info("reading output");
                String s = buffReader.readLine();
                logger.info("s = " + s);
            }else{
                logger.info("not ready yet.. waiting.. ");
                try{Thread.sleep(1000);}catch(Exception e){}
            }

        }
    }

    public TestConfig getConfig() {
        return config;
    }

    public void setConfig(TestConfig config) {
        this.config = config;
    }

    public static class TestConfig{
        public String ip;
        public int port;
        public String username;
        public String password;
        public String command;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        @Override
        public String toString() {
            return "TestConfig{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", command='" + command + '\'' +
                    '}';
        }
    }
}

