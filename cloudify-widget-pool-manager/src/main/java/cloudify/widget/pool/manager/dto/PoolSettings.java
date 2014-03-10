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
    private BootstrapProperties bootstrapProperties;
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

    public BootstrapProperties getBootstrapProperties() {
        return bootstrapProperties;
    }

    public void setBootstrapProperties(BootstrapProperties bootstrapProperties) {
        this.bootstrapProperties = bootstrapProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PoolSettings that = (PoolSettings) o;

        if (maxNodes != that.maxNodes) return false;
        if (minNodes != that.minNodes) return false;
        if (!authKey.equals(that.authKey)) return false;
        if (!bootstrapProperties.equals(that.bootstrapProperties)) return false;
        if (!id.equals(that.id)) return false;
        if (!provider.equals(that.provider)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + authKey.hashCode();
        result = 31 * result + maxNodes;
        result = 31 * result + minNodes;
        result = 31 * result + bootstrapProperties.hashCode();
        result = 31 * result + provider.hashCode();
        return result;
    }
}
