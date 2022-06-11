package land.vani.plugin.core.di

import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.MainConfig
import org.koin.dsl.module

fun modulesWithFeatures(plugin: VanilandPlugin, mainConfig: MainConfig) = buildList {
    add(coreModule(plugin, mainConfig))
}

fun coreModule(plugin: VanilandPlugin, mainConfig: MainConfig) = module {
    single { plugin }
    single { mainConfig }
}
