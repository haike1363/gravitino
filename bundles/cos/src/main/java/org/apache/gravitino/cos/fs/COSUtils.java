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

import java.util.HashMap;
import java.util.Map;
import org.apache.gravitino.credential.COSSecretKeyCredential;
import org.apache.gravitino.credential.COSTokenCredential;
import org.apache.gravitino.credential.Credential;

public class COSUtils {

  /**
   * Get the credential from the credential array. Using dynamic credential first, if not found,
   * uses static credential.
   *
   * @param credentials The credential array.
   * @return A credential. Null if not found.
   */
  static Credential getSuitableCredential(Credential[] credentials) {
    // Use dynamic credential if found.
    for (Credential credential : credentials) {
      if (credential instanceof COSTokenCredential) {
        return credential;
      }
    }

    // If dynamic credential not found, use the static one
    for (Credential credential : credentials) {
      if (credential instanceof COSSecretKeyCredential) {
        return credential;
      }
    }

    return null;
  }

  /**
   * Get the COS configuration from the configuration map.
   *
   * @param config The configuration map.
   * @return The COS configuration map.
   */
  static Map<String, String> getCosConfig(Map<String, String> config) {
    Map<String, String> result = new HashMap<>();
    if (config != null) {
      config.forEach((k, v) -> {
        if (k.startsWith("fs.cos.") || k.startsWith("fs.cosn.")) {
          result.put(k, v);
        }
      });
    }
    return result;
  }
}
