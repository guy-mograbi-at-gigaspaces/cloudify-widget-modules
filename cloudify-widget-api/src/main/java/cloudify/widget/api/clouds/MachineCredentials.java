package cloudify.widget.api.clouds;

/**
 * A representation of the machine credentials in the time of creation. Machine credentials are available
 * after the node is created, but are not guaranteed to be persisted to the node metadata
 * later in the node's lifecycle.
 *
 * User: eliranm
 * Date: 4/1/14
 * Time: 3:23 PM
 */
public class MachineCredentials {

    private String user;
    private String password;
    private String privateKey;

    public MachineCredentials() {
    }

    public String getUser() {
        return user;
    }

    public MachineCredentials setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MachineCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public MachineCredentials setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MachineCredentials that = (MachineCredentials) o;

        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MachineCredentials{" +
                "user='" + user + '\'' +
                ", password='***'" +
                ", privateKey='###'" +
                '}';
    }
}
