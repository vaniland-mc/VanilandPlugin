import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"

    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "land.vani.plugin"
version = "2.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        content {
            includeGroup("io.papermc.paper")
            includeGroup("com.mojang")
            includeGroup("net.md-5")
        }
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "OnARandomBox"
        url = uri("https://repo.onarandombox.com/content/groups/public/")
        content {
            includeGroup("com.onarandombox.multiversecore")
        }
    }
    maven("https://jitpack.io")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")

    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-extra-kotlin:4.14.0") {
        exclude("net.kyori")
    }

    implementation("land.vani.mcorouhlin:mcorouhlin-api:7.0.43")
    implementation("land.vani.mcorouhlin:mcorouhlin-paper:7.0.43")
//    implementation("land.vani.mcorouhlin:mcorouhlin-api:SNAPSHOT")
//    implementation("land.vani.mcorouhlin:mcorouhlin-paper:SNAPSHOT")

    compileOnly("com.github.LeonMangler:SuperVanish:6.2.16") {
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
    compileOnly("com.github.NuVotifier.NuVotifier:nuvotifier-api:2.7.2")
    compileOnly("com.github.NuVotifier.NuVotifier:nuvotifier-bukkit:2.7.2")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.2.2")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    implementation("io.insert-koin:koin-core:3.4.2")

    implementation("dev.kord:kord-core:0.15.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        @Suppress("UnstableApiUsage")
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        @Suppress("UnstableApiUsage")
        vendor.set(JvmVendorSpec.GRAAL_VM)
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
            sarif.required.set(true)
        }
    }

    assemble {
        dependsOn("reobfJar")
    }
}
