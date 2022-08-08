package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.event.on
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.extra.kotlin.translatable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent

internal val MOB_SELECTIONS = mutableMapOf<Player, MutableSet<Entity>>()

class WorldWarpMobNpc(
    private val plugin: VanilandPlugin,
) : Feature<WorldWarpMobNpc>() {
    companion object : Key<WorldWarpMobNpc>("worldWarpMobNpc") {
        private val ALLOWED_ENTITY_TYPES = setOf(
            EntityType.BEE,
            EntityType.CAT,
            EntityType.CHICKEN,
            EntityType.COW,
            EntityType.DONKEY,
            EntityType.FOX,
            EntityType.GHAST,
            EntityType.GOAT,
            EntityType.HORSE,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MULE,
            EntityType.MUSHROOM_COW,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PIG,
            EntityType.POLAR_BEAR,
            EntityType.RABBIT,
            EntityType.SHEEP,
            EntityType.SKELETON_HORSE,
            EntityType.STRIDER,
            EntityType.TADPOLE,
            EntityType.TURTLE,
            EntityType.VILLAGER,
            EntityType.WOLF
        )
    }

    override val key: Key<WorldWarpMobNpc> = Companion

    override suspend fun onEnable() {
        registerListeners()
        registerCommands()
    }

    @Suppress("RemoveExplicitTypeArguments")
    private suspend fun registerCommands() {
        val command = command<CommandSender>("worldWarpMobNpc") {
            required { it.hasPermission(Permissions.ADMIN) }

            literal("start") {
                runs {
                    if (source !is Player) {
                        sendMessage(
                            source,
                            text {
                                content("You must be a player to use this command.")
                                color(NamedTextColor.RED)
                            }
                        )
                        return@runs
                    }
                    val player = source as Player

                    MOB_SELECTIONS[player] = mutableSetOf()
                    player.sendMessage(
                        player,
                        text {
                            content("Mobの選択を開始しました")
                            color(NamedTextColor.YELLOW)
                        }
                    )
                    return@runs
                }
            }
            literal("cancel") {
                runs {
                    if (source !is Player) {
                        sendMessage(
                            source,
                            text {
                                content("You must be a player to use this command.")
                                color(NamedTextColor.RED)
                            }
                        )
                        return@runs
                    }
                    val player = source as Player

                    if (MOB_SELECTIONS.remove(player) != null) {
                        player.sendMessage(
                            player,
                            text {
                                content("Mobの選択をキャンセルしました")
                                color(NamedTextColor.YELLOW)
                            }
                        )
                    }
                    return@runs
                }
            }
        }
        plugin.registerCommand(command)
    }

    private fun registerListeners() = plugin.events {
        on<PlayerQuitEvent> { event ->
            MOB_SELECTIONS.remove(event.player)
        }

        on<PlayerInteractEntityEvent> { event ->
            val selections = MOB_SELECTIONS[event.player] ?: return@on
            if (event.rightClicked.type !in ALLOWED_ENTITY_TYPES) return@on
            selections.add(event.rightClicked)
            event.player.sendMessage(
                (event.rightClicked.customName() ?: event.rightClicked.name()) + text {
                    resetStyle()
                    content("を選択しました")
                    color(NamedTextColor.YELLOW)
                } + Component.newline() + text {
                    resetStyle()
                    content("現在選択中のMob:")
                } + selections.map { entity ->
                    text {
                        resetStyle()
                        content("= ")
                    } + (entity.customName() ?: entity.name()) +
                        Component.space() +
                        translatable {
                            resetStyle()
                            key(entity.type)
                        }
                }.reduce { first, second -> first + Component.newline() + second }
            )
        }
    }
}
