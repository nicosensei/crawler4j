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

import java.util.regex.Pattern;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * A crawler trap specifies an URI pattern that the crawler should not process, as a
 * regular expression. Traps are grouped in lists, which are identified by a unique name.
 *
 * @author ngiraud
 *
 */
@Entity
public class CrawlerTrap implements Comparable<CrawlerTrap> {

    /**
     * The regular expression.
     */
    @PrimaryKey
    private String pattern;

    /**
     * The name of the parent list.
     */
    @SecondaryKey(relate=Relationship.MANY_TO_ONE)
    private String trapListName;

    /**
     * Default empty constructor (required for an {@link Entity})
     */
    public CrawlerTrap() {

    }

    /**
     * Constructor.
     * @param trapListName the list name
     * @param pattern the regulmar expression
     */
    public CrawlerTrap(
            final String trapListName,
            final String pattern) {
        this.trapListName = trapListName;
        this.pattern = pattern;
    }

    /**
     * Tests an URI against the regexp.
     * @param uri the string to test
     * @return true if the expression is matched, false otherwise.
     */
    public boolean matches(String uri) {
        return Pattern.matches(pattern, uri);
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the trapListName
     */
    public String getTrapListName() {
        return trapListName;
    }

    /**
     * @param trapListName the trapListName to set
     */
    public void setTrapListName(String trapListName) {
        this.trapListName = trapListName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((pattern == null) ? 0 : pattern.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CrawlerTrap other = (CrawlerTrap) obj;
        if (pattern == null) {
            if (other.pattern != null) {
                return false;
            }
        } else if (!pattern.equals(other.pattern)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CrawlerTrap o) {
        if (pattern == null || o.pattern == null) {
            throw new NullPointerException();
        }
        return pattern.compareTo(o.pattern);
    }

}