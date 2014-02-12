package cloudify.widget.hpcloud;

import cloudify.widget.api.clouds.CloudCreateServerOptions;
import cloudify.widget.api.clouds.CloudExecResponse;
import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.api.clouds.ISecurityGroupDetails;
import cloudify.widget.api.clouds.ISshDetails;
import cloudify.widget.api.clouds.MachineOptions;
import cloudify.widget.api.clouds.RunNodesException;
import cloudify.widget.common.CloudExecResponseImpl;
import cloudify.widget.common.StringUtils;
import com.google.common.collect.FluentIterable;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaAsyncApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.Strings2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/6/14
 * Time: 1:58 PM
 */
public class HpCloudServerApi implements CloudServerApi {

    private static Logger logger = LoggerFactory.getLogger(HpCloudServerApi.class);

    private ComputeService computeService;
    private ComputeServiceContext context;
    private ServerApi serverApi;

    public void connect( IConnectDetails connectDetails ){

        if ( ! ( connectDetails instanceof HpCloudConnectDetails ) ){
            throw new RuntimeException("expected HpCloudConnectDetails");
        }

        HpCloudConnectDetails hpCloudConnectDetails = (HpCloudConnectDetails) connectDetails;




    }

    @Override
    public Collection<CloudServer> getAllMachinesWithTag(String tag) {
        return null;
    }

    @Override
    public CloudServer get(String serverId) {
        return null;
    }

    @Override
    public void delete(String id) {
        computeService.destroyNode(id);
    }

    @Override
    public void rebuild(String id) {
        computeService.rebootNode(id);
    }

    @Override
    public void setConnectDetails(IConnectDetails connectDetails) {

    }

    @Override
    public void connect() {

    }

    @Override
    public Collection<? extends CloudServerCreated> create(MachineOptions machineOpts) {

        if (!(machineOpts instanceof HpMachineOptions)) {
            throw new RuntimeException("expected HpMachineOptions implementation");
        }

        HpMachineOptions hpMachineOptions = (HpMachineOptions) machineOpts;
        List<CreateServerOptions> optionsList = new ArrayList<CreateServerOptions>();
        CreateServerOptions serverOptions = new CreateServerOptions();
        Map<String, String> metadata = new HashMap<String, String>();

        List<String> tags = new LinkedList<String>();

        if (!StringUtils.isEmpty(hpMachineOptions.tag)) {
            tags.add(hpMachineOptions.tag);
        }

        metadata.put("tags", StringUtils.join(tags, ","));
        serverOptions.metadata(metadata);
        serverOptions.keyPairName(hpMachineOptions.keyPair);
        serverOptions.securityGroupNames(hpMachineOptions.securityGroup);

        optionsList.add(serverOptions);
        ServerCreated serverCreated = serverApi.create(hpMachineOptions.name, hpMachineOptions.imageRef, hpMachineOptions.flavorRef,
                optionsList.toArray(new CreateServerOptions[optionsList.size()]));
        return Collections.singletonList(new HpCloudServerCreated(serverCreated));
    }

    @Override
    public String createCertificate() {
        return null;
    }

    @Override
    public void createSecurityGroup( ISecurityGroupDetails securityGroupDetails) {

        if (!(securityGroupDetails instanceof HpCloudSecurityGroupDetails)) {
            throw new RuntimeException("expected HpCloudSecurityGroupDetails implementation");

        }

        HpCloudSecurityGroupDetails hpCloudSecurityGroupDetails = (HpCloudSecurityGroupDetails) securityGroupDetails;

        try {
            RestContext<NovaApi, NovaAsyncApi> novaClient = context.unwrap();
            NovaApi novaApi = novaClient.getApi();
            SecurityGroupApi securityGroupClient = novaApi.getSecurityGroupExtensionForZone(hpCloudSecurityGroupDetails.zoneName).get();
            //Check if group already exists.
            FluentIterable<? extends SecurityGroup> groupsList = securityGroupClient.list();
            for (Object group : groupsList) {
                if (((SecurityGroup)group).getName().equals(hpCloudSecurityGroupDetails.securityGroup)) {
                    return;
                }
            }
            //Create a new security group with open port range of 80-65535.
            Ingress ingress = Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(1).toPort(65535).build();
            SecurityGroup securityGroup = securityGroupClient.createWithDescription(hpCloudSecurityGroupDetails.securityGroup, "All ports open.");
            securityGroupClient.createRuleAllowingCidrBlock(securityGroup.getId(), ingress, "0.0.0.0/0");
        }
        catch (Exception e) {
            throw new RuntimeException("Failed creating security group.", e);
        }
    }

    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp,  ISshDetails sshDetails ) {
        if (!( sshDetails instanceof HpSshDetails )){
            throw new RuntimeException("expecting implementation of HpSshDetails");
        }

        try {
            HpSshDetails hpSshDetails = (HpSshDetails) sshDetails;
            logger.debug("Run ssh on server: {} script: {}", serverIp, script);
            Injector i = Guice.createInjector(new SshjSshClientModule(), new NullLoggingModule());
            SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
            SshClient sshConnection = factory.create(HostAndPort.fromParts(serverIp, hpSshDetails.port),
                    LoginCredentials.builder().user(hpSshDetails.username)
                            .privateKey(Strings2.toStringAndClose(new FileInputStream(hpSshDetails.privateKey))).build());
            ExecResponse execResponse = null;
            try {
                sshConnection.connect();
                logger.info("ssh connected, executing");
                execResponse = sshConnection.exec(script);
                logger.info("finished execution");
            } finally {
                if (sshConnection != null)
                    sshConnection.disconnect();
            }

            return new CloudExecResponseImpl(execResponse);
        }catch(Exception e){
            throw new RuntimeException("unable to run SSH on " + serverIp,e);
        }
    }

}
