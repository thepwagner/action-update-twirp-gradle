import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.30"
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation("org.slf4j:slf4j-api:1.7.30")
  val protobufVersion = "3.14.0"
  implementation("com.google.protobuf:protobuf-java:${protobufVersion}")
  implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")
  implementation("io.undertow:undertow-core:2.0.9.Final")
  runtimeOnly("ch.qos.logback:logback-classic:1.2.3")

  // flit runtime
  implementation(fileTree(mapOf("dir" to "libs", "include" to "*.jar")))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

task<Exec>("flit") {
  workingDir = file("${projectDir}/src/main/proto")
  commandLine = listOf("protoc",
    "--java_out=../java",
    "--flit_out=target=server,type=undertow:../java",
    "actionupdate/v1/update.proto"
  )
}

task<JavaExec>("runApp") {
  main = "com.github.thepwagner.actionupdate.gradle.MainKt"
  classpath = sourceSets["main"].runtimeClasspath
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes["Main-Class"] = "com.github.thepwagner.actionupdate.gradle.MainKt"
  }
}

task<Exec>("buildDocker") {
  dependsOn("shadowJar")
  commandLine = listOf("docker", "build", "-t", "action-update-twirp-gradle", ".")
}