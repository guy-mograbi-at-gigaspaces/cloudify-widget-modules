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

    public String id;
    public String authKey;
    public int maxNodes;
    public int minNodes;
    public ProviderSettings provider;
}
