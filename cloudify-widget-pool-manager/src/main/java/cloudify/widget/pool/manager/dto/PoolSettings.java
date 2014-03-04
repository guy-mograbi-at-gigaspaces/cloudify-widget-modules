package cloudify.widget.pool.manager.dto;

/**
 *
 * Please don't remove fields, use deprecation instead.
 *
 * User: eliranm
 * Date: 2/27/14
 * Time: 3:21 PM
 */
public class PoolSettings {

    private String id;
    private String authKey;
    private int maxNodes;
    private int minNodes;
    private ProviderSettings provider;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public int getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public int getMinNodes() {
        return minNodes;
    }

    public void setMinNodes(int minNodes) {
        this.minNodes = minNodes;
    }

    public ProviderSettings getProvider() {
        return provider;
    }

    public void setProvider(ProviderSettings provider) {
        this.provider = provider;
    }
}
