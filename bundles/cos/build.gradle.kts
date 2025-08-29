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
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  `maven-publish`
  id("java")
  alias(libs.plugins.shadow)
}

dependencies {
  compileOnly(project(":api"))
  compileOnly(libs.hadoop3.cos)
  compileOnly(libs.hadoop3.client.api)
  compileOnly(libs.hadoop3.client.runtime)

  implementation(project(":common")) {
    exclude("*")
  }
  implementation(project(":catalogs:catalog-common")) {
    exclude("*")
  }
  implementation(project(":catalogs:hadoop-common")) {
    exclude("*")
  }

  implementation(libs.cos.api.bundle)
  implementation(libs.commons.lang3)
  implementation(libs.guava)

  testImplementation(project(":api"))
  testImplementation(project(":core"))
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.junit.jupiter.params)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType(ShadowJar::class.java) {
  isZip64 = true
  configurations = listOf(project.configurations.runtimeClasspath.get())
  archiveClassifier.set("")
  mergeServiceFiles()

  dependencies {
    exclude(dependency("org.slf4j:slf4j-api"))
  }

  relocate("com.qcloud", "org.apache.gravitino.cos.shaded.com.qcloud")
  relocate("com.fasterxml.jackson", "org.apache.gravitino.cos.shaded.com.fasterxml.jackson")
  relocate("com.google.common", "org.apache.gravitino.cos.shaded.com.google.common")
  relocate("com.google.errorprone", "org.apache.gravitino.cos.shaded.com.google.errorprone")
  relocate("com.google.thirdparty", "org.apache.gravitino.cos.shaded.com.google.thirdparty")
  relocate("io.netty", "org.apache.gravitino.cos.shaded.io.netty")
  relocate("org.apache.commons", "org.apache.gravitino.cos.shaded.org.apache.commons")
  relocate("org.apache.hadoop.fs.cosn", "org.apache.gravitino.cos.shaded.org.apache.hadoop.fs.cosn")
  relocate("org.apache.http", "org.apache.gravitino.cos.shaded.org.apache.http")
  relocate("org.checkerframework", "org.apache.gravitino.cos.shaded.org.checkerframework")
  relocate("org.reactivestreams", "org.apache.gravitino.cos.shaded.org.reactivestreams")
  relocate("org.wildfly.openssl", "org.apache.gravitino.cos.shaded.org.wildfly.openssl")

  mergeServiceFiles()
}

tasks.jar {
  dependsOn(tasks.named("shadowJar"))
  archiveClassifier.set("empty")
}

tasks.compileJava {
  dependsOn(":catalogs:catalog-fileset:runtimeJars")
}
