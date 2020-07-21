# pelias-server-stress

This gatling script allow you to qualify and stress an **Pelias Server**

The Testing scenario is the following:

-   Each user will randomly select a region in the regions.csv file (round-robin strategy)
-   In this region, the user will randomly choose one positions (lat/lng)
-   Then, the user will request and get the address at this positions

## Requirements

-   Java : <https://adoptopenjdk.net/>
-   Scala : <http://www.scala-lang.org/download/>

Scala plugin for IntelliJ platform also helps.

## How to use

This script has been tested and approved by both Gatling and Gatling Frontline solutions.

-   Clone the project
    `git clone https://github.com/jawg/pelias-server-stress.git`sh
-   Set your environment properties in {projectRoot}/src/main/resources
    Properties are server.url, simulation.users.count...
-   Browse the project root and execute the following commands
    `./gradlew shadowJar` to build the project
-   Now you can generate seeds for all simulated users with this command `GENERATE_SEEDS=true ./bin/pelias-server-stress`. Skip this step if you want to use a custom seeds file or keep the previous one and produce the exact same test
-   In order to run the stress test use `./bin/pelias-server-stress`.

You can override properties with environment variables when you use the script `pelias-server-stress`.

Available arguments are:

-    `oa` or `openaddresses`: Run the openaddresses scenario. This needs openaddresses data (`OA_FILE`).
-    `pip` or `pip-service`: Run the PIP service scenario. This needs `REGION_FILE`.
-    `reverse` (default): Run the reverse geocoding scenario. This needs `REGION_FILE`.

Available environments are:

-    `GATLING_PROPERTIES`: Path to your custom property file
-    `SERVER_URL`: Set your custon server URL
-    `REGIONS_FILE`: Where your region file is
-    `OA_FILE`: Where your openaddresses file is
-    `SEEDS_FILE`: Where your seeds file
-    `AUTOCOMPLETE`: Set it to true if you wants autocomplete endpoint instead of search (only for openaddresses scenario)
-    `USERS_COUNT`: Set users count for your simulation (and number of seeds)
-    `USERS_RAMP_TIME`: Set the ramp time for your users
-    `GENERATE_SEEDS`: When not empty, will generate only seeds
-    `AUTO_GENERATE_SEEDS`: When not empty, will generate seed and run the simulation

A docker image is also available `docker build -t jawg/pelias-server-stress .` or `docker pull jawg/pelias-server-streess`

Simple openaddresses run: `docker run -ti --rm -e OA_FILE=/path/to/oa.csv jawg/pelias-server-streess oa`

## License

Copyright 2017-2020 Jawg

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   <http://www.apache.org/licenses/LICENSE-2.0>
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
