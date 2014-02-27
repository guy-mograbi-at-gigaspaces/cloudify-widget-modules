package cloudify.widget.website.models;

import cloudify.widget.pool.manager.PoolSettings;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/27/14
 * Time: 10:46 AM
 */
public class PoolConfigurationModel {

    public Long id;

    public PoolSettings poolSettings;

    public Long accountId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PoolSettings getPoolSettings() {
        return poolSettings;
    }

    public void setPoolSettings(PoolSettings poolSettings) {
        this.poolSettings = poolSettings;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
