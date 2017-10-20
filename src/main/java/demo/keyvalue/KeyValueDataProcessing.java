/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.keyvalue;

import demo.model.City;
import demo.model.CityKey;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

import static demo.Params.CITY_CACHE_NAME;
import static demo.Params.COUNTRY_CACHE_NAME;
import static demo.Params.COUNTRY_LANGUAGE_CACHE_NAME;

/**
 *
 */
public class KeyValueDataProcessing {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Ignition.setClientMode(true);

        try (Ignite ignite = Ignition.start("config/ignite-config.xml")) {
            IgniteCache<CityKey, City> cityCache = ignite.cache(CITY_CACHE_NAME);
            IgniteCache<BinaryObject, BinaryObject> cityCacheBinary = cityCache.withKeepBinary();

            IgniteCache countryCache = ignite.cache(COUNTRY_CACHE_NAME);
            IgniteCache languageCache = ignite.cache(COUNTRY_LANGUAGE_CACHE_NAME);

            accessCityCacheBinary(ignite, cityCacheBinary);

            accessCityCache(cityCache);
        }
    }

    /**
     * Processes data stored in a cache with key-value APIs.
     *
     * @param cityCache City cache.
     */
    private static void accessCityCache(IgniteCache<CityKey, City> cityCache) {
        System.out.println("Getting Amsterdam Record");
        System.out.println("HashCode = " + new CityKey(5, "NLD").hashCode());

        System.out.println(cityCache.get(new CityKey(5, "NLD")));
    }

    /**
     * Processes data stored in a cache with key-value APIs.
     *
     * @param cityCache City cache.
     */
    private static void accessCityCacheBinary(Ignite ignite, IgniteCache<BinaryObject, BinaryObject> cityCache) {
        BinaryObjectBuilder cityKeyBuilder = ignite.binary().builder("CityKey");

        cityKeyBuilder.setField("id", 5);
        cityKeyBuilder.setField("countryCode", "NLD");

        BinaryObject cityKey = cityKeyBuilder.build();

        System.out.println("Getting Amsterdam Record");
        System.out.println("HashCode = " + cityKey.hashCode());

        BinaryObject city = cityCache.get(cityKey);

        System.out.println("Binary " + city);
        System.out.println("Deserialized " + city.deserialize());
    }
}
