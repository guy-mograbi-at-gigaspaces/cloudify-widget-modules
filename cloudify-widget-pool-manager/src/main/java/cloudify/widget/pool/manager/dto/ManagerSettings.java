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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerSettings that = (ManagerSettings) o;
        if (!pools.equals(that.pools)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return pools.hashCode();
    }
}
