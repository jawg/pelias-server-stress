package io.jawg.gatling.pelias

import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.CoreDsl.rampUsers
import io.gatling.javaapi.core.PopulationBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import io.gatling.javaapi.http.HttpProtocolBuilder
import io.jawg.gatling.pelias.Parameters.AUTOCOMPLETE
import io.jawg.gatling.pelias.Parameters.PELIAS_URLS
import io.jawg.gatling.pelias.Parameters.RAMP_TIME
import io.jawg.gatling.pelias.Parameters.USERS

class PeliasOASimulation : Simulation() {
  private val httpProtocol: HttpProtocolBuilder = http.shareConnections()

  private fun scenarios(urls: List<String>): List<PopulationBuilder> =
    urls.map { url ->
      val endpoint = if (AUTOCOMPLETE) "/v1/autocomplete" else "/v1/search"
      ScenarioBuilder.oa("PeliasOASimulation")
        .exec(
          http("OpenAddresses").get(endpoint)
            .queryParam("text") { it.getString("text") }
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