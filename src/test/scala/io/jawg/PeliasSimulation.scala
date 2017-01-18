/*
 * Copyright 2015 eBusiness Information
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jawg

import java.util.Random

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


object Parameters {

  val propertiesFromSystem = ConfigFactory.systemProperties()
  val propertiesFromFile = ConfigFactory.load("parameters.properties")
  val properties = propertiesFromSystem.withFallback(propertiesFromFile)

  // Url of the server to stress test
  val PELIAS_URLS = properties.getString("server.url").trim().split(",").toList

  // File to load containing the region rectangles where users will choose their initial latitudes and longitudes.
  // sample.csv contains an example of the format used.
  val CSV_FILE = properties.getString("simulation.regions")

  // File to load containing the region rectangles where users will choose their initial latitudes and longitudes.
  // sample.csv contains an example of the format used.
  val SEED_FILE = properties.getString("simulation.seeds")

  // Amount of users. Users will be dispatched as equally as possible across regions.
  val USERS = properties.getString("simulation.users.count").toInt

  // Users amount can be ramped up over this duration in seconds
  val RAMP_TIME = properties.getString("simulation.users.ramp.time").toInt.seconds

  // Note :
  // The time units can be specified, for instance 1.minute, 1000.millis, etc
}

class PeliasSimulation extends Simulation {

  import Parameters._

  val httpProtocol = http.baseURLs(PELIAS_URLS)

  val scn = scenario("PeliasSimulation")
    .feed(csv(CSV_FILE).circular)
    .feed(csv(SEED_FILE).circular)
    .exec { session =>
      val seed = session("seed").as[String].toLong
      val rand = new Random(seed)

      val latMin = session("LatMin").as[String].toDouble
      val latMax = session("LatMax").as[String].toDouble
      val lngMin = session("LngMin").as[String].toDouble
      val lngMax = session("LngMax").as[String].toDouble

      val lng = rand.nextDouble() * (lngMax - lngMin) + lngMin
      val lat = rand.nextDouble() * (latMax - latMin) + latMin

      session
        .set("lat", lat)
        .set("lon", lng)
    }
    .exec(
      http(s"$${Region}").get("/v1/reverse")
        .queryParam("size", 1)
        .queryParam("point.lat", "${lat}")
        .queryParam("point.lon", "${lon}")
        .check(status.is(200))
    )

  setUp(scn.inject(rampUsers(USERS) over RAMP_TIME))
    .protocols(httpProtocol)
}
