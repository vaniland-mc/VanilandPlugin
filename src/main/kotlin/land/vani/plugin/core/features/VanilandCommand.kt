package land.vani.plugin.core.features

import kotlinx.coroutines.runBlocking
import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.AutoMessagesConfig
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.config.SafetyLoginsConfig
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

class VanilandCommand(
    private val plugin: VanilandPlugin,
    private val mainConfig: MainConfig,
    private val safetyLoginsConfig: SafetyLoginsConfig,
    private val autoMessagesConfig: AutoMessagesConfig,
) : Feature<VanilandCommand>() {
    companion object : Key<VanilandCommand>("vanilandCommand")

    override val key: Key<VanilandCommand> = Companion

    override suspend fun onEnable() {
        plugin.registerCommand(createVanilandCommand())
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun createVanilandCommand() = command<CommandSender>("vaniland") {
        required { it.hasPermission(Permissions.ADMIN) }

        subCommands(
            createReloadCommand()
        )
    }

    private fun createReloadCommand() = command("reload") {
        runs {
            runBlocking {
                mainConfig.reload()
                safetyLoginsConfig.reload()
                autoMessagesConfig.reload()

                val autoMessageFeature = plugin.featuresRegistry.getFeature(AutoMessage)
                autoMessageFeature?.reload()

                sendMessage(
                    source,
                    text {
                        content("設定ファイルの再読み込みが完了しました")
                        color(NamedTextColor.GREEN)
                    }
                )
            }
        }
    }
}
