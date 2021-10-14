package land.vani.plugin.di

import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.def.DefaultConfigResource
import com.sk89q.worldguard.WorldGuard
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.config.MCBansConfig
import land.vani.plugin.config.WorldMenuConfig
import land.vani.plugin.gateway.mcbans.MCBansGateway
import land.vani.plugin.gateway.mcbans.impl.MCBansGatewayImpl
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

private val gatewayMCBansModules = module {
    single<MCBansGateway> { MCBansGatewayImpl(get(), get()) }
}

private val dependPluginsModule = module {
    single { WorldGuard.getInstance().platform.regionContainer }
    single {
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)!!.provider
    }
}

fun makeModules(plugin: VanilandPlugin): List<Module> {
    val pluginModule = module {
        single { plugin }
    }

    return pluginModule +
            configsModule +
            httpClientModule +
            gatewayMCBansModules +
            dependPluginsModule
}
