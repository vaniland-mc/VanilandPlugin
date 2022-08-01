package land.vani.plugin.core.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.AutoMessagesConfig
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.config.PortalWarpNpcsConfig
import land.vani.plugin.core.config.SafetyLoginsConfig
import land.vani.plugin.core.config.WorldWarpNpcsConfig
import org.koin.dsl.module

fun modulesWithFeatures(plugin: VanilandPlugin, mainConfig: MainConfig) = buildList {
    add(coreModule(plugin, mainConfig))
    add(configModule(plugin))
}

fun coreModule(plugin: VanilandPlugin, mainConfig: MainConfig) = module {
    single { plugin }
    single { mainConfig }
}

fun configModule(plugin: VanilandPlugin) = module {
    single {
        SafetyLoginsConfig(plugin).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        AutoMessagesConfig(plugin).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        PortalWarpNpcsConfig(plugin).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        WorldWarpNpcsConfig(plugin).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
}
