package cloudify.widget.pool.manager;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 3:14 AM
 */
public class PoolMachineStatus {
    public boolean managementAvailable = false;
    public boolean applicationIsOnline = false;
    public String ip = null;
    public long uptimeMillis = -1;


    public MachineId getMachineId(){
        return new MachineId(ip);
    }
}
