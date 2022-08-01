package land.vani.plugin.core.features

import kotlinx.coroutines.runBlocking
import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

object VanilandCommand : Feature<VanilandCommand>() {
    override val key: Key<VanilandCommand> = Key("vanilandCommand")

    override suspend fun onEnable(plugin: VanilandPlugin) {
        plugin.registerCommand(createVanilandCommand(plugin))
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun createVanilandCommand(plugin: VanilandPlugin) = command<CommandSender>("vaniland") {
        required { it.hasPermission(Permissions.ADMIN) }

        subCommands(
            createReloadCommand(plugin)
        )
    }

    private fun createReloadCommand(plugin: VanilandPlugin) = command("reload") {
        runs {
            runBlocking {
                plugin.mainConfig.reload()
                plugin.safetyLoginsConfig.reload()
                plugin.autoMessageConfig.reload()

                val autoMessageFeature = plugin.featuresRegistry.getFeature(AutoMessage.key)
                autoMessageFeature?.reload(plugin)

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
