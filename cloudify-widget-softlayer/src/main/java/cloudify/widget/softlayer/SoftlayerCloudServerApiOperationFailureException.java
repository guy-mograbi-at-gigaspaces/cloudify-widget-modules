package cloudify.widget.softlayer;

/**
 * User: eliranm
 * Date: 2/10/14
 * Time: 9:51 PM
 */
public class SoftlayerCloudServerApiOperationFailureException extends RuntimeException {

    public SoftlayerCloudServerApiOperationFailureException() {
        super();
    }

    public SoftlayerCloudServerApiOperationFailureException(String message) {
        super(message);
    }

    public SoftlayerCloudServerApiOperationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoftlayerCloudServerApiOperationFailureException(Throwable cause) {
        super(cause);
    }

    protected SoftlayerCloudServerApiOperationFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
