val ktorVersion: String by project
//val kotlinVersion: String by project

fun DependencyHandler.ktor(module: String, version: String? = ktorVersion): Any =
    "io.ktor:ktor-$module:$version"

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("application")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(ktor("server-core")) // "io.ktor:ktor-server-core:$ktorVersion"
    implementation(ktor("server-netty")) // "io.ktor:ktor-ktor-server-netty:$ktorVersion"

    // logging if you want
    implementation("ch.qos.logback:logback-classic:1.2.5")

    // transport models
    implementation(project(":ok-marketplace-be-common"))
    implementation(project(":ok-marketplace-be-transport-openapi"))
    implementation(project(":ok-marketplace-be-transport-mapping-openapi"))
    // stubs
    implementation(project(":ok-marketplace-be-stubs"))

    testImplementation(kotlin("test-junit"))
    testImplementation(ktor("server-test-host")) // "io.ktor:ktor-server-test-host:$ktorVersion"
}