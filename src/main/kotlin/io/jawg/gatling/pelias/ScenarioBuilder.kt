package io.jawg.gatling.pelias

import io.gatling.javaapi.core.CoreDsl.csv
import io.gatling.javaapi.core.CoreDsl.scenario
import io.jawg.gatling.pelias.Parameters.OA_CSV_FILE
import io.jawg.gatling.pelias.Parameters.REGIONS_CSV_FILE
import io.jawg.gatling.pelias.Parameters.SEED_FILE
import kotlin.random.Random

object ScenarioBuilder {
  fun region(name: String) = scenario(name)
    .feed(csv(REGIONS_CSV_FILE).circular())
    .feed(csv(SEED_FILE).circular())
    .exec { session ->
      val seed: Long = session.getString("seed")!!.toLong()
      val rand = Random(seed)
      val latMin = session.getString("LatMin")!!.toDouble()
      val latMax = session.getString("LatMax")!!.toDouble()
      val lngMin = session.getString("LngMin")!!.toDouble()
      val lngMax = session.getString("LngMax")!!.toDouble()

      val lng = rand.nextDouble() * (lngMax - lngMin) + lngMin
      val lat = rand.nextDouble() * (latMax - latMin) + latMin

      session
        .set("lat", lat)
        .set("lon", lng)
    }

  fun oa(name: String) = scenario("PeliasOASimulation")
    .feed(csv(OA_CSV_FILE).circular())
    .feed(csv(SEED_FILE).circular())
    .exec { session ->
      val number = session.getString("NUMBER")
      val street = session.getString("STREET")
      val city = session.getString("CITY")
      val district = session.getString("DISTRICT")
      val postcode = session.getString("POSTCODE")
      val region = session.getString("REGION")

      var text = ""

      if (!number.isNullOrBlank()) text += number
      if (!street.isNullOrBlank()) text += " $street"
      if (!city.isNullOrBlank()) text += ", $city"
      if (!district.isNullOrBlank() && city.isNullOrBlank()) text += ", $district"
      if (!region.isNullOrBlank()) text += ", $region"
      if (!postcode.isNullOrBlank()) text += " $postcode"

      session
        .set("text", text)
    }
}