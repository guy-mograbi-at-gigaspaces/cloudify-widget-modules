package cloudify.widget.website.dao;

import cloudify.widget.website.models.PoolConfigurationModel;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/27/14
 * Time: 10:40 AM
 */
public interface IPoolDao {

    /**
     * saves model and returns ID
     * @param poolSettings
     * @return
     */
    public Long createPool( PoolConfigurationModel poolSettings );

    public void updatePool( PoolConfigurationModel poolSettings );

    public boolean deletePool( Long id );

    public PoolConfigurationModel readPool( Long id );

    public PoolConfigurationModel readPoolByAccountId( Long accountId );
}
