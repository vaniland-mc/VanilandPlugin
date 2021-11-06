package land.vani.plugin.command

import com.github.syari.spigot.api.command.CommandArgument
import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.event.events
import com.github.syari.spigot.api.inventory.inventory
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.command.util.getSenderOrTarget
import land.vani.plugin.command.util.unknownCommand
import land.vani.plugin.config.WorldMenuConfig
import land.vani.plugin.permission.TELEPORT_WORLD_MENU
import land.vani.plugin.util.displayName
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.koin.core.component.get
import java.util.UUID

private val mobSelection = mutableMapOf<UUID, MutableSet<Entity>>()

fun VanilandPlugin.worldMenuCommand() {
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
                addAll("select", "cancel")
            }
            argument("mob *") {
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
                else -> unknownCommand(sender)
            }
        }
    }

    registerEventListener()
}

private fun executeOpenCommand(sender: CommandSender, args: CommandArgument, config: WorldMenuConfig) {
    val target = getSenderOrTarget(sender, args, 1) ?: return

    inventory("移動するワールドを選択してください", line = 5, "land.vani.plugin.menu.world") {
        config.worlds.forEach { worldMenuDetails ->
            item(worldMenuDetails.slot, worldMenuDetails.itemStack) {
                onClick {
                    target.teleport(worldMenuDetails.teleportLocation)
                    mobSelection.remove(target.uniqueId)?.forEach {
                        it.teleport(worldMenuDetails.teleportLocation)
                    }
                }
            }
        }
    }.open(target)
}

private fun executeMobCommand(sender: CommandSender, args: CommandArgument) {
    val target = getSenderOrTarget(sender, args, 2) ?: return

    when (args.lowerOrNull(1)) {
        "select" -> executeMobSelect(target)
        "remove" -> executeMobSelectRemove(target, args)
        "cancel" -> executeMobSelectCancel(target)
        else -> unknownCommand(sender)
    }
}

private fun executeMobSelect(target: Player) {
    val mobToMoveList = mobSelection[target.uniqueId]
    if (mobToMoveList != null) {
        target.sendMessage(
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

private fun executeMobSelectRemove(target: Player, args: CommandArgument) {
    val selectedMobs = mobSelection[target.uniqueId] ?: run {
        target.sendMessage("Mobの選択を開始していません")
        return
    }
    val entityUuid = args.lowerOrNull(index = 3)?.let {
        runCatching { UUID.fromString(it) }.getOrNull()
    } ?: run {
        target.sendMessage("エンティティが指定されていないか、正しいUUIDではありません")
    }
    val removed = selectedMobs.removeIf { it.uniqueId == entityUuid }
    if (removed) {
        target.sendMessage("削除しました")
    } else {
        target.sendMessage("そのMobは見つかりませんでした")
    }

    target.sendMessage(
        text {
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
                    clickEvent(ClickEvent.runCommand("/worldMenu mob remove ${target.name} ${entity.uniqueId}"))
                }.let {
                    append(it)
                }
            }
        }
    )
}

private fun executeMobSelectCancel(target: Player) {
    if (mobSelection.remove(target.uniqueId) != null) {
        target.sendMessage(
            text {
                content("Mob選択モードをキャンセルしました")
                color(NamedTextColor.GREEN)
            }
        )
    }
}

private val TELEPORTABLE_ENTITY_TYPES = setOf(
    EntityType.BEE,
    EntityType.CAT,
    EntityType.CHICKEN,
    EntityType.COW,
    EntityType.DONKEY,
    EntityType.FOX,
    EntityType.HORSE,
    EntityType.LLAMA,
    EntityType.MULE,
    EntityType.MUSHROOM_COW,
    EntityType.OCELOT,
    EntityType.PANDA,
    EntityType.PARROT,
    EntityType.PIG,
    EntityType.RABBIT,
    EntityType.SHEEP,
    EntityType.STRIDER,
    EntityType.TURTLE,
    EntityType.WOLF
)

private fun VanilandPlugin.registerEventListener() {
    events {
        event<PlayerInteractEntityEvent> { event ->
            val selectedMobs = mobSelection[event.player.uniqueId] ?: return@event
            if (event.rightClicked in selectedMobs) return@event
            if (event.rightClicked.type !in TELEPORTABLE_ENTITY_TYPES) return@event

            selectedMobs += event.rightClicked

            event.player.sendMessage(
                text {
                    content("${event.rightClicked.displayName}を追加しました\n")
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
                            clickEvent(
                                ClickEvent.runCommand(
                                    "/worldMenu mob remove ${event.player.name} ${entity.uniqueId}"
                                )
                            )
                        }.let {
                            append(it)
                        }
                    }
                }
            )
        }
    }
}
