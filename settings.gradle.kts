rootProject.name = "ktor-extensions"

plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
}

include("scheduler")
include("exposed-shedlock")
include("http")