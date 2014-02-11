package cloudify.widget.ec2;

import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CloudExecResponseImpl;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2CloudServerApi implements CloudServerApi {

    private static Logger logger = LoggerFactory.getLogger(Ec2CloudServerApi.class);

    private final ComputeService computeService;
    private final AWSEC2Api ec2Api;
//    private final Ec2Api ec2Api;

    public Ec2CloudServerApi(ComputeService computeService, AWSEC2Api ec2Api ) {
        this.computeService = computeService;
        this.ec2Api = ec2Api;
    }

    @Override
    public Collection<CloudServer> getAllMachinesWithTag(final String tag) {

        Set<? extends NodeMetadata> nodeMetadatas = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(@Nullable ComputeMetadata computeMetadata) {
                return tag == null ? true : computeMetadata.getTags().contains(tag);
            }
        });

        return transform(nodeMetadatas, new Function<NodeMetadata, CloudServer>() {
            @Override
            public Ec2CloudServer apply(@Nullable NodeMetadata o) {
                return new Ec2CloudServer(computeService, o);
            }
        });
    }

    @Override
    public CloudServer get(String serverId) {
        CloudServer cloudServer = null;

//        VirtualGuest virtualGuest = ec2Api.getVirtualGuestClient().getVirtualGuest(0);
//        logger.info("virtual guest: [{}]", virtualGuest);

/*        Server server = softLayerApi.get(serverId);
        if (server != null) {
            cloudServer = new SoftlayerCloudServer(server);
        }*/

        return cloudServer;
    }

    @Override
    public boolean delete(String id) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void rebuild(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends CloudServerCreated> create( MachineOptions machineOpts ) {

        Ec2MachineOptions ec2MachineOptions = ( Ec2MachineOptions )machineOpts;
        String name = ec2MachineOptions.name();
        int machinesCount = ec2MachineOptions.machinesCount();
        Template template = createTemplate(ec2MachineOptions);
        Set<? extends NodeMetadata> newNodes;
        try {
            newNodes = computeService.createNodesInGroup( name, machinesCount, template );
        }
        catch (org.jclouds.compute.RunNodesException e) {
            if( logger.isErrorEnabled() ){
                logger.error( "Create EC2 node failed", e );
            }
            throw new RuntimeException( e );
        }

        List<CloudServerCreated> newNodesList = new ArrayList<CloudServerCreated>( newNodes.size() );
        for( NodeMetadata newNode : newNodes ){
            newNodesList.add( new Ec2CloudServerCreated( newNode ) );
        }

        return newNodesList;
    }

    private Template createTemplate( Ec2MachineOptions machineOptions ) {
        TemplateBuilder templateBuilder = computeService.templateBuilder();

        String hardwareId = machineOptions.hardwareId();
        String locationId = machineOptions.locationId();
        String imageId = machineOptions.locationId();
        OsFamily osFamily = machineOptions.osFamily();
        if( osFamily != null ){
            templateBuilder.osFamily(osFamily);
        }
        if( !StringUtils.isEmpty(hardwareId)){
            templateBuilder.hardwareId( hardwareId );
        }
        if( !StringUtils.isEmpty( locationId ) ){
            templateBuilder.locationId(locationId);
        }
        if( !StringUtils.isEmpty( locationId ) ){
            templateBuilder.imageId(imageId);
        }

        Template template = templateBuilder.build();
        if( machineOptions.tags() != null ){
            template.getOptions().tags( machineOptions.tags() );
        }

        return template;
    }

    @Override
    public String createCertificate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connect(IConnectDetails connectDetails) {

    }

    @Override
    public void createSecurityGroup(ISecurityGroupDetails securityGroupDetails) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails) {

        Ec2SshDetails softlayerSshDetails = getMachineCredentialsByIp( serverIp );
        //retrieve missing ssh details
        String user = softlayerSshDetails.user();
        String password = softlayerSshDetails.password();
        int port = softlayerSshDetails.port();

        logger.debug("Run ssh on server: {} script: {}" , serverIp, script );
        Injector i = Guice.createInjector(new SshjSshClientModule(), new NullLoggingModule());
        SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
        LoginCredentials loginCredentials = LoginCredentials.builder().user(user).password(password).build();
        //.privateKey(Strings2.toStringAndClose(new FileInputStream(conf.server.bootstrap.ssh.privateKey)))

        SshClient sshConnection = factory.create(HostAndPort.fromParts(serverIp, port),
                loginCredentials );
        ExecResponse execResponse = null;
        try{
            sshConnection.connect();
            logger.info("ssh connected, executing");
            execResponse = sshConnection.exec(script);
            logger.info("finished execution");
        }
        finally{
            if (sshConnection != null)
                sshConnection.disconnect();
        }

        return new CloudExecResponseImpl( execResponse );
    }


    private Ec2SshDetails getMachineCredentialsByIp( final String ip ){

        Set<? extends NodeMetadata> nodeMetadatas = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(ComputeMetadata computeMetadata) {
                NodeMetadata nodeMetadata = (NodeMetadata) computeMetadata;
                Set<String> publicAddresses = nodeMetadata.getPublicAddresses();
                return publicAddresses.contains(ip);
            }
        });

//        NodeMetadata nodeMetadata = computeService.getNodeMetadata(nodeId);
        if( nodeMetadatas.isEmpty() ){
            throw new RuntimeException( "Machine [" + ip + "] was not found" );
        }

        NodeMetadata nodeMetadata = nodeMetadatas.iterator().next();

        LoginCredentials loginCredentials = nodeMetadata.getCredentials();
        String user = loginCredentials.getUser();
        String password = loginCredentials.getPassword();
        int port = nodeMetadata.getLoginPort();

        return new Ec2SshDetails( port, user, password );
    }

    @Override
    public CloudServerCreated create(String name, String imageRef, String flavorRef, CloudCreateServerOptions... options) throws RunNodesException {
        throw new UnsupportedOperationException("this method is no longer supported, please use create(MachineOptions machineOpts) instead.");
    }
}
