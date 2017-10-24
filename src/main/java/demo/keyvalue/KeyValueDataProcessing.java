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
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;

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

            accessCityCache(cityCache);

            migrateBetweenCities(ignite, cityCache);
        }
    }

    /**
     * Processes data stored in a cache with key-value APIs.
     *
     * @param cityCache City cache.
     */
    private static void accessCityCache(IgniteCache<CityKey, City> cityCache) {
        CityKey key = new CityKey(5, "NLD");
        City city = cityCache.get(key);

        System.out.println(">> Getting Amsterdam Record:");
        System.out.println(city);

        System.out.println(">> Updating Amsterdam record:");
        city.setPopulation(city.getPopulation() - 10_000);

        cityCache.put(key, city);

        System.out.println(cityCache.get(key));
    }

    /**
     * Moving people between cities using transactions.
     *
     * @param ignite Ignite connection reference.
     * @param cityCache City cache.
     */
    private static void migrateBetweenCities(Ignite ignite, IgniteCache<CityKey, City> cityCache) {
        IgniteTransactions igniteTx = ignite.transactions();

        /** Amsterdam Key. */
        CityKey amKey = new CityKey(5, "NLD");

        /** Berlin Key. */
        CityKey berKey = new CityKey(3068, "DEU");

        System.out.println();
        System.out.println(">> Moving people between Amsterdam and Berlin");

        try (Transaction tx = igniteTx.txStart(TransactionConcurrency.PESSIMISTIC,TransactionIsolation.REPEATABLE_READ)) {
            City amsterdam = cityCache.get(amKey);
            City berlin = cityCache.get(berKey);

            System.out.println(">> Before update:");
            System.out.println(amsterdam);
            System.out.println(berlin);

            /** Moving people between cities. **/
            amsterdam.setPopulation(amsterdam.getPopulation() + 100_000);
            berlin.setPopulation(berlin.getPopulation() - 100_000);

            /** Applying changes in a transactional fashion. */
            cityCache.put(amKey, amsterdam);
            cityCache.put(berKey, berlin);
        }

        City amsterdam = cityCache.get(amKey);
        City berlin = cityCache.get(berKey);

        System.out.println(">> After update:");
        System.out.println(amsterdam);
        System.out.println(berlin);
    }
}
