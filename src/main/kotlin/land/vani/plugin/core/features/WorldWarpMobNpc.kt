package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.dsl.command
import land.vani.plugin.core.VanilandPlugin
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.net.URL

object WorldWarpMobNpc : Feature<WorldWarpMobNpc>() {
    override val key: Key<WorldWarpMobNpc> = Key("worldWarpMobNpc")

    internal val mobSelections = mutableMapOf<Player, MutableSet<Entity>>()

    override suspend fun onEnable(plugin: VanilandPlugin) {
        registerCommands(plugin)
    }

    private fun registerCommands(plugin: VanilandPlugin) {
        val command = command<CommandSender>("spawnWorldWarpMobNpc") {
            runs {
                if (source !is Player) {
                    WorldWarpNpc.sendMessage(
                        source,
                        text {
                            content("You must be a player to use this command.")
                            color(NamedTextColor.RED)
                        }
                    )
                    return@runs
                }

                val npc = CitizensAPI.getNPCRegistry().createNPC(
                    EntityType.PLAYER,
                    "§b動物移送受付"
                ).apply {
                    addTrait(WorldWarpMobTrait())
                }
                npc.spawn((source as Player).location)
                // https://ja.namemc.com/skin/fdd411d4bcb02677
                (npc.entity as Player).playerProfile.textures.skin = URL(
                    "https://s.namemc.com/i/fdd411d4bcb02677.png"
                )
            }
        }
        plugin.registerCommand(command)
    }

    class WorldWarpMobTrait : Trait("worldWarpMobTrait") {
        @EventHandler
        fun onClick(event: NPCRightClickEvent) {
            if (event.npc != npc) return

            val selections = mobSelections[event.clicker]
            if (selections == null) {
                mobSelections[event.clicker] = mutableSetOf()
                sendMessage(
                    event.clicker,
                    text {
                        content("Mobの選択を開始しました")
                        color(NamedTextColor.YELLOW)
                    }
                )
                return
            } else {
                mobSelections.remove(event.clicker)
                sendMessage(
                    event.clicker,
                    text {
                        content("Mobの選択をキャンセルしました")
                        color(NamedTextColor.YELLOW)
                    }
                )
            }
        }

        @EventHandler
        fun onLogout(event: PlayerQuitEvent) {
            mobSelections.remove(event.player)
        }

        @EventHandler
        fun onMobClick(event: PlayerInteractEntityEvent) {
            val selections = mobSelections[event.player] ?: return
            if (event.rightClicked.type !in ALLOWED_ENTITY_TYPES) return
            selections.add(event.rightClicked)
            sendMessage(
                event.player,
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
                        text {
                            resetStyle()
                            content("(${entity.type.name.lowercase()})")
                        }
                }.reduce { first, second -> first + Component.newline() + second }
            )
        }

        companion object {
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
    }
}
