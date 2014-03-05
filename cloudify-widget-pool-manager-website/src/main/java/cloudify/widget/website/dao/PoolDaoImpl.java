package cloudify.widget.website.dao;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.website.dao.mappers.PoolRowMapper;
import cloudify.widget.website.models.PoolConfigurationModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: evgenyf
 * Date: 2/27/14
 */
public class PoolDaoImpl implements IPoolDao {

    private static final String TABLE_NAME = "pool_configuration";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private final static String delQuery = "delete from " + TABLE_NAME + " where id = ?";
    private final static String selectSqlById = "select * from " + TABLE_NAME + " where id = ?";
    private final static String selectSqlByAccountId = "select * from " + TABLE_NAME + " where account_id = ?";

    private static final Logger logger = LoggerFactory.getLogger(PoolDaoImpl.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static{
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        jdbcInsert = new SimpleJdbcInsert( jdbcTemplate ).
                withTableName(TABLE_NAME).
                usingGeneratedKeyColumns("id");
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
        String poolSettingsJson = parsePoolSettingToJson(settings);

        jdbcTemplate.update( "update " + TABLE_NAME +
               " set account_id = ?, pool_setting = ? where id = ?", accountId, poolSettingsJson, id );
    }

    @Override
    public boolean deletePool( Long id ) {
        int count = jdbcTemplate.update(delQuery, new Object[]{id});
        return count > 0;
    }

    @Override
    public PoolConfigurationModel readPool(Long id) {
        logger.info( "select query is [{}]", selectSqlById );
        PoolConfigurationModel poolConfigurationModel =( PoolConfigurationModel )
                jdbcTemplate.queryForObject(selectSqlById, new Object[]{id}, new PoolRowMapper( objectMapper ));
        return poolConfigurationModel;
    }

    @Override
    public PoolConfigurationModel readPoolByAccountId(Long accountId) {
        logger.info( "select query is [{}] accountId [{}]", selectSqlByAccountId, accountId );
        PoolConfigurationModel poolConfigurationModel =( PoolConfigurationModel )jdbcTemplate.queryForObject(
                selectSqlByAccountId, new Object[]{accountId}, new PoolRowMapper( objectMapper ));
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