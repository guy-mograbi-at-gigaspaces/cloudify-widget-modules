package cloudify.widget.pool.manager;

public  class MachineId{
    public String ip;

    public MachineId(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MachineId machineId = (MachineId) o;

        if (ip != null ? !ip.equals(machineId.ip) : machineId.ip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ip != null ? ip.hashCode() : 0;
    }
}