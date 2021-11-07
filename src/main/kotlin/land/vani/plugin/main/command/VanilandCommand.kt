package land.vani.plugin.main.command

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.MCBansConfig
import land.vani.plugin.main.config.WorldMenuConfig
import land.vani.plugin.main.permission.VANILAND_COMMAND
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.koin.core.component.get

fun VanilandPlugin.vanilandCommand() {
    command("vaniland") {
        permission = VANILAND_COMMAND
        tab {
            argument {
                addAll("reload")
            }
        }
        execute {
            when (args.lowerOrNull(0)) {
                "reload" -> {
                    val mcBansConfig = get<MCBansConfig>()
                    val worldMenuConfig = get<WorldMenuConfig>()

                    mcBansConfig.reload()
                    worldMenuConfig.reload()

                    sender.sendMessage(
                        text {
                            content("設定ファイルをリロードしました")
                            color(NamedTextColor.GREEN)
                        }
                    )
                }
                else -> {
                    sender.sendMessage(
                        text {
                            content("不明なサブコマンドです")
                            color(NamedTextColor.RED)
                        }
                    )
                }
            }
        }
    }
}