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

/**
 *
 */
public class KeyValueBinaryDataProcessing {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Ignition.setClientMode(true);

        try (Ignite ignite = Ignition.start("config/ignite-config.xml")) {
            IgniteCache<BinaryObject, BinaryObject> cityCacheBinary = ignite.cache(CITY_CACHE_NAME).withKeepBinary();

            accessCityCache(ignite, cityCacheBinary);

            migrateBetweenCities(ignite, cityCacheBinary);
        }
    }

    /**
     * Processes data stored in a cache with binary key-value APIs.
     *
     * @param cityCache City cache.
     */
    private static void accessCityCache(Ignite ignite, IgniteCache<BinaryObject, BinaryObject> cityCache) {
        BinaryObjectBuilder cityKeyBuilder = ignite.binary().builder("demo.model.CityKey");

        cityKeyBuilder.setField("ID", 5);
        cityKeyBuilder.setField("COUNTRYCODE", "NLD");

        BinaryObject amKey = cityKeyBuilder.build();

        BinaryObject amsterdam = cityCache.get(amKey);

        System.out.println(">> Getting Amsterdam Record:");
        System.out.println(amsterdam);

        System.out.println(">> Updating Amsterdam record:");
        amsterdam = amsterdam.toBuilder().setField("POPULATION", (int)amsterdam.field("POPULATION") - 10_000).build();

        cityCache.put(amKey, amsterdam);

        System.out.println(cityCache.get(amKey));
    }

    /**
     * Moving people between cities using transactions.
     *
     * @param ignite Ignite connection reference.
     * @param cityCache City cache.
     */
    private static void migrateBetweenCities(Ignite ignite, IgniteCache<BinaryObject, BinaryObject> cityCache) {
        IgniteTransactions igniteTx = ignite.transactions();

        BinaryObjectBuilder cityKeyBuilder = ignite.binary().builder("demo.model.CityKey");

        /** Amsterdam Key. */

        cityKeyBuilder.setField("ID", 5);
        cityKeyBuilder.setField("COUNTRYCODE", "NLD");

        BinaryObject amKey = cityKeyBuilder.build();

        /** Berlin Key. */
        cityKeyBuilder.setField("ID", 3068);
        cityKeyBuilder.setField("COUNTRYCODE", "DEU");

        BinaryObject berKey = cityKeyBuilder.build();

        System.out.println();
        System.out.println(">> Moving people between Amsterdam and Berlin");

        try (Transaction tx = igniteTx.txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
            /** Moving people between cities using EntryProcessor (collocated processing). */
            cityCache.invoke(amKey, new CityEntryProcessor(true, 100_000));

            cityCache.invoke(berKey, new CityEntryProcessor(false, 100_000));
        }

        BinaryObject amsterdam = cityCache.get(amKey);
        BinaryObject berlin = cityCache.get(berKey);

        System.out.println(">> After update:");
        System.out.println(amsterdam);
        System.out.println(berlin);
    }

    /**
     * Using collocated processing approach to update an entry directly on the server side w/o deserialization.
     * */
    private static class CityEntryProcessor implements EntryProcessor<BinaryObject, BinaryObject, Object> {

        private boolean increase;

        private int delta;

        public CityEntryProcessor(boolean increase, int delta) {
            this.increase = increase;
            this.delta = delta;
        }

        @Override public Object process(MutableEntry<BinaryObject, BinaryObject> entry,
            Object... arguments) throws EntryProcessorException {

            BinaryObject val = entry.getValue();

            /** The message will be printed on the node that stores the entry! */
            System.out.println(">> Before update:");
            System.out.println(val);

            if (increase)
                val = val.toBuilder().setField("POPULATION", (int)val.field("POPULATION") + delta).build();
            else
                val = val.toBuilder().setField("POPULATION", (int)val.field("POPULATION") - delta).build();

            entry.setValue(val);

            return entry;
        }
    }
}
