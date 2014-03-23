package cloudify.widget.website.dao;

import cloudify.widget.website.models.ResourceModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 3/20/14
 * Time: 4:36 PM
 */
public class ResourceDaoImpl implements IResourceDao{

    private JdbcTemplate jdbcTemplate;

    private LobHandler lobHandler;


    // all resource data without content
    public ResourceModel readResourceDetails(Long accountId, Long resourceId){
        return jdbcTemplate.queryForObject("select * from resource where account_id = ? and id = ?", new ResourceModelRowMapper(), accountId, resourceId);
    }

    public byte[] readResourceContent( Long accountId, Long resourceId ){
        ResourceContentRowMapper resultWrapper = new ResourceContentRowMapper( lobHandler, "resource_content");
        jdbcTemplate.queryForObject("select resource_content from resource where account_id = ? and id = ?", resultWrapper, accountId, resourceId);
        return resultWrapper.content;
    }



    public List<ResourceModel> listResourcesDetails( Long accountId ){
        return jdbcTemplate.query("select * from resource where account_id = ?",new ResourceModelRowMapper(), accountId);
    }


    @Override
    public Long create( final Long accountId, final MultipartFile file) throws IOException {
        try {
            final InputStream blobIs = file.getInputStream();
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    "INSERT INTO resources (id, account_id, resource_content, resource_name, resource_type, resource_size, resource_orig_name) VALUES (NULL, ?,?, ?,?, ?,?)",
                    new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                        protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                                throws SQLException {



                            lobCreator.setBlobAsBinaryStream(ps, 3, blobIs, (int) file.getSize());
                            ps.setLong(2,accountId);
                            ps.setString(3, file.getName());
                            ps.setString(4, file.getContentType());
                            ps.setLong(5, file.getSize());
                            ps.setString(6, file.getOriginalFilename());
                        }
                    },
                    keyHolder
            );
            blobIs.close();
            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            throw new RuntimeException("unable to upload resource because :: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete( Long accountId, Long resourceId) {
        int count = jdbcTemplate.update("delete from resources where account_id = ?, id = ?", accountId, resourceId );
        return count > 0;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LobHandler getLobHandler() {
        return lobHandler;
    }

    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }

    public static class ResourceContentRowMapper implements RowMapper<Void>{
        byte[] content;
        LobHandler lobHandler;
        String columnName;

        public ResourceContentRowMapper(LobHandler lobHandler, String columnName) {
            this.lobHandler = lobHandler;
            this.columnName = columnName;
        }

        @Override
        public Void mapRow(ResultSet resultSet, int i) throws SQLException {
            content = lobHandler.getBlobAsBytes(resultSet, columnName);
            return null;
        }
    }


    public static class ResourceModelRowMapper implements RowMapper<ResourceModel>{

        @Override
        public ResourceModel mapRow(ResultSet resultSet, int i) throws SQLException {
            ResourceModel result = new ResourceModel();
            result.setName( resultSet.getString("resource_name"));
            result.setId(resultSet.getLong("id"));
            result.setAccountId( resultSet.getLong("account_id"));
            result.setSize( resultSet.getLong("resource_size"));
            result.setContentType( resultSet.getString("resource_content_type"));
            result.setOrigName( resultSet.getString("resource_orig_name") );
            return result;
        }
    }


}
