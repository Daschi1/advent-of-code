plugins {
    kotlin("jvm") version "1.9.20"
}

dependencies {
    implementation("tools.aqua:z3-turnkey:4.12.2.1")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}