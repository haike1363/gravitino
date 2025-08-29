/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.gravitino.cos.fs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import org.apache.gravitino.catalog.hadoop.fs.FileSystemProvider;
import org.apache.gravitino.catalog.hadoop.fs.FileSystemUtils;
import org.apache.gravitino.catalog.hadoop.fs.SupportsCredentialVending;
import org.apache.gravitino.credential.COSSecretKeyCredential;
import org.apache.gravitino.credential.COSTokenCredential;
import org.apache.gravitino.credential.Credential;
import org.apache.gravitino.storage.COSProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CosNFileSystem;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.auth.SessionTokenCredentialProvider;

public class COSFileSystemProvider implements FileSystemProvider, SupportsCredentialVending {

  @VisibleForTesting
  public static final Map<String, String> GRAVITINO_KEY_TO_COS_HADOOP_KEY =
      ImmutableMap.of(
          COSProperties.GRAVITINO_COS_ACCESS_KEY_ID, "fs.cosn.userinfo.secretId",
          COSProperties.GRAVITINO_COS_SECRET_ACCESS_KEY, "fs.cosn.userinfo.secretKey");

  private static final String COS_FILESYSTEM_IMPL = "fs.cosn.impl";

  // This map maintains the mapping relationship between the COS properties in Gravitino and
  // the Hadoop properties. Through this map, users can customize the COS properties in Gravitino
  // and map them to the corresponding Hadoop properties.
  // For example, User can use oss-endpoint to set the endpoint of COS 'fs.oss.endpoint' in
  // Gravitino.
  // GCS and S3 also have similar mapping relationship.
  private static final String CREDENTIALS_PROVIDER_KEY = "fs.cosn.credentials.provider";

  @Override
  public FileSystem getFileSystem(Path path, Map<String, String> config) throws IOException {
    Map<String, String> hadoopConfMap =
        FileSystemUtils.toHadoopConfigMap(config, GRAVITINO_KEY_TO_COS_HADOOP_KEY);
    // COS do not use service loader to load the file system, so we need to set the impl class
    if (!hadoopConfMap.containsKey(COS_FILESYSTEM_IMPL)) {
      hadoopConfMap.put(COS_FILESYSTEM_IMPL, CosNFileSystem.class.getCanonicalName());
    }
    hadoopConfMap.putAll(COSUtils.getCosConfig(config));
    Configuration configuration = FileSystemUtils.createConfiguration(hadoopConfMap);

    return CosNFileSystem.newInstance(path.toUri(), configuration);
  }

  @Override
  public Map<String, String> getFileSystemCredentialConf(Credential[] credentials) {
    Credential credential = COSUtils.getSuitableCredential(credentials);
    Map<String, String> result = Maps.newHashMap();
    if (credential instanceof COSSecretKeyCredential || credential instanceof COSTokenCredential) {
      result.put(CREDENTIALS_PROVIDER_KEY, SessionTokenCredentialProvider.class.getCanonicalName());
    }

    return result;
  }

  @Override
  public String scheme() {
    return "cosn";
  }

  @Override
  public String name() {
    return "cos";
  }
}
