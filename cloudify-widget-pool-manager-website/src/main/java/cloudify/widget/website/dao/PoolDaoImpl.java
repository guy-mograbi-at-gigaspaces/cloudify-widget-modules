package cloudify.widget.website.dao;

import cloudify.widget.pool.manager.PoolSettings;
import cloudify.widget.website.dao.mappers.PoolRowMapper;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * User: evgenyf
 * Date: 2/27/14
 */
public class PoolDaoImpl implements IPoolDao {

    private static final String TABLE_NAME = "pool_configuration";

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long createPool(PoolConfigurationModel poolConfiguration) {

        Long accountId = poolConfiguration.getAccountId();
        PoolSettings poolSettings = poolConfiguration.getPoolSettings();
        String poolSettingsName = poolSettings.name;

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert( jdbcTemplate ).
                withTableName(TABLE_NAME).
                usingGeneratedKeyColumns("id");

        Map<String,Object> parametersMap = new HashMap<String,Object>(2);
        parametersMap.put( "account_id", accountId );
        parametersMap.put( "pool_setting", poolSettingsName );

        Number id = jdbcInsert.executeAndReturnKey(parametersMap);

        return ( Long )id;
    }

    @Override
    public void updatePool(PoolConfigurationModel poolSettings) {

    }

    @Override
    public boolean deletePool( Long id ) {
        String delQuery = "delete from " + TABLE_NAME + " where id = ?";
        int count = jdbcTemplate.update(delQuery, new Object[] { id });
        return count > 0;
    }

    @Override
    public PoolConfigurationModel readPool(Long id) {

        String sql = "select * from " + TABLE_NAME + " where id = ?";

        PoolConfigurationModel poolConfigurationModel  =
                ( PoolConfigurationModel )jdbcTemplate.queryForObject(sql, new Object[]{id}, new PoolRowMapper());
        return poolConfigurationModel;
    }
}