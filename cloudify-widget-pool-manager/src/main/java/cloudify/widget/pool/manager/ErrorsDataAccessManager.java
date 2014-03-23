package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.ErrorModel;
import cloudify.widget.pool.manager.dto.PoolSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 2:09 PM
 */
public class ErrorsDataAccessManager {

    private static Logger logger = LoggerFactory.getLogger(ErrorsDataAccessManager.class);

    @Autowired
    private ErrorsDao errorsDao;


    public List<ErrorModel> listErrors(PoolSettings poolSettings) {
        return errorsDao.readAllOfPool(poolSettings.getUuid());
    }

    public ErrorModel getError(long errorId) {
        return errorsDao.read(errorId);
    }

    public boolean addError(ErrorModel errorModel) {
        return errorsDao.create(errorModel);
    }

    public int removeError(long errorId) {
        return errorsDao.delete(errorId);
    }

    public int updateError(ErrorModel errorModel) {
        return errorsDao.update(errorModel);
    }


    public void setErrorsDao(ErrorsDao errorsDao) {
        this.errorsDao = errorsDao;
    }
}
