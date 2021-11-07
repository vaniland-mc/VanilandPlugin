package land.vani.plugin.main.di

import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.def.DefaultConfigResource
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.DiscordConfig
import land.vani.plugin.main.config.MCBansConfig
import land.vani.plugin.main.config.WorldMenuConfig
import land.vani.plugin.main.gateway.mcbans.MCBansGateway
import land.vani.plugin.main.gateway.mcbans.impl.MCBansGatewayImpl
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.koin.core.module.Module
import org.koin.dsl.module

private val configsModule = module {
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "mcBans.yml", DefaultConfigResource(plugin, "mcBans.yml"))

        MCBansConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "worldMenu.yml")

        WorldMenuConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "discord.yml")

        DiscordConfig(config)
    }
}

private val httpClientModule = module {
    single {
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
                acceptContentTypes = acceptContentTypes + ContentType.parse("text/html")
            }
        }
    }
}

private val gatewaysModules = module {
    single<MCBansGateway> { MCBansGatewayImpl(get(), get()) }
    single {
        val config = get<DiscordConfig>()
        runBlocking {
            Kord(config.token)
        }
    }
    single {
        val config = get<DiscordConfig>()
        val kord = get<Kord>()
        runBlocking {
            kord.getGuild(Snowflake(config.guildId))
        }
    }
}

private val dependPluginsModule = module {
    single {
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)!!.provider
    }
}

fun makeModules(plugin: VanilandPlugin): List<Module> {
    val pluginModule = module {
        single { plugin }
        single { plugin.slF4JLogger }
    }

    return pluginModule +
            configsModule +
            httpClientModule +
            gatewaysModules +
            dependPluginsModule
}
