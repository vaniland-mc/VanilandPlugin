package land.vani.plugin.command

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.config.MCBansConfig
import land.vani.plugin.permission.MCBANS_IGNORE_COMMAND
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit

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
                    val targetName = args.lowerOrNull(1) ?: run {
                        sender.sendMessage(
                            text {
                                content("対象が見つかりませんでした")
                                color(NamedTextColor.RED)
                            }
                        )
                        return@execute
                    }
                    val targetUuid = Bukkit.getPlayer(targetName)?.uniqueId
                    if (targetUuid == null) {
                        sender.sendMessage(
                            text {
                                content("対象がオンラインでないため見つかりませんでした")
                                color(NamedTextColor.RED)
                            }
                        )
                        return@execute
                    }
                    config.ignoredUuidList = config.ignoredUuidList + targetUuid

                    sender.sendMessage(
                        text {
                            content("${targetName}をMCBans通知の無視対象に追加しました")
                            color(NamedTextColor.GREEN)
                        }
                    )
                }
            }
        }
    }
}
