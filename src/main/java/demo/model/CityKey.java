package demo.model;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

/**
 * City key class to properly work with city objects using key-value and compute APIs.
 * No need to implement hashCode or equals. Ignite does this internally on top of the serialized data (BinaryObject).
 */
public class CityKey {
    /** */
    private int ID;

    /** */
    @AffinityKeyMapped
    private String COUNTRYCODE;

    /**
     * Constructor.
     *
     * @param id City ID.
     * @param countryCode Country code (affinity key).
     */
    public CityKey(int id, String countryCode) {
        this.ID = id;
        this.COUNTRYCODE = countryCode;
    }
}
