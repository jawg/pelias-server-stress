package io.jawg.gatling.pelias

import java.io.File
import java.io.PrintWriter
import java.util.Random
import kotlin.math.abs

fun main() {
  val file = File(Parameters.SEED_FILE)
  file.createNewFile()
  val writer = PrintWriter(file)
  val rand = Random()
  writer.append("seed")
  writer.append("\n")
  writer.flush()

  (0 until Parameters.USERS).forEach {
    val randInt = abs(rand.nextInt())
    writer.append("$randInt\n")
    writer.flush()
  }
}