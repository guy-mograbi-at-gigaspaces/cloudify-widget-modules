package cloudify.widget.website.dao;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.website.dao.mappers.PoolRowMapper;
import cloudify.widget.website.models.PoolConfigurationModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.SqlUpdate;

import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(PoolDaoImpl.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static{
//        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long createPool(PoolConfigurationModel poolConfiguration) {

        Long accountId = poolConfiguration.getAccountId();
        PoolSettings poolSettings = poolConfiguration.getPoolSettings();
        String poolSettingsJson = null;
        try {
            poolSettingsJson = objectMapper.writeValueAsString( poolSettings );
        } catch (IOException e) {
            if( logger.isErrorEnabled() ){
                logger.error( "Unable to parse poolSetting to JSON", e );
            }
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert( jdbcTemplate ).
                withTableName(TABLE_NAME).
                usingGeneratedKeyColumns("id");

        Map<String,Object> parametersMap = new HashMap<String,Object>(2);
        parametersMap.put( "account_id", accountId );
        parametersMap.put( "pool_setting", poolSettingsJson );

        Number id = jdbcInsert.executeAndReturnKey(parametersMap);

        return ( Long )id;
    }

    @Override
    public void updatePool( PoolConfigurationModel poolSettings ) {
        Long accountId = poolSettings.getAccountId();
        Long id = poolSettings.getId();
        PoolSettings settings = poolSettings.getPoolSettings();
        String poolSettingsJson = parsePoolSettingToJson( settings );

        jdbcTemplate.update( "update " + TABLE_NAME +
               " set account_id = ?, pool_setting = ? where id = ?", accountId, poolSettingsJson, id );
    }

    @Override
    public boolean deletePool( Long id ) {
        String delQuery = "delete from " + TABLE_NAME + " where id = ?";
        int count = jdbcTemplate.update(delQuery, new Object[]{id});
        return count > 0;
    }

    @Override
    public PoolConfigurationModel readPool(Long id) {

        String sql = "select * from " + TABLE_NAME + " where id = ?";

        PoolConfigurationModel poolConfigurationModel  =
                ( PoolConfigurationModel )jdbcTemplate.queryForObject(sql, new Object[]{id}, new PoolRowMapper( objectMapper ));
        return poolConfigurationModel;
    }

    private static String parsePoolSettingToJson( PoolSettings poolSettings ){

        String poolSettingsJson = null;
        try {
            poolSettingsJson = objectMapper.writeValueAsString( poolSettings );
        } catch (IOException e) {
            if( logger.isErrorEnabled() ){
                logger.error( "Unable to parse poolSetting to JSON", e );
            }
        }
        return poolSettingsJson;
    }
}