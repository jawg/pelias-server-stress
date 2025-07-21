plugins {
  kotlin("jvm") version "2.2.0"
  kotlin("plugin.allopen") version "2.2.0"
  id("io.gatling.gradle") version "3.14.3.3"
  id("com.gradleup.shadow") version "8.3.0"
}

group = "io.jawg"
version = "1.1.0"

gatling {
  enterprise.closureOf<Any> {
  }
}

tasks.jar {
  version = ""
  manifest {
    attributes["Main-Class"] = "io.gatling.app.Gatling"
  }
}


repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("com.typesafe:config:1.4.3")
  implementation("io.gatling.highcharts:gatling-charts-highcharts:3.14.3")
}