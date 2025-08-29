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
package org.apache.gravitino.storage;

// Defines the unified COS properties for different catalogs and connectors.
public class COSProperties {
  // An alternative endpoint of the COS service, This could be used to for COSFileIO with any
  // cos-compatible object storage service that has a different endpoint, or access a private COS
  // endpoint in a virtual private cloud
  public static final String GRAVITINO_COS_ENDPOINT = "cos-endpoint";
  // The static access key ID used to access COS data.
  public static final String GRAVITINO_COS_ACCESS_KEY_ID = "cos-access-key-id";
  // The static secret access key used to access COS data.
  public static final String GRAVITINO_COS_SECRET_ACCESS_KEY = "cos-secret-access-key";
  // The region of the COS service.
  public static final String GRAVITINO_COS_REGION = "cos-region";
  // COS role arn
  public static final String GRAVITINO_COS_ROLE_ARN = "cos-role-arn";

  public static final String GRAVITINO_COS_STS_ENDPOINT = "cos-token-service-endpoint";
  // COS external id
  public static final String GRAVITINO_COS_EXTERNAL_ID = "cos-external-id";

  // The COS credentials provider class name.
  public static final String GRAVITINO_COS_CREDS_PROVIDER = "cos-creds-provider";

  // The COS path style access flag.
  public static final String GRAVITINO_COS_PATH_STYLE_ACCESS = "cos-path-style-access";

  private COSProperties() {}
}
