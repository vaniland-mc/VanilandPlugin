package land.vani.plugin.core.features

import kotlinx.coroutines.runBlocking
import land.vani.mcorouhlin.command.dsl.command
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor

object VanilandCommand : Feature<VanilandCommand>() {
    override val key: Key<VanilandCommand> = Key("vanilandCommand")

    override suspend fun onEnable(plugin: VanilandPlugin) {
        plugin.registerCommand(createVanilandCommand(plugin))
    }

    private fun createVanilandCommand(plugin: VanilandPlugin) = command("vaniland") {
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
