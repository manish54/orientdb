/*
 * Copyright 2012 Orient Technologies.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.core.index;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.core.index.engine.OMVRBTreeIndexEngine;
import com.orientechnologies.orient.core.index.engine.OSBTreeIndexEngine;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Default OrientDB index factory for indexes based on MVRBTree.<br>
 * Supports index types :
 * <ul>
 * <li>UNIQUE</li>
 * <li>NOTUNIQUE</li>
 * <li>FULLTEXT</li>
 * <li>DICTIONARY</li>
 * </ul>
 */
public class ODefaultIndexFactory implements OIndexFactory {

  public static final String       SBTREE_ALGORITHM   = "SBTREE";
  public static final String       MVRBTREE_ALGORITHM = "MVRBTREE";

  private static final Set<String> TYPES;
  static {
    final Set<String> types = new HashSet<String>();
    types.add(OClass.INDEX_TYPE.UNIQUE.toString());
    types.add(OClass.INDEX_TYPE.NOTUNIQUE.toString());
    types.add(OClass.INDEX_TYPE.FULLTEXT.toString());
    types.add(OClass.INDEX_TYPE.DICTIONARY.toString());
    TYPES = Collections.unmodifiableSet(types);
  }

  /**
   * Index types :
   * <ul>
   * <li>UNIQUE</li>
   * <li>NOTUNIQUE</li>
   * <li>FULLTEXT</li>
   * <li>DICTIONARY</li>
   * </ul>
   */
  public Set<String> getTypes() {
    return TYPES;
  }

  public OIndexInternal<?> createIndex(ODatabaseRecord database, String indexType, String algorithm) throws OConfigurationException {
    if (SBTREE_ALGORITHM.equals(algorithm))
      return createSBTreeIndex(indexType);

    if (MVRBTREE_ALGORITHM.equals(algorithm) || algorithm == null)
      return createMRBTreeIndex(indexType);

    throw new OConfigurationException("Unsupported type : " + indexType);
  }

  private OIndexInternal<?> createMRBTreeIndex(String indexType) {
    if (OClass.INDEX_TYPE.UNIQUE.toString().equals(indexType)) {
      return new OIndexUnique(indexType, MVRBTREE_ALGORITHM, new OMVRBTreeIndexEngine<OIdentifiable>());
    } else if (OClass.INDEX_TYPE.NOTUNIQUE.toString().equals(indexType)) {
      return new OIndexNotUnique(indexType, MVRBTREE_ALGORITHM, new OMVRBTreeIndexEngine<Set<OIdentifiable>>());
    } else if (OClass.INDEX_TYPE.FULLTEXT.toString().equals(indexType)) {
      return new OIndexFullText(indexType, MVRBTREE_ALGORITHM, new OMVRBTreeIndexEngine<Set<OIdentifiable>>());
    } else if (OClass.INDEX_TYPE.DICTIONARY.toString().equals(indexType)) {
      return new OIndexDictionary(indexType, MVRBTREE_ALGORITHM, new OMVRBTreeIndexEngine<OIdentifiable>());
    }

    throw new OConfigurationException("Unsupported type : " + indexType);
  }

  private OIndexInternal<?> createSBTreeIndex(String indexType) {
    if (OClass.INDEX_TYPE.UNIQUE.toString().equals(indexType)) {
      return new OIndexUnique(indexType, SBTREE_ALGORITHM, new OSBTreeIndexEngine<OIdentifiable>());
    } else if (OClass.INDEX_TYPE.NOTUNIQUE.toString().equals(indexType)) {
      return new OIndexNotUnique(indexType, SBTREE_ALGORITHM, new OSBTreeIndexEngine<Set<OIdentifiable>>());
    } else if (OClass.INDEX_TYPE.FULLTEXT.toString().equals(indexType)) {
      return new OIndexFullText(indexType, SBTREE_ALGORITHM, new OSBTreeIndexEngine<Set<OIdentifiable>>());
    } else if (OClass.INDEX_TYPE.DICTIONARY.toString().equals(indexType)) {
      return new OIndexDictionary(indexType, SBTREE_ALGORITHM, new OSBTreeIndexEngine<OIdentifiable>());
    }

    throw new OConfigurationException("Unsupported type : " + indexType);
  }
}
