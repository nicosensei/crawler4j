/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.crawler4j.crawler.traps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.nicosensei.commons.bdb.AbstractBDB;
import com.github.nicosensei.commons.exceptions.Unexpected;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

/**
 * Stores crawler traps in a BDB, and offers access methods.
 *
 * @author ngiraud
 *
 */
public class CrawlerTrapsDB extends AbstractBDB {

    /**
     * The class logger
     */
    private static final Logger LOGGER = Logger.getLogger(CrawlerTrapsDB.class);

    /**
     * Default cache percentage.
     * TODO configurable.
     */
    private static final int DEFAULT_CACHE_PERCENTAGE = 10;

    /**
     * The entity store.
     */
    private EntityStore trapStore;

    /**
     * The primary index.
     */
    private PrimaryIndex<String, CrawlerTrap> trapsByPattern;

    /**
     * Absolute path to the folder where BDB files should be stored.
     */
    private final String storageFolderPath;

    /**
     *
     */
    public CrawlerTrapsDB(final String storageFolderPath) {
        super();
        this.storageFolderPath = storageFolderPath;
        startEnvironment();
    }

    /**
     * Closes the environment.
     */
    public void close() {
        stopEnvironment();
    }

    @Override
    public String getStorageFolderPath() {
        return storageFolderPath;
    }

    @Override
    public int getCachePercentage() {
        return DEFAULT_CACHE_PERCENTAGE;
    }

    @Override
    protected void closeStores() {
        if (trapStore != null) {
            try {
                trapStore.close();
            } catch (final DatabaseException e) {
                LOGGER.error(e);
            }
        }
    }

    @Override
    protected void initStores(Environment dbEnv) {
        try {
            StoreConfig storeCfg = new StoreConfig();
            List<String> dbNames = dbEnv.getDatabaseNames();
            boolean allowCreate = dbNames.isEmpty();
            storeCfg.setAllowCreate(allowCreate);

            trapStore = new EntityStore(
                    dbEnv, CrawlerTrap.class.getSimpleName(), storeCfg);

            trapsByPattern = trapStore.getPrimaryIndex(String.class, CrawlerTrap.class);

            LOGGER.info("Initialized crawler traps store (allowCreate="
                    + allowCreate + ").");
        } catch (final DatabaseException e) {
            LOGGER.error(e);
            throw new Unexpected(e); // TODO proper exception handling
        }

    }

    /**
     * Adds crawler traps to the DB from a flat text file,
     * assuming there is a regular expression on every line.
     * @param absoluteFilePath path to the input file
     * @throws IOException if an IO error occurs during the processing.
     */
    public void addCrawlerTrapsFromFile(final String absoluteFilePath)
            throws IOException {

        File trapFile = new File(absoluteFilePath);
        String trapListName = trapFile.getName();
        BufferedReader br = new BufferedReader(new FileReader(absoluteFilePath));
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                trapsByPattern.putNoReturn(new CrawlerTrap(trapListName, line));
            }
        } finally {
            br.close();
        }
    }

    /**
     * Tests if the given URL is a crawler trap.
     * @param url the URL to test
     * @return whether the given URL is a crawler trap.
     */
    public boolean isCrawlerTrap(String url) {
        EntityCursor<CrawlerTrap> traps = trapsByPattern.entities();
        try {
            for (CrawlerTrap t : traps) {
                if (t.matches(url)) {
                    return true;
                }
            }
            return false;
        } finally {
            traps.close();
        }
    }
}
