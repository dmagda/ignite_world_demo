package demo.model;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

/**
 * City key class to properly work with city objects using key-value and compute APIs.
 */
public class CityKey {
    /** */
    private int id;

    /** */
    @AffinityKeyMapped
    private String countryCode;

    /**
     * Constructor.
     *
     * @param id City ID.
     * @param countryCode Country code (affinity key).
     */
    public CityKey(int id, String countryCode) {
        this.id = id;
        this.countryCode = countryCode;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CityKey key = (CityKey)o;

        if (id != key.id)
            return false;

        return countryCode != null ? countryCode.equals(key.countryCode) : key.countryCode == null;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        int result = id;

        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);

        return result;
    }
}
