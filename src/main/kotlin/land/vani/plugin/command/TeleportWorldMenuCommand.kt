package land.vani.plugin.command

import com.github.syari.spigot.api.command.CommandArgument
import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.event.events
import com.github.syari.spigot.api.inventory.inventory
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.config.WorldMenuConfig
import land.vani.plugin.permission.TELEPORT_WORLD_MENU
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.koin.core.component.get
import java.util.UUID

private val mobSelection = mutableMapOf<UUID, MutableSet<Entity>>()

/*
/worldMenu open
/worldMenu open rona_tombo
/worldMenu mob select
/worldMenu mob select rona_tombo
/worldMenu mob cancel
/worldMenu mob cancel rona_tombo
 */
fun VanilandPlugin.teleportWorldMenu() {
    command("worldMenu") {
        permission = TELEPORT_WORLD_MENU

        tab {
            argument {
                addAll("open", "mob", "setMobSpawn")
            }
            argument("open") {
                addAll(Bukkit.getOnlinePlayers().map { it.name })
            }
            argument("mob") {
                addAll(Bukkit.getOnlinePlayers().map { it.name })
            }
        }

        execute {
            val config = get<WorldMenuConfig>()

            if (args.isEmpty()) {
                sender.sendMessage(
                    text {
                        content("引数にopen/mob/setMobSpawnのどれかを指定してください")
                        color(NamedTextColor.RED)
                    }
                )
                return@execute
            }
            when (args.lowerOrNull(0)) {
                "open" -> executeOpenCommand(sender, args, config)
                "mob" -> executeMobCommand(sender, args)
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

    registerEventListener()
}

private fun executeOpenCommand(sender: CommandSender, args: CommandArgument, config: WorldMenuConfig) {
    val target = getTarget(sender, args, 1) ?: return

    inventory("移動するワールドを選択してください", 5, "land.vani.plugin.menu.world") {
        config.worlds.forEach { worldMenuDetails ->
            item(worldMenuDetails.slot, worldMenuDetails.itemStack) {
                onClick {
                    mobSelection.remove(target.uniqueId)?.forEach {
                        it.teleport(worldMenuDetails.teleportLocation)
                    }
                    target.teleport(worldMenuDetails.teleportLocation)
                }
            }
        }
    }.open(target)
}

private fun executeMobCommand(sender: CommandSender, args: CommandArgument) {
    val target = getTarget(sender, args, 2) ?: return

    when (args.lowerOrNull(1)) {
        "select" -> {
            val mobToMoveList = mobSelection[target.uniqueId]
            if (mobToMoveList != null) {
                sender.sendMessage(
                    text {
                        content("既にMobの選択を開始しています")
                        color(NamedTextColor.RED)
                    }
                )
                return
            }
            mobSelection[target.uniqueId] = mutableSetOf()

            target.sendMessage(
                text {
                    content("ワールドを移動させるMobを右クリックしてください")
                    color(NamedTextColor.GREEN)
                }
            )
        }
        "cancel" -> {
            if (mobSelection.remove(target.uniqueId) != null) {
                target.sendMessage(
                    text {
                        content("Mob選択モードをキャンセルしました")
                        color(NamedTextColor.GREEN)
                    }
                )
            }
        }
    }
}

private fun getTarget(sender: CommandSender, args: CommandArgument, targetNameIndex: Int): Player? =
    if (args.getOrNull(targetNameIndex) == null) {
        if (sender is Player) {
            sender
        } else {
            sender.sendMessage(
                text {
                    content("このコマンドをコンソールから使用するには対象プレイヤーを指定する必要があります")
                    color(NamedTextColor.RED)
                }
            )
            null
        }
    } else {
        val targetName = args[targetNameIndex]
        Bukkit.getPlayer(targetName) ?: run {
            sender.sendMessage(
                text {
                    content("対象プレイヤーが見つかりませんでした")
                    color(NamedTextColor.RED)
                }
            )
            null
        }
    }

private fun VanilandPlugin.registerEventListener() {
    events {
        event<PlayerInteractEntityEvent> { event ->
            mobSelection[event.player.uniqueId]?.let { selectedMobs ->
                selectedMobs += event.rightClicked

                event.player.sendMessage(
                    text {
                        content("${event.rightClicked.name}を追加しました\n")
                        color(NamedTextColor.GREEN)
                    } + text {
                        content("現在輸送対象の動物\n")
                    } + text {
                        selectedMobs.forEach { entity ->
                            text {
                                content("${entity.name}(${entity.type})\n")
                                color(NamedTextColor.WHITE)
                                hoverEvent(
                                    text {
                                        content("クリックでこの動物を輸送対象から削除")
                                    }
                                )
                                clickEvent(ClickEvent.runCommand("/worldMenu mob remove ${event.player.name} ${entity.uniqueId}"))
                            }.let {
                                append(it)
                            }
                        }
                    }
                )
            }
        }
    }
}
