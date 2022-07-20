package land.vani.plugin.core.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.config.SafetyLoginsConfig
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
}
