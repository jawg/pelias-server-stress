package io.jawg.gatling.pelias

import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.CoreDsl.rampUsers
import io.gatling.javaapi.core.PopulationBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import io.gatling.javaapi.http.HttpProtocolBuilder
import io.jawg.gatling.pelias.Parameters.PELIAS_URLS
import io.jawg.gatling.pelias.Parameters.RAMP_TIME
import io.jawg.gatling.pelias.Parameters.USERS

class PeliasReverseSimulation : Simulation() {
  private val httpProtocol: HttpProtocolBuilder = http.shareConnections()

  private fun scenarios(urls: List<String>): List<PopulationBuilder> =
    urls.map { url ->
      ScenarioBuilder.region("PeliasReverseSimulation")
        .exec(
          http { it.getString("Region") }
            .get("/v1/reverse")
            .queryParam("size", 1)
            .queryParam("point.lat") { it.getString("lat") }
            .queryParam("point.lon") { it.getString("lon") }
            .check(status().`is`(200))
            .check(jsonPath("$..errors").notExists())
        )
        .injectOpen(rampUsers(USERS).during(RAMP_TIME))
        .protocols(httpProtocol.baseUrl(url))
    }

  init {
    setUp(scenarios(PELIAS_URLS))
  }
}