package cloudify.widget.api.clouds;


import java.util.Collection;

/**
 * 
 * @author evgenyf
 * Date: 10/7/13
 */
public interface CloudServerApi {

    /**
     * returns all machines with a specific tag
     * if tag is null, it will return all machines
     *
     * a tag is an identifier of a pool. it can even be a machine name prefix,
     * as long as it can identify machines from specific pool.
     *
     * @param tag -
     * @return all the machines from the pool.
     */
    public Collection<CloudServer> getAllMachinesWithTag( String tag );

    /**
     * get CloudServer by id
     * @param serverId - the server id
     * @return CloudServer - null if does not exists. otherwise CloudServer from the cloud.
     */
	public CloudServer get( String serverId );

    /**
     * Machine should be removed from the cloud
     * @param id - id of node
     */
	public void delete(String id);

    /**
     * rebuild the machine
     * @param id - machine id
     */
	public void rebuild( String id );

    /**
     * create a new machine
     * @param machineOpts  - options for machine
     * @return an instance that holds the new machine's id.
     */
    public Collection<? extends CloudServerCreated> create( MachineOptions machineOpts );

    /**
     * returns a PEM file content
     * @return
     */
    public String createCertificate();


    public void connect( IConnectDetails connectDetails );

    /**
     * setter for connect details.
     * important for spring beans.
     * @return
     */
    public void setConnectDetails( IConnectDetails connectDetails);

    /**
     * uses the connect details.
     * important for spring beans.
     */
    public void connect();
    /**
     *
     * creates a security group
     */
    public void createSecurityGroup( ISecurityGroupDetails securityGroupDetails);

    public CloudExecResponse runScriptOnMachine( String script, String serverIp, ISshDetails sshDetails );

}
