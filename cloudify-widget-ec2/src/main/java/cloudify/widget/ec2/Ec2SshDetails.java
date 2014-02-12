package cloudify.widget.ec2;

import cloudify.widget.api.clouds.ISshDetails;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2SshDetails implements ISshDetails {

    private final int port;
    private final String user;
    private final String password;

    public Ec2SshDetails( int port, String user, String password ){
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
}