package cloudify.widget.pool.manager.dto;

import java.util.*;

/**
 * User: eliranm
 * Date: 2/27/14
 * Time: 3:21 PM
 */
public class ManagerSettings {

/*
    public static class Pools {

        private final List<PoolSettings> list = new LinkedList<PoolSettings>();

        public List<PoolSettings> getList() {
            return list;
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public boolean containsAll(Collection<?> c) {
            return list.containsAll(c);
        }

        public ListIterator<PoolSettings> listIterator() {
            return list.listIterator();
        }

        public List<PoolSettings> subList(int fromIndex, int toIndex) {
            return list.subList(fromIndex, toIndex);
        }

        public boolean equals(Object o) {
            return list.equals(o);
        }

        public int hashCode() {
            return list.hashCode();
        }

        public Iterator<PoolSettings> iterator() {
            return list.iterator();
        }

        public boolean contains(Object o) {
            return list.contains(o);
        }

        public int size() {
            return list.size();
        }

        public PoolSettings get(int index) {
            return list.get(index);
        }

        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        public ListIterator<PoolSettings> listIterator(int index) {
            return list.listIterator(index);
        }
    }
*/

    private List<PoolSettings> pools = new LinkedList<PoolSettings>();

    public List<PoolSettings> getPools() {
        return pools;
    }

    public void setPools(List<PoolSettings> pools) {
        this.pools = pools;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerSettings that = (ManagerSettings) o;
        if (!pools.equals(that.pools)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return pools.hashCode();
    }
}
