package cloudify.widget.pool.manager;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 4:21 AM
 */
public class MachineModel {
    public long id;
    public String publicIp;
    public boolean remote;
    public long busySince;

    public MachineId getMachineId(){
        return new MachineId(publicIp);
    }

    @Override
    public String toString() {
        return "MachineModel{" +
                "id=" + id +
                ", publicIp='" + publicIp + '\'' +
                ", remote=" + remote +
                ", busySince=" + busySince +
                ", busyDuration=" + ( System.currentTimeMillis() - busySince )/360000 + "minutes" +

                '}';
    }


}
