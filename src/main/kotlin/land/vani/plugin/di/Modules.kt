package land.vani.plugin.di

import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.type.ConfigDataType
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.gateway.mcbans.MCBansGateway
import land.vani.plugin.gateway.mcbans.config.MCBansConfig
import land.vani.plugin.gateway.mcbans.impl.MCBansGatewayImpl
import org.koin.core.module.Module
import org.koin.dsl.module

private val configsModule = module {
    single {
        val plugin = get<VanilandPlugin>()
        val apiKey = plugin.config(null, "mcbans.yml").get("apiKey", ConfigDataType.String)!!

        MCBansConfig(apiKey)
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

fun makeModules(plugin: VanilandPlugin): List<Module> {
    val pluginModule = module {
        single { plugin }
    }

    return pluginModule +
            configsModule +
            httpClientModule +
            gatewayMCBansModules
}
