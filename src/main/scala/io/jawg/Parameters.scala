package io.jawg

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._

object Parameters {

  private val propertiesFromSystem = ConfigFactory.systemProperties()
  private val propertiesFromFile = fileProperties()
  private val properties = propertiesFromSystem.withFallback(propertiesFromFile)

  // Url of the server to stress test
  val PELIAS_URLS: List[String] = properties.getString("server.url").trim().split(",").toList

  // File to load containing the region rectangles where users will choose their initial latitudes and longitudes.
  // regions.csv contains an example of the format used.
  val REGIONS_CSV_FILE: String = properties.getString("simulation.reverse.regions")

  // File to load containing OpenAddresses like data.
  // openaddresses.csv contains an example of the format used.
  val OA_CSV_FILE: String = properties.getString("simulation.openaddresses.file")

  // True if we want autocomplete endpoint, false for search endpoint
  val AUTOCOMPLETE: Boolean = properties.getBoolean("simulation.autocomplete")

  // File to load containing the region rectangles where users will choose their initial latitudes and longitudes.
  // regions.csv contains an example of the format used.
  val SEED_FILE: String = properties.getString("simulation.seeds")

  // Amount of users. Users will be dispatched as equally as possible across regions.
  val USERS: Int = properties.getString("simulation.users.count").toInt

  // Users amount can be ramped up over this duration in seconds
  val RAMP_TIME: FiniteDuration = properties.getString("simulation.users.ramp.time").toInt.seconds

  private def fileProperties(): Config = {
    val defaultProperties = ConfigFactory.load("parameters.properties")
    if (!propertiesFromSystem.hasPath("properties")) {
      defaultProperties
    } else {
      ConfigFactory.parseFileAnySyntax(new File(propertiesFromSystem.getString("properties")))
        .withFallback(defaultProperties)
    }
  }
}