import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"

    id("io.gitlab.arturbosch.detekt") version "1.20.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "land.vani.plugin"
version = "1.1.0"

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
    maven {
        name = "OnARandomBox"
        setUrl("https://repo.onarandombox.com/content/groups/public/")
    }
    maven("https://jitpack.io")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")

    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    api("com.github.sya-ri:EasySpigotAPI:2.4.0") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    implementation("net.kyori:adventure-extra-kotlin:4.11.0") {
        exclude("net.kyori")
    }

    compileOnly("com.github.LeonMangler:SuperVanish:6.2.7") {
        exclude("com.comphenix.protocol", "ProtocolLib-API")
        exclude("net.citizensnpcs", "citizensapi")
        exclude("com.sk89q.worldguard", "worldguard-bukkit")
        exclude("me.clip", "placeholderapi")
        exclude("org.bstats", "bstats-bukkit")
    }
    compileOnly("com.github.ucchyocean:LunaChat:3.0.16") {
        exclude("org.bstats")
    }
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.confuser.banmanager:BanManagerBukkit:7.8.0")
    compileOnly("com.github.NuVotifier.NuVotifier:nuvotifier-api:2.7.2")
    compileOnly("com.github.NuVotifier.NuVotifier:nuvotifier-bukkit:2.7.2")

    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.2.2")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("io.insert-koin:koin-core:3.2.0")

    implementation("dev.kord:kord-core:0.8.0-M15")

    testImplementation("io.kotest:kotest-runner-junit5:5.3.2")
    testImplementation("io.kotest:kotest-assertions-core:5.3.2")
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
        reports {
            xml.required.set(true)
        }
    }
}
