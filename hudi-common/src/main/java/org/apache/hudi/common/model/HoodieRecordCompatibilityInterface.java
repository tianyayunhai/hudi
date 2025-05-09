/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi.common.model;

import org.apache.hudi.common.util.Option;
import org.apache.hudi.common.util.collection.Pair;
import org.apache.hudi.keygen.BaseKeyGenerator;

import org.apache.avro.Schema;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public interface HoodieRecordCompatibilityInterface {

  /**
   * This method used to extract HoodieKey not through keyGenerator.
   */
  HoodieRecord wrapIntoHoodieRecordPayloadWithParams(
      Schema recordSchema,
      Properties props,
      Option<Pair<String, String>> simpleKeyGenFieldsOpt,
      Boolean withOperation,
      Option<String> partitionNameOp,
      Boolean populateMetaFieldsOp,
      Option<Schema> schemaWithoutMetaFields) throws IOException;

  /**
   * This method used to extract HoodieKey through keyGenerator. This method used in ClusteringExecutionStrategy.
   */
  HoodieRecord wrapIntoHoodieRecordPayloadWithKeyGen(Schema recordSchema, Properties props, Option<BaseKeyGenerator> keyGen);

  /**
   * This method used to overwrite record key field.
   */
  HoodieRecord truncateRecordKey(Schema recordSchema, Properties props, String keyFieldName) throws IOException;

  Option<HoodieAvroIndexedRecord> toIndexedRecord(Schema recordSchema, Properties props) throws IOException;

  ByteArrayOutputStream getAvroBytes(Schema recordSchema, Properties props) throws IOException;
}
