package cloudify.widget.hpcloudcompute;


import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CloudExecResponseImpl;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
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
public class HpCloudComputeCloudServerApi implements CloudServerApi {

    private static Logger logger = LoggerFactory.getLogger(HpCloudComputeCloudServerApi.class);

    private ContextBuilder contextBuilder;
    private ComputeServiceContext computeServiceContext;
    private ComputeService computeService;
    private HpCloudComputeConnectDetails connectDetails;

    public HpCloudComputeCloudServerApi() {
    }

    @Override
    public Collection<CloudServer> getAllMachinesWithTag(final String tag) {

        Set<? extends NodeMetadata> nodeMetadatas = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(@Nullable ComputeMetadata computeMetadata) {
                NodeMetadata nodeMetadata = ( NodeMetadata )computeMetadata;
                return nodeMetadata.getStatus() == NodeMetadata.Status.RUNNING &&
                        ( tag == null ? true : computeMetadata.getTags().contains( tag ));
            }
        });

        return transform(nodeMetadatas, new Function<NodeMetadata, CloudServer>() {
            @Override
            public HpCloudComputeCloudServer apply(@Nullable NodeMetadata o) {
                return new HpCloudComputeCloudServer(computeService, o);
            }
        });
    }

    @Override
    public CloudServer get(String serverId) {
        CloudServer cloudServer = null;
        NodeMetadata nodeMetadata = computeService.getNodeMetadata(serverId);
        if (nodeMetadata != null) {
            cloudServer = new HpCloudComputeCloudServer( computeService, nodeMetadata );
        }
        return cloudServer;
    }

    @Override
    public void delete(String id) {

        if (id != null) {
            try {
                computeService.destroyNode(id);
            }
            catch (Throwable e) {
                throw new HpCloudComputeServerApiOperationFailureException(
                        String.format("delete operation failed for server with id [%s].", id), e);
            }
        }
    }


    private ServerApi getApi( String zone ){
        NovaApi novaApi = contextBuilder.buildApi(NovaApi.class);
        ServerApi serverApi = novaApi.getServerApiForZone( zone );
        return serverApi;
    }

    @Override
    public void rebuild(String id) {

        HpCloudComputeCloudServer cloudServer = ( HpCloudComputeCloudServer )get(id);
        String imageId = cloudServer.getImageId();
        String zone = cloudify.widget.common.StringUtils.substringBefore( imageId, "/" );
        String imageIdLocal = cloudify.widget.common.StringUtils.substringAfter( imageId, "/" );

        String idLocal = StringUtils.substringAfter(id, "/");
        logger.info("rebuilding [{}] that had image id [{}] idLocal [{}] with zone [{}] and imageIdLocal [{}]", id, imageId, idLocal, zone, imageIdLocal);

        ServerApi serverApi = getApi(zone);

        RebuildServerOptions rebuildServerOptions = RebuildServerOptions.Builder.withImage( imageIdLocal );

        serverApi.rebuild( idLocal, rebuildServerOptions );
    }



    @Override
    public Collection<? extends CloudServerCreated> create( MachineOptions machineOpts ) {

        logger.info( "Starting to create new node(s)..." );
        long startTime = System.currentTimeMillis();

        HpCloudComputeMachineOptions hpCloudMachineOptions = ( HpCloudComputeMachineOptions )machineOpts;
        String name = hpCloudMachineOptions.name();
        int machinesCount = hpCloudMachineOptions.machinesCount();
        Template template = createTemplate(hpCloudMachineOptions);
        Set<? extends NodeMetadata> newNodes;
        try {
            newNodes = computeService.createNodesInGroup( name, machinesCount, template );
        }
        catch (org.jclouds.compute.RunNodesException e) {
            if( logger.isErrorEnabled() ){
                logger.error( "Create Hp cloud node failed", e );
            }
            throw new RuntimeException( e );
        }

        long endTime = System.currentTimeMillis();
        long totalTimeSec = ( endTime - startTime )/1000;
        logger.info( "After create new node, creating took [" + ( totalTimeSec ) + "] sec." );

        List<CloudServerCreated> newNodesList = new ArrayList<CloudServerCreated>( newNodes.size() );
        for( NodeMetadata newNode : newNodes ){
            newNodesList.add( new HpCloudComputeCloudServerCreated( newNode ) );
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
        if (!(connectDetails instanceof HpCloudComputeConnectDetails)) {
            throw new RuntimeException("expected HpCloudComputeConnectDetails implementation");
        }
        this.connectDetails = (HpCloudComputeConnectDetails) connectDetails;

    }

    @Override
    public void connect() {
        computeServiceContext = computeServiceContext(connectDetails);
        computeService = computeServiceContext.getComputeService();
    }

    public ComputeServiceContext computeServiceContext(HpCloudComputeConnectDetails connectDetails) {

        contextBuilder = createContextBuilder();
        ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);

        return context;
    }

    private ContextBuilder createContextBuilder(){

        String project = connectDetails.getProject();
        String key = connectDetails.getKey();
        String secretKey = connectDetails.getSecretKey();
        String identity = project + ":" + key;

        logger.info("creating compute service context");

        String cloudProvider = CloudProvider.HP.label;
        logger.info("building new context for provider [{}]", cloudProvider);

        Properties overrides = new Properties();
        overrides.put("jclouds.keystone.credential-type", "apiAccessKeyCredentials");

        ContextBuilder contextBuilder = ContextBuilder.newBuilder(cloudProvider)
                .credentials(identity, secretKey)
                .overrides(overrides);

        return contextBuilder;
    }

    private Template createTemplate( HpCloudComputeMachineOptions machineOptions ) {
        TemplateBuilder templateBuilder = computeService.templateBuilder();

        String hardwareId = machineOptions.hardwareId();
        String imageId = machineOptions.imageId();

        if( !StringUtils.isEmpty(hardwareId)){
            templateBuilder.hardwareId(hardwareId);
        }

        if( !StringUtils.isEmpty( imageId ) ){
            templateBuilder.imageId(imageId);
        }

        logger.info( "Before building template" );
        long startTime = System.currentTimeMillis();
        Template template = templateBuilder.build();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info( "After building template, build took [" + ( totalTime ) + "] msec." );

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

        HpCloudComputeSshDetails hpCloudSshDetails = getMachineCredentialsByIp( serverIp );
        //retrieve missing ssh details
        String user = "ubuntu";//hpCloudSshDetails.user();
        String privateKey = hpCloudSshDetails.privateKey();
        int port = hpCloudSshDetails.port();

        logger.debug("Run ssh on server: {} script: {}" , serverIp, script );
        Injector i = Guice.createInjector(new SshjSshClientModule(), new NullLoggingModule());
        SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
        LoginCredentials loginCredentials = LoginCredentials.builder().user(user).privateKey(privateKey).build();

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

    private HpCloudComputeSshDetails getMachineCredentialsByIp( final String ip ){

        Set<? extends NodeMetadata> nodeMetadatas = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(ComputeMetadata computeMetadata) {
                NodeMetadata nodeMetadata = (NodeMetadata) computeMetadata;
                Set<String> publicAddresses = nodeMetadata.getPublicAddresses();
                return publicAddresses.contains(ip);
            }
        });

        if( nodeMetadatas.isEmpty() ){
            throw new RuntimeException( "Machine [" + ip + "] was not found" );
        }

        NodeMetadata nodeMetadata = nodeMetadatas.iterator().next();

        LoginCredentials loginCredentials = nodeMetadata.getCredentials();
        String user = loginCredentials.getUser();
        String privateKey = loginCredentials.getPrivateKey();
        int port = nodeMetadata.getLoginPort();

        return new HpCloudComputeSshDetails( port, user, privateKey );
    }

    private LoginCredentials getMachineLoginCredentialssByIp( final String ip ){

        Set<? extends NodeMetadata> nodeMetadatas = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(ComputeMetadata computeMetadata) {
                NodeMetadata nodeMetadata = (NodeMetadata) computeMetadata;
                Set<String> publicAddresses = nodeMetadata.getPublicAddresses();
                return publicAddresses.contains(ip);
            }
        });

        if( nodeMetadatas.isEmpty() ){
            throw new RuntimeException( "Machine [" + ip + "] was not found" );
        }

        NodeMetadata nodeMetadata = nodeMetadatas.iterator().next();

        return nodeMetadata.getCredentials();
    }
}
