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

    public int getMinNodes() {
        return minNodes;
    }

    public PoolStatus setMinNodes(int minNodes) {
        this.minNodes = minNodes;
        return this;
    }

    public int getMaxNodes() {
        return maxNodes;
    }

    public PoolStatus setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
        return this;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public PoolStatus setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
        return this;
    }
}
