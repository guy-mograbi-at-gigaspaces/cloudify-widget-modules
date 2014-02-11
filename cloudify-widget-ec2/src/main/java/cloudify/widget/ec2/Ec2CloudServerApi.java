package cloudify.widget.ec2;

import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CloudExecResponseImpl;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.collect.Collections2.transform;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2CloudServerApi implements CloudServerApi {

    private static Logger logger = LoggerFactory.getLogger(Ec2CloudServerApi.class);

    private ComputeService computeService;
    private Ec2ConnectDetails connectDetails;
//    private final AWSEC2Api ec2Api;

    public Ec2CloudServerApi() {
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
    public void delete(String id) {

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

    @Override
    public String createCertificate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connect(IConnectDetails connectDetails) {
        setConnectDetails(connectDetails);
        connect();
    }

    @Override
    public void setConnectDetails(IConnectDetails connectDetails) {
        logger.info("connecting");
        if (!( connectDetails instanceof Ec2ConnectDetails )){
            throw new RuntimeException("expected SoftlayerConnectDetails implementation");
        }
        this.connectDetails = (Ec2ConnectDetails) connectDetails;

    }

    @Override
    public void connect() {
        computeService = computeServiceContext(connectDetails).getComputeService();
    }

    public static ComputeServiceContext computeServiceContext(Ec2ConnectDetails connectDetails) {

        String accessId = connectDetails.getAccessId();
        String secretAccessKey = connectDetails.getSecretAccessKey();

        logger.info("creating compute service context");

        Properties overrides = new Properties();
//        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "");
//        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

        String cloudProvider = CloudProvider.AWS_EC2.label;
        logger.info("building new context for provider [{}]", cloudProvider);
        ComputeServiceContext context = ContextBuilder.newBuilder(cloudProvider)
                .overrides(overrides)
                .credentials(accessId, secretAccessKey)
//                .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);

        return context;
    }

    private Template createTemplate( Ec2MachineOptions machineOptions ) {
        TemplateBuilder templateBuilder = computeService.templateBuilder();

        String hardwareId = machineOptions.hardwareId();
        String locationId = machineOptions.locationId();
        String imageId = machineOptions.imageId();
        OsFamily osFamily = machineOptions.osFamily();

        if( osFamily != null ){
            templateBuilder.osFamily(osFamily);
        }
        if( !StringUtils.isEmpty(hardwareId)){
            templateBuilder.hardwareId(hardwareId);
        }
        if( !StringUtils.isEmpty( locationId ) ){
            templateBuilder.locationId(locationId);
        }
        if( !StringUtils.isEmpty( imageId ) ){
            templateBuilder.imageId(imageId);
        }

        Template template = templateBuilder.build();
        if( machineOptions.tags() != null ){
            template.getOptions().tags( machineOptions.tags() );
        }

        return template;
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
}
