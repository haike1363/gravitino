/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.gravitino.credential;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/** COS token credential. */
public class COSTokenCredential implements Credential {

  /** COS token credential type. */
  public static final String COS_TOKEN_CREDENTIAL_TYPE = "cos-token";
  /** COS access key ID used to access COS data. */
  public static final String GRAVITINO_COS_SESSION_ACCESS_KEY_ID = "cos-access-key-id";
  /** COS secret access key used to access COS data. */
  public static final String GRAVITINO_COS_SESSION_SECRET_ACCESS_KEY = "cos-secret-access-key";
  /** COS security token. */
  public static final String GRAVITINO_COS_TOKEN = "cos-security-token";

  private String accessKeyId;
  private String secretAccessKey;
  private String securityToken;
  private long expireTimeInMS;

  /**
   * Constructs an instance of {@link COSTokenCredential} with secret key and token.
   *
   * @param accessKeyId The cos access key ID.
   * @param secretAccessKey The cos secret access key.
   * @param securityToken The cos security token.
   * @param expireTimeInMS The cos token expire time in ms.
   */
  public COSTokenCredential(
      String accessKeyId, String secretAccessKey, String securityToken, long expireTimeInMS) {
    validate(accessKeyId, secretAccessKey, securityToken, expireTimeInMS);
    this.accessKeyId = accessKeyId;
    this.secretAccessKey = secretAccessKey;
    this.securityToken = securityToken;
    this.expireTimeInMS = expireTimeInMS;
  }

  /**
   * This is the constructor that is used by credential factory to create an instance of credential
   * according to the credential information.
   */
  public COSTokenCredential() {}

  @Override
  public String credentialType() {
    return COS_TOKEN_CREDENTIAL_TYPE;
  }

  @Override
  public long expireTimeInMs() {
    return expireTimeInMS;
  }

  @Override
  public Map<String, String> credentialInfo() {
    return new ImmutableMap.Builder<String, String>()
        .put(GRAVITINO_COS_SESSION_ACCESS_KEY_ID, accessKeyId)
        .put(GRAVITINO_COS_SESSION_SECRET_ACCESS_KEY, secretAccessKey)
        .put(GRAVITINO_COS_TOKEN, securityToken)
        .build();
  }

  @Override
  public void initialize(Map<String, String> credentialInfo, long expireTimeInMS) {
    String accessKeyId = credentialInfo.get(GRAVITINO_COS_SESSION_ACCESS_KEY_ID);
    String secretAccessKey = credentialInfo.get(GRAVITINO_COS_SESSION_SECRET_ACCESS_KEY);
    String securityToken = credentialInfo.get(GRAVITINO_COS_TOKEN);

    validate(accessKeyId, secretAccessKey, securityToken, expireTimeInMS);

    this.accessKeyId = accessKeyId;
    this.secretAccessKey = secretAccessKey;
    this.securityToken = securityToken;
    this.expireTimeInMS = expireTimeInMS;
  }

  /**
   * Get cos access key ID.
   *
   * @return The cos access key ID.
   */
  public String accessKeyId() {
    return accessKeyId;
  }

  /**
   * Get cos secret access key.
   *
   * @return The cos secret access key.
   */
  public String secretAccessKey() {
    return secretAccessKey;
  }

  /**
   * Get cos security token.
   *
   * @return The cos security token.
   */
  public String securityToken() {
    return securityToken;
  }

  private void validate(
      String accessKeyId, String secretAccessKey, String sessionToken, long expireTimeInMS) {
    Preconditions.checkArgument(
        StringUtils.isNotBlank(accessKeyId), "COS access key Id should not be empty");
    Preconditions.checkArgument(
        StringUtils.isNotBlank(secretAccessKey), "COS secret access key should not be empty");
    Preconditions.checkArgument(
        StringUtils.isNotBlank(sessionToken), "COS session token should not be empty");
    Preconditions.checkArgument(
        expireTimeInMS > 0, "The expire time of COSTokenCredential should great than 0");
  }
}
