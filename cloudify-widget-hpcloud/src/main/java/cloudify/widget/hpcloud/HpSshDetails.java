package cloudify.widget.hpcloud;

import cloudify.widget.api.clouds.ISshDetails;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/6/14
 * Time: 2:17 PM
 */
public class HpSshDetails implements ISshDetails {
    public String username;
    public int port;
    public File privateKey;
}
