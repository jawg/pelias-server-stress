/*
 * Copyright 2015-2019 Jawg
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

class PeliasOASimulation extends Simulation {

  import Parameters._

  val httpProtocol: HttpProtocolBuilder = http
    .shareConnections


  def scenarios(urls: List[String]): List[PopulationBuilder] =
    urls.map { url =>
      val endpoint = if (AUTOCOMPLETE) "/v1/autocomplete" else "/v1/search"
      scenario("PeliasSimulation")
        .feed(csv(OA_CSV_FILE).circular)
        .feed(csv(SEED_FILE).circular)
        .exec { session =>
          val seed = session("seed").as[String].toLong
          val rand = new Random(seed)

          val number = session("NUMBER").as[String]
          val street = session("STREET").as[String]
          val city = session("CITY").as[String]
          val district = session("DISTRICT").as[String]
          val postcode = session("POSTCODE").as[String]
          val region = session("REGION").as[String]

          var text = ""

          if (!number.isEmpty) text += number
          if (!street.isEmpty) text += s" $street"
          if (!city.isEmpty) text += s", $city"
          if (!district.isEmpty && city.isEmpty) text += s", $district"
          if (!region.isEmpty) text += s", $region"
          if (!postcode.isEmpty) text += s" $postcode"

          session
            .set("text", text)
        }
        .exec(
          http(s"OpenAddresses").get(endpoint)
            .queryParam("text", "${text}")
            .check(status.is(200))
            .check(jsonPath("$..errors").notExists)
        )
        .inject(rampUsers(USERS) during RAMP_TIME)
        .protocols(httpProtocol.baseUrl(url))
    }

  setUp(scenarios(PELIAS_URLS))
}
