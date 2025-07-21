package io.jawg.gatling.pelias

import io.gatling.javaapi.core.CoreDsl.rampUsers
import io.gatling.javaapi.core.PopulationBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import io.gatling.javaapi.http.HttpProtocolBuilder
import io.jawg.gatling.pelias.Parameters.PELIAS_URLS
import io.jawg.gatling.pelias.Parameters.RAMP_TIME
import io.jawg.gatling.pelias.Parameters.USERS

class SpatialServiceSimulation : Simulation() {
  private val httpProtocol: HttpProtocolBuilder = http.shareConnections()

  private fun scenarios(urls: List<String>): List<PopulationBuilder> =
    urls.map { url ->
      ScenarioBuilder.region("PIPServiceSimulation")
        .exec(
          http { it.getString("Region") }
            .get("/query/pip")
            .queryParam("lat") { it.getString("lat") }
            .queryParam("lon") { it.getString("lon") }
            .queryParam("role", "boundary")
            .check(status().`is`(200))
        )
        .injectOpen(rampUsers(USERS).during(RAMP_TIME))
        .protocols(httpProtocol.baseUrl(url))
    }

  init {
    setUp(scenarios(PELIAS_URLS))
  }
}