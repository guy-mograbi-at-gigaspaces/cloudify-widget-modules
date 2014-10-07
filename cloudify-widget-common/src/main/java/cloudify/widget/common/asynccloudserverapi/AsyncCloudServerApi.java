package cloudify.widget.common.asynccloudserverapi;

import cloudify.widget.api.clouds.CloudExecResponse;
import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.api.clouds.ISecurityGroupDetails;
import cloudify.widget.api.clouds.ISshDetails;
import cloudify.widget.api.clouds.ISshOutputHandler;
import cloudify.widget.api.clouds.MachineOptions;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/2/14
 * Time: 1:38 PM
 *
 * a wrapper for other cloud server apis.
 */
public class AsyncCloudServerApi implements CloudServerApi{

    private CloudServerApi wrapped;

    Future<CloudServerApi> connected = null;

    private CloudServerApi wrapped(){
        try {
            return connected.get();
        }catch(Exception e){
            connected = null; // reset
            throw new RuntimeException("unable to connect asynchronously ");
        }
    }

    @Override
    public void connect(IConnectDetails connectDetails) {
        wrapped.setConnectDetails(connectDetails);
        connect();
    }

    @Override
    public void setConnectDetails(IConnectDetails connectDetails) {
        wrapped.setConnectDetails(connectDetails);
    }

    @Override
    public void connect() {
        if ( connected == null ){
            connected = new FutureTask<CloudServerApi>( new ConnectServerApiAsynchronously().setCloudServerApi(wrapped), wrapped);
        }
    }


    @Override
    public Collection<CloudServer> getAllMachinesWithTag(String tag) {
        return wrapped().getAllMachinesWithTag(tag);
    }

    @Override
    public CloudServer get(String serverId) {
        return wrapped().get(serverId);
    }

    @Override
    public void delete(String id) {
        wrapped().delete(id);
    }

    @Override
    public void rebuild(String id) {
        wrapped().rebuild(id);
    }

    @Override
    public Collection<? extends CloudServerCreated> create(MachineOptions machineOpts) {
        return wrapped().create(machineOpts);
    }

    @Override
    public String createCertificate() {
        return wrapped().createCertificate();
    }

    @Override
    public void createSecurityGroup(ISecurityGroupDetails securityGroupDetails) {
        wrapped().createSecurityGroup(securityGroupDetails);
    }

    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails, ISshOutputHandler sshOutputHandler) {
        return wrapped().runScriptOnMachine(script, serverIp, sshDetails, sshOutputHandler);
    }

    @Override
    public CloudExecResponse runScriptOnMachine(String script, String serverIp, ISshDetails sshDetails) {
        return wrapped().runScriptOnMachine(script, serverIp, sshDetails);
    }

    public CloudServerApi getWrapped() {
        return wrapped;
    }

    public void setWrapped(CloudServerApi wrapped) {
        this.wrapped = wrapped;
    }

    public static class ConnectServerApiAsynchronously implements Runnable{

        private CloudServerApi cloudServerApi;

        @Override
        public void run() {
            try {
                cloudServerApi.connect();
            }catch(Exception e){
                throw new RuntimeException("unable to connect",e);
            }
        }

        public ConnectServerApiAsynchronously setCloudServerApi(CloudServerApi cloudServerApi) {
            this.cloudServerApi = cloudServerApi;
            return this;
        }
    }

}
