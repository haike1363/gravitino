/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.gravitino.cos.fs;

import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import java.net.URI;
import org.apache.gravitino.catalog.hadoop.fs.FileSystemUtils;
import org.apache.gravitino.catalog.hadoop.fs.GravitinoFileSystemCredentialsProvider;
import org.apache.gravitino.credential.COSSecretKeyCredential;
import org.apache.gravitino.credential.COSTokenCredential;
import org.apache.gravitino.credential.Credential;
import org.apache.hadoop.conf.Configuration;

public class COSCredentialsProvider implements com.qcloud.cos.auth.COSCredentialsProvider {

  private static final double EXPIRATION_TIME_FACTOR = 0.5D;
  private final GravitinoFileSystemCredentialsProvider gravitinoFileSystemCredentialsProvider;
  private volatile COSCredentials basicCredentials;
  private long expirationTime = Long.MAX_VALUE;

  public COSCredentialsProvider(URI uri, Configuration conf) {
    this.gravitinoFileSystemCredentialsProvider = FileSystemUtils.getGvfsCredentialProvider(conf);
  }

  @Override
  public COSCredentials getCredentials() {
    if (basicCredentials == null || System.currentTimeMillis() >= expirationTime) {
      synchronized (this) {
        refresh();
      }
    }

    return basicCredentials;
  }

  @Override
  public void refresh() {
    Credential[] gravitinoCredentials = gravitinoFileSystemCredentialsProvider.getCredentials();
    Credential credential = COSUtils.getSuitableCredential(gravitinoCredentials);
    if (credential == null) {
      throw new RuntimeException("No suitable credential for COS found...");
    }

    if (credential instanceof COSSecretKeyCredential cosSecretKeyCredential) {
      // TODO set appid
      basicCredentials =
          new BasicCOSCredentials(
              cosSecretKeyCredential.accessKeyId(), cosSecretKeyCredential.secretAccessKey());
    } else if (credential instanceof COSTokenCredential cosTokenCredential) {
      // TODO set appid
      basicCredentials =
          new BasicSessionCredentials(
              cosTokenCredential.accessKeyId(),
              cosTokenCredential.secretAccessKey(),
              cosTokenCredential.securityToken());
    }

    if (credential.expireTimeInMs() > 0) {
      expirationTime =
          System.currentTimeMillis()
              + (long)
                  ((credential.expireTimeInMs() - System.currentTimeMillis())
                      * EXPIRATION_TIME_FACTOR);
    }
  }
}
