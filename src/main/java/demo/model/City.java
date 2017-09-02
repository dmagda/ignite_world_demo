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

package demo.model;

/**
 * City object class.
 */
public class City {
    /** */
    private String name;

    /** */
    private String district;

    /** */
    private int population;

    /**
     * Constructor.
     *
     * @param name
     * @param district
     * @param population
     */
    public City(String name, String district, int population) {
        this.name = name;
        this.district = district;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public int getPopulation() {
        return population;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "City{" +
            "name='" + name + '\'' +
            ", district='" + district + '\'' +
            ", population=" + population +
            '}';
    }
}
