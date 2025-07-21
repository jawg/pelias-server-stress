rootProject.name = "pelias-server-stress"

pluginManagement {
  val kotlinVersion: String by settings
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
  }
}
