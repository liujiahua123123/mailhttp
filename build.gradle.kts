import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenLocal()
}

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}


group = "net.mamoe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}

val serializationVersion = "1.0.0"
val ktorVersion = "2.0.1"

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
    implementation("com.github.davidmoten:subethasmtp:6.0.0")
    implementation("tech.blueglacier:email-mime-parser:1.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0-RC")

    implementation("io.ktor:ktor-html-builder:1.6.8")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation(kotlinx("serialization-core", serializationVersion))
    implementation(kotlinx("serialization-json", serializationVersion))
}

tasks.test {
    useJUnit()
}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}