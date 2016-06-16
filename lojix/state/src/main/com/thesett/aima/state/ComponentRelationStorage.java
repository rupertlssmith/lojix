/*
 * Copyright The Sett Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.aima.state;

/**
 * Describes how a relationship between components is persisted.
 *
 * <p/>In the case where a component holds a reference to an entity, by an exposed id field, the field to use for the id
 * is named.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Describe how a component relationship is stored. </td></tr>
 * <tr><td> Allow an id field to be named, for reference by id. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ComponentRelationStorage
{
    private final StorageType storageType;
    private final String externalIdField;

    public ComponentRelationStorage(StorageType storageType, String externalIdField)
    {
        this.storageType = storageType;
        this.externalIdField = externalIdField;
    }

    public ComponentRelationStorage(StorageType storageType)
    {
        this.storageType = storageType;
        this.externalIdField = null;
    }

    public StorageType getStorageType()
    {
        return storageType;
    }

    public String getExternalIdField()
    {
        return externalIdField;
    }
}
