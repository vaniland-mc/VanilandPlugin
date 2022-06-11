package land.vani.plugin.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import land.vani.mcorouhlin.paper.McorouhlinKotlinPlugin
import land.vani.mcorouhlin.permission.registerPermissions
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.di.VanilandCoreKoinComponent
import land.vani.plugin.core.di.modulesWithFeatures
import land.vani.plugin.core.di.startVanilandKoin
import land.vani.plugin.core.di.stopVanilandKoin
import land.vani.plugin.core.features.FeaturesRegistry
import land.vani.plugin.core.features.automessage.AutoMessage
import org.koin.core.component.inject

class VanilandPlugin : McorouhlinKotlinPlugin(), VanilandCoreKoinComponent {
    private val featuresRegistry = FeaturesRegistry(
        this,
        listOf(
            AutoMessage,
        )
    )

    private suspend fun saveDefaultConfigs() = withContext(Dispatchers.IO) {
        saveResource("config.yml", false)
    }

    override suspend fun onEnableAsync() {
        registerPermissions<Permissions>()

        saveDefaultConfigs()

        val mainConfig = MainConfig(this).apply {
            reload()
        }

        startVanilandKoin {
            modules(modulesWithFeatures(this@VanilandPlugin, mainConfig))
        }

        featuresRegistry.enableFeatures(mainConfig.features)
    }

    override suspend fun onDisableAsync() {
        val mainConfig by inject<MainConfig>()
        featuresRegistry.disableFeatures(mainConfig.features)

        cancel()
        stopVanilandKoin()
    }
}
