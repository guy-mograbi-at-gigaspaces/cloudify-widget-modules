package cloudify.widget.pool.manager.dto;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 1:33 PM
 */
public class PoolStatus {

    public int minNodes;
    public int maxNodes;
    public int currentSize;

    public PoolStatus minNodes(int minNodes) {
        this.minNodes = minNodes;
        return this;
    }

    public PoolStatus maxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
        return this;
    }

    public PoolStatus currentSize(int currentSize) {
        this.currentSize = currentSize;
        return this;
    }
}
