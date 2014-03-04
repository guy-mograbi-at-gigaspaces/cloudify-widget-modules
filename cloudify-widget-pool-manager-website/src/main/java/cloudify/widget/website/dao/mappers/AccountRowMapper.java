package cloudify.widget.website.dao.mappers;

import cloudify.widget.pool.manager.PoolSettings;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: evgenyf
 * Date: 3/2/14
 */
public class AccountRowMapper implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

        AccountModel accountModel = new AccountModel();
        accountModel.setId( rs.getLong("id") );
        accountModel.setUuid( rs.getString("uuid") );

        return accountModel;
    }
}