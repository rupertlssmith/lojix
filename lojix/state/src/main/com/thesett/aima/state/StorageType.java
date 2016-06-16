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
 * StorageType describes the different ways of storing relationships between components.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Describe different ways of storing relationships between entities and components.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public enum StorageType
{
    /** The default style for entity-entity relationships. */
    ForeignKey,

    /** The default style for entity-component relationships; the component is embedded. */
    EmbeddedInline,

    /** Alternate style for entity-component relationships; the component is embedded as a json document. */
    DocJson,

    /** Alternate style for entity-component relationships; the component is embedded as an xml document. */
    DocXml,

    /** Alternate style for entity-component relationships; the component is embedded as a serialized blob. */
    ObjectJavaSerialisation,

    /** Default style for component-entity relationships, an external id field is used to identify the entity. */
    ExternalId
}
