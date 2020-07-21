/*
 * Copyright 2015-2020 Jawg
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

import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class PeliasReverseSimulation extends Simulation {

  import Parameters._

  val httpProtocol: HttpProtocolBuilder = http
    .shareConnections


  def scenarios(urls: List[String]): List[PopulationBuilder] =
    urls.map { url =>
      scenario("PeliasReverseSimulation")
        .feed(csv(REGIONS_CSV_FILE).circular)
        .feed(csv(SEED_FILE).circular)
        .exec { session =>
          val seed: Long = session("seed").as[String].toLong
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
        .inject(rampUsers(USERS) during RAMP_TIME)
        .protocols(httpProtocol.baseUrl(url))
    }

  setUp(scenarios(PELIAS_URLS))
}
