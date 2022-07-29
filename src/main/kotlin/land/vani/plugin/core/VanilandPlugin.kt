package land.vani.plugin.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import land.vani.mcorouhlin.paper.McorouhlinKotlinPlugin
import land.vani.mcorouhlin.permission.registerPermissions
import land.vani.plugin.core.config.AutoMessagesConfig
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.config.SafetyLoginsConfig
import land.vani.plugin.core.di.VanilandCoreKoinComponent
import land.vani.plugin.core.di.modulesWithFeatures
import land.vani.plugin.core.di.startVanilandKoin
import land.vani.plugin.core.di.stopVanilandKoin
import land.vani.plugin.core.features.AutoMessage
import land.vani.plugin.core.features.FeaturesRegistry
import land.vani.plugin.core.features.Newbie
import land.vani.plugin.core.features.SafetyLogin
import land.vani.plugin.core.features.VanilandCommand
import land.vani.plugin.core.features.Vote
import land.vani.plugin.core.features.commands.Commands
import org.koin.core.component.inject

class VanilandPlugin : McorouhlinKotlinPlugin(), VanilandCoreKoinComponent {
    val featuresRegistry = FeaturesRegistry(
        this,
        listOf(
            AutoMessage,
            Newbie,
            Vote,
            SafetyLogin,
            VanilandCommand,
            Commands,
        )
    )

    val mainConfig by inject<MainConfig>()
    val safetyLoginsConfig by inject<SafetyLoginsConfig>()
    val autoMessageConfig by inject<AutoMessagesConfig>()

    private suspend fun saveDefaultConfigs() = withContext(Dispatchers.IO) {
        saveResource("config.yml", false)
        saveResource("safetyLogins.yml", false)
        saveResource("autoMessages.yml", false)
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
        featuresRegistry.disableFeatures(mainConfig.features)

        cancel()
        stopVanilandKoin()
    }
}
