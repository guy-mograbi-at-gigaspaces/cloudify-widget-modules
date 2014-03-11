package cloudify.widget.pool.manager.dto;

/**
 * Status for nodes are sequential. The ordinal for a node status is represented by {@link #getOrdinal()}.
 * (we don't use {@link #ordinal()} to avoid breaking this behavior if constant order is changed).
 */
public enum NodeStatus {

    CREATED         (100),
    BOOTSTRAPPED    (200),
    OCCUPIED        (300);

    private final int ordinalValue;

    private NodeStatus(int ordinalValue) {
        this.ordinalValue = ordinalValue;
    }

    public int getOrdinal() {
        return ordinalValue;
    }
}
