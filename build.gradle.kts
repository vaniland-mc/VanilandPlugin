import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"

    id("io.gitlab.arturbosch.detekt") version "1.18.1"

    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "land.vani.plugin"
version = "1.0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        setUrl("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        setUrl("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "BanManager repo"
        setUrl("https://ci.frostcast.net/plugin/repository/everything")
    }
    maven("https://jitpack.io")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.18.1")

    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    api("com.github.sya-ri:EasySpigotAPI:2.4.0") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    implementation("net.kyori:adventure-extra-kotlin:4.9.2") {
        exclude("net.kyori")
    }

    compileOnly("com.github.LeonMangler:SuperVanish:6.2.6-2") {
        exclude("com.comphenix.protocol", "ProtocolLib-API")
        exclude("net.citizensnpcs", "citizensapi")
        exclude("com.sk89q.worldguard", "worldguard-bukkit")
        exclude("me.clip", "placeholderapi")
        exclude("org.bstats", "bstats-bukkit")
    }
    compileOnly("com.github.ucchyocean:LunaChat:2.8.14") {
        exclude("org.bstats")
    }
    compileOnly("net.luckperms:api:5.3")
    compileOnly("me.confuser.banmanager:BanManagerBukkit:7.6.0-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")

    implementation("io.insert-koin:koin-core:3.1.2")

    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-cio:1.6.4")
    implementation("io.ktor:ktor-client-serialization:1.6.4")
    implementation("io.ktor:ktor-client-logging:1.6.4")

    implementation("dev.kord:kord-core:0.8.0-M7")

    testImplementation("io.kotest:kotest-runner-junit5:5.0.0.659-SNAPSHOT")
    testImplementation("io.kotest:kotest-assertions-core:5.0.0.659-SNAPSHOT")
}

val targetJavaVersion = 16
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

detekt {
    reports {
        xml.enabled = true
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            this.jvmTarget = "$targetJavaVersion"
        }
    }

    processResources {
        expand("version" to version)
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Detekt> {
        jvmTarget = "$targetJavaVersion"
    }
}
