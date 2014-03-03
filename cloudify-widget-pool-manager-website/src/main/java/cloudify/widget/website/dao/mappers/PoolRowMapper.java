package cloudify.widget.website.dao.mappers;

import cloudify.widget.pool.manager.PoolSettings;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: evgenyf
 * Date: 3/2/14
 */
public class PoolRowMapper implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

        PoolConfigurationModel poolConfigurationModel = new PoolConfigurationModel();
        poolConfigurationModel.setId( rs.getLong("id") );
        PoolSettings poolSettings = new PoolSettings();
        poolSettings.name = rs.getString( "pool_setting" );

        poolConfigurationModel.setPoolSettings( poolSettings );
        poolConfigurationModel.setAccountId( rs.getLong( "account_id" ) );

        return poolConfigurationModel;
    }
}