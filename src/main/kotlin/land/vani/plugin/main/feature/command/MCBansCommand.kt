package land.vani.plugin.main.feature.command

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.MCBansConfig
import land.vani.plugin.main.feature.command.util.getTarget
import land.vani.plugin.main.permission.MCBANS_IGNORE_COMMAND
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor

fun VanilandPlugin.mcBansCommand(config: MCBansConfig) {
    command("mcbans") {
        permission = MCBANS_IGNORE_COMMAND
        tab {
            argument {
                add("reload")
            }
        }
        execute {
            if (args.isEmpty()) {
                sender.sendMessage(
                    text {
                        content("引数に以下のいずれかを指定してください: ignore")
                        color(NamedTextColor.RED)
                    }
                )
                return@execute
            }

            when (args.lowerOrNull(0)) {
                "ignore" -> {
                    val target = getTarget(sender, args, 1) ?: return@execute
                    config.ignoredUuidList = config.ignoredUuidList + target.uniqueId

                    sender.sendMessage(
                        text {
                            content("${target.name}をMCBans通知の無視対象に追加しました")
                            color(NamedTextColor.GREEN)
                        }
                    )
                }
            }
        }
    }
}
