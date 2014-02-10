package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CloudExecResponseImpl;
import com.google.common.base.Predicate;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.Strings2;
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

    private final ComputeService computeService;
    private final SoftLayerApi softLayerApi;

    public SoftlayerCloudServerApi(ComputeService computeService, SoftLayerApi softLayerApi) {
        this.computeService = computeService;
        this.softLayerApi = softLayerApi;
    }

    @Override
    public Collection<CloudServer> getAllMachinesWithTag(String tag) {
        List<CloudServer> cloudServers = new LinkedList<CloudServer>();
        for (ComputeMetadata computeMetadata : computeService.listNodes()) {
            computeService.getNodeMetadata(computeMetadata.getId());
            if (computeMetadata.getTags().contains(tag)) {
            }
            cloudServers.add(new SoftlayerCloudServer(computeMetadata));
        }
        return cloudServers;
    }

    @Override
    public CloudServer get(String serverId) {
        CloudServer cloudServer = null;

        VirtualGuest virtualGuest = softLayerApi.getVirtualGuestClient().getVirtualGuest(0);
        logger.info("virtual guest: [{}]", virtualGuest);
/*
        Server server = softLayerApi.get(serverId);
        if (server != null) {
            cloudServer = new SoftlayerCloudServer(server);
        }
*/
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
    public Collection<CloudServerCreated> create( MachineOptions machineOpts ) {

        SoftlayerMachineOptions softlayerMachineOptions = ( SoftlayerMachineOptions )machineOpts;
        String name = softlayerMachineOptions.name();
        int machinesCount = softlayerMachineOptions.machinesCount();
        Template template = createTemplate(softlayerMachineOptions);
        Set<? extends NodeMetadata> newNodes;
        try {
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createSecurityGroup(ISecurityGroupDetails securityGroupDetails) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails) {

        SoftlayerSshDetails softlayerSshDetails = getMachineCredentialsByIp( serverIp );
        //retrieve missing ssh details
        String user = softlayerSshDetails.user();
        String password = softlayerSshDetails.password();
        int port = softlayerSshDetails.port();

        logger.debug("Run ssh on server: {} script: {}" , serverIp, script );
        Injector i = Guice.createInjector( new SshjSshClientModule(), new NullLoggingModule() );
        SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
        LoginCredentials loginCredentials = LoginCredentials.builder().user(user).password(password).build();
        //.privateKey(Strings2.toStringAndClose(new FileInputStream(conf.server.bootstrap.ssh.privateKey)))

        SshClient sshConnection = factory.create(HostAndPort.fromParts( serverIp, port ),
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

    @Override
    public CloudServerCreated create(String name, String imageRef, String flavorRef, CloudCreateServerOptions... options) throws RunNodesException {
        throw new UnsupportedOperationException("this method is no longer supported, please use create(MachineOptions machineOpts) instead.");
    }
}
