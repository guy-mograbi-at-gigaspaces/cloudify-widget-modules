package cloudify.widget.softlayer;

import static com.google.common.collect.Collections2.*;

import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CloudExecResponseImpl;
import cloudify.widget.common.ssh.MachineScriptRunner;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.config.NullLoggingModule;


import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.softlayer.compute.functions.guest.VirtualGuestToReducedNodeMetaDataLocal;
import org.jclouds.softlayer.reference.SoftLayerConstants;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.*;

/**
 * User: eliranm
 * Date: 2/4/14
 * Time: 3:41 PM
 */
public class SoftlayerCloudServerApi implements CloudServerApi {

    private static Logger logger = LoggerFactory.getLogger(SoftlayerCloudServerApi.class);

    private ComputeService computeService = null;

    private SoftlayerConnectDetails connectDetails;


    public SoftlayerCloudServerApi(){

    }



    @Override
    public void connect(IConnectDetails connectDetails) {
       setConnectDetails( connectDetails);
        connect();
    }

    @Override
    public Collection<CloudServer> getAllMachinesWithTag(final String tag) {
        logger.info("getting all machines with tag [{}]", tag);
        Set<? extends NodeMetadata> nodeMetadatas = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(@Nullable ComputeMetadata computeMetadata) {
                return computeMetadata.getName().startsWith(tag);
            }
        });

        return transform(nodeMetadatas, new Function<NodeMetadata, CloudServer>() {
            @Override
            public SoftlayerCloudServer apply(@Nullable NodeMetadata o) {
                return new SoftlayerCloudServer(computeService, o);
            }
        });
    }

    @Override
    public CloudServer get(String serverId) {
        CloudServer cloudServer = null;
        NodeMetadata nodeMetadata = computeService.getNodeMetadata(serverId);
        if (nodeMetadata != null) {
            cloudServer = new SoftlayerCloudServer(computeService, nodeMetadata);
        }
        return cloudServer;
    }

    @Override
    public void delete(String id) {
        SoftlayerCloudServer cloudServer = null;
        if (id != null) {
            cloudServer = (SoftlayerCloudServer) get(id);
        }
        if (cloudServer != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("calling destroyNode, status is [{}]", cloudServer.getStatus());
            }
            try {
                computeService.destroyNode(id);
            } catch (RuntimeException e) {
                throw new SoftlayerCloudServerApiOperationFailureException(
                        String.format("delete operation failed for server with id [%s].", id), e);
            }
        }
    }

    @Override
    public void rebuild(String id) {
        logger.info("rebooting : [{}]", id);
        throw new UnsupportedOperationException("this driver does not support this operation");
    }

    @Override
    public void setConnectDetails(IConnectDetails connectDetails) {
        if (!( connectDetails instanceof SoftlayerConnectDetails )){
            throw new RuntimeException("expected SoftlayerConnectDetails implementation");
        }
        this.connectDetails = (SoftlayerConnectDetails) connectDetails;

    }

    @Override
    public void connect() {
        try{
            logger.info("connecting");
            computeService = computeServiceContext( connectDetails ).getComputeService();
            if ( computeService == null ){
                throw new RuntimeException("illegal credentials");
            }
        }catch(RuntimeException e){
            logger.error("unable to connect softlayer context",e);
            throw e;
        }
    }

    private ComputeServiceContext computeServiceContext( SoftlayerConnectDetails connectDetails) {

        logger.info("creating compute service context");
        Set<Module> modules = new HashSet<Module>();

        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(org.jclouds.softlayer.compute.functions.guest.VirtualGuestToNodeMetadata.class).to(VirtualGuestToReducedNodeMetaDataLocal.class);
            }
        });

        modules.add(new SLF4JLoggingModule());

        ComputeServiceContext context;
        Properties overrides = new Properties();

        // it is strange that we add a machine detail on the context, but it was less work.
        overrides.put("jclouds.timeouts.AccountClient.getActivePackages", String.valueOf(10 * 60 * 1000));
        if (connectDetails.isApiKey()) {
            overrides.put("jclouds.keystone.credential-type", "apiAccessKeyCredentials");
        }

        String cloudProvider = CloudProvider.SOFTLAYER.label;
        logger.info("building new context for provider [{}]", cloudProvider);
        context = ContextBuilder.newBuilder(cloudProvider)
                .credentials(connectDetails.getUsername(), connectDetails.getKey())
                .overrides(overrides)
                .modules(modules)
                .buildView(ComputeServiceContext.class);
        logger.info("new context built");
        return context;
    }

    @Override
    public Collection<? extends CloudServerCreated> create( MachineOptions machineOpts ) {

        SoftlayerMachineOptions softlayerMachineOptions = ( SoftlayerMachineOptions )machineOpts;
        String name = softlayerMachineOptions.name();
        int machinesCount = softlayerMachineOptions.machinesCount();
        Template template = createTemplate(softlayerMachineOptions);
        Set<? extends NodeMetadata> newNodes;
        try {
            logger.info("creating [{}] new machine with name [{}]", machinesCount, name);
            newNodes = computeService.createNodesInGroup( name, machinesCount, template );
        }
        catch (org.jclouds.compute.RunNodesException e) {
            if( logger.isErrorEnabled() ){
                logger.error( "Create softlayer node failed", e );
            }
            throw new RuntimeException( e );
        }

        List<CloudServerCreated> newNodesList = new ArrayList<CloudServerCreated>( newNodes.size() );
        for( NodeMetadata newNode : newNodes ){
            newNodesList.add( new SoftlayerCloudServerCreated( newNode ) );
        }

        return newNodesList;
    }

    private Template createTemplate( SoftlayerMachineOptions machineOptions ) {
        TemplateBuilder templateBuilder = computeService.templateBuilder();

        String hardwareId = machineOptions.hardwareId();
        String locationId = machineOptions.locationId();
        OsFamily osFamily = machineOptions.osFamily();
        if ( !StringUtils.isEmpty(machineOptions.getVersionRegex())) {
            templateBuilder.osVersionMatches(machineOptions.getVersionRegex());
        }
        if( osFamily != null ){
            templateBuilder.osFamily(osFamily);
        }
        if( !StringUtils.isEmpty(hardwareId)){
            templateBuilder.hardwareId( hardwareId );
        }

        if( !StringUtils.isEmpty( locationId ) ){
            templateBuilder.locationId(locationId);
        }

        return templateBuilder.build();
    }

    @Override
    public String createCertificate() {
        throw new UnsupportedOperationException("create certificate is unsupported");
    }

    @Override
    public void createSecurityGroup(ISecurityGroupDetails securityGroupDetails) {
        throw new UnsupportedOperationException("create security group is unsupported in this implementation");
        //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
//    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails) {
//
//        SoftlayerSshDetails softlayerSshDetails = getMachineCredentialsByIp( serverIp );
//        //retrieve missing ssh details
//        String user = softlayerSshDetails.user();
//        String password = softlayerSshDetails.password();
//        int port = softlayerSshDetails.port();
//
//        logger.debug("Run ssh on server: {} script: {}" , serverIp, script );
//
//
//        LoginCredentials loginCredentials = LoginCredentials.builder().user(user).password(password).build();
//        //.privateKey(Strings2.toStringAndClose(new FileInputStream(conf.server.bootstrap.ssh.privateKey)))
//
//        MachineScriptRunner scriptRunner = new MachineScriptRunner();
//        return scriptRunner.runScriptOnMachine(HostAndPort.fromParts(serverIp, port), loginCredentials, script, null );
//    }


    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails) {

        SoftlayerSshDetails softlayerSshDetails = getMachineCredentialsByIp( serverIp );
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

    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails, ISshOutputHandler sshOutputHandler) {
        return null;
    }

    private SoftlayerSshDetails getMachineCredentialsByIp( final String ip ){

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

        return new SoftlayerSshDetails( port, user, password );
    }


}
