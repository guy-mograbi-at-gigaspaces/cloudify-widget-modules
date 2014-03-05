package cloudify.widget.pool.manager.dto;

import java.util.LinkedList;
import java.util.List;

/**
* User: eliranm
* Date: 2/27/14
* Time: 3:21 PM
*/
public class ManagerSettings {

    private List<PoolSettings> pools = new LinkedList<PoolSettings>();

    public List<PoolSettings> getPools() {
        return pools;
    }

    public void setPools(List<PoolSettings> pools) {
        this.pools = pools;
    }
}
