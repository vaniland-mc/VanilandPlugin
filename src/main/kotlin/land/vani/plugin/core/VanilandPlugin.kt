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
import org.koin.core.component.get
import org.koin.core.qualifier.named

class VanilandPlugin : McorouhlinKotlinPlugin(), VanilandCoreKoinComponent {
    val featuresRegistry by lazy { // Require to initialize koin at onEnableAsync so lazy init
        FeaturesRegistry(
            this,
            get(named("features"))
        )
    }

    private suspend fun saveDefaultConfigs() = withContext(Dispatchers.IO) {
        saveResource("autoMessages.yml", false)
        saveResource("config.yml", false)
        saveResource("portalWarpNpcs.yml", false)
        saveResource("safetyLogins.yml", false)
        saveResource("worldWarpNpcs.yml", false)
        saveResource("opInventory.yml", false)
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
        featuresRegistry.disableFeatures(get<MainConfig>().features)

        cancel()
        stopVanilandKoin()
    }
}
