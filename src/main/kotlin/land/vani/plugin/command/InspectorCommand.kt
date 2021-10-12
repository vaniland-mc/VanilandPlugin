package land.vani.plugin.command

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import de.myzelyam.api.vanish.VanishAPI
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.permission.INSPECTOR_MODE
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun VanilandPlugin.inspectorCommand() {
    command("inspector") {
        permission = INSPECTOR_MODE

        tab {
            argument {
                addAll("tp")
            }

            argument("tp") {
                addAll(Bukkit.getOnlinePlayers().map { it.name })
            }
        }

        execute {
            if (sender !is Player) {
                sender.sendMessage(
                    text {
                        content("このコマンドはゲーム内からのみ実行できます")
                        color(NamedTextColor.RED)
                    }
                )
                return@execute
            }
            val player = sender as Player

            if (args.size != 2) {
                sender.sendMessage(
                    text {
                        content("引数が不正です")
                        color(NamedTextColor.RED)
                    }
                )
            }
            when (args.lowerOrNull(0)) {
                "tp" -> {
                    val targetName = args.lowerOrNull(1)
                    if (targetName.isNullOrEmpty()) {
                        sender.sendMessage(
                            text {
                                content("対象のプレイヤーを指定してください")
                                color(NamedTextColor.RED)
                            }
                        )
                        return@execute
                    }
                    val target = Bukkit.getPlayer(targetName)
                    if (target == null) {
                        sender.sendMessage(
                            text {
                                content("プレイヤーが見つかりませんでした")
                                color(NamedTextColor.RED)
                            }
                        )
                        return@execute
                    }

                    player.gameMode = GameMode.SPECTATOR
                    VanishAPI.hidePlayer(player)
                    player.teleport(target.location)
                    sender.sendMessage(
                        text {
                            content("透明化を有効にして${target.name}にテレポートしました")
                            color(NamedTextColor.GREEN)
                        }
                    )
                }
                else -> {
                    sender.sendMessage(
                        text {
                            content("不正なサブコマンドです")
                            color(NamedTextColor.RED)
                        }
                    )
                }
            }
        }
    }
}
