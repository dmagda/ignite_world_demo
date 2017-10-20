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

package demo.sql;

import java.util.Iterator;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import static demo.Params.*;

/**
 *
 */
public class SqlDataProcessing {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Ignition.setClientMode(true);

        try (Ignite ignite = Ignition.start()) {
            IgniteCache cityCache = ignite.cache(CITY_CACHE_NAME);
            IgniteCache countryCache = ignite.cache(COUNTRY_CACHE_NAME);
            IgniteCache languageCache = ignite.cache(COUNTRY_LANGUAGE_CACHE_NAME);

            getMostPopulatedCities(countryCache);
            getTopCitiesInThreeCountries(countryCache);
            getCityDetails(cityCache, 5);
        }
    }

    private static void getMostPopulatedCities(IgniteCache countryCache) {
        SqlFieldsQuery query = new SqlFieldsQuery(
            "SELECT name, population FROM country " +
            "ORDER BY population DESC LIMIT 10");

        FieldsQueryCursor<List<?>> cursor = countryCache.query(query);

        Iterator<List<?>> iterator = cursor.iterator();

        System.out.println();
        System.out.println(">>> 10 Most Populated Cities:");

        while (iterator.hasNext()) {
            List row = iterator.next();

            System.out.println("    >>> " + row.get(1) + " people live in " + row.get(0));
        }
    }

    private static void getTopCitiesInThreeCountries(IgniteCache countryCache) {
        SqlFieldsQuery query = new SqlFieldsQuery(
            "SELECT country.name, city.name, MAX(city.population) as max_pop FROM country " +
            "JOIN city ON city.countrycode = country.code " +
            "WHERE country.code IN ('USA','RUS','CHN') " +
            "GROUP BY country.name, city.name ORDER BY max_pop DESC LIMIT 3");

        FieldsQueryCursor<List<?>> cursor = countryCache.query(query);

        Iterator<List<?>> iterator = cursor.iterator();

        System.out.println();
        System.out.println(">>> 3 Most Populated Cities in US, RUS and CHN:");

        while (iterator.hasNext()) {
            List row = iterator.next();

            System.out.println("    >>> " + row.get(2) + " people live in " + row.get(1) + ", " + row.get(0));
        }
    }

    private static void getCityDetails(IgniteCache cityCache, int cityId) {
        SqlFieldsQuery query = new SqlFieldsQuery("SELECT * FROM City WHERE id = ?").setArgs(cityId);

        FieldsQueryCursor<List<?>> cursor = cityCache.query(query);

        Iterator<List<?>> iterator = cursor.iterator();

        int colCount = cursor.getColumnsCount();

        System.out.println();
        System.out.println(">>> City Info:");

        while (iterator.hasNext()) {
            List row = iterator.next();

            for (int i = 0; i < colCount; i++)
                System.out.println(row.get(i));
        }
    }
}
