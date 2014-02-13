package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.ISshDetails;

/**
 * User: evgenyf
 * Date: 2/10/14
 */
public class SoftlayerSshDetails implements ISshDetails {

    private final int port;
    private final String user;
    private final String password;

    public SoftlayerSshDetails( int port, String user, String password ){
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public int port(){
        return port;
    }

    public String user(){
        return user;
    }

    public String password(){
        return password;
    }

    @Override
    public String toString() {
        return "SoftlayerSshDetails{" +
                "port=" + port +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}