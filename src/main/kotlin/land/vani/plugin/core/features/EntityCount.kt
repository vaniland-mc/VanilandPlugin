package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.arguments.integer
import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.command.arguments.world
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.extra.kotlin.translatable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EntityCount(
    private val plugin: VanilandPlugin,
) : Feature<EntityCount>() {
    companion object : Key<EntityCount>("entityCount")

    override val key: Key<EntityCount> = Companion

    override suspend fun onEnable() {
        registerCommands()
    }

    private suspend fun registerCommands() {
        plugin.registerCommand(entitiesCountCommand())
        plugin.registerCommand(entitiesRankingCommand())
        plugin.registerCommand(tpChunkCommand())
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun entitiesCountCommand() = command<CommandSender>("entitiesCount") {
        runs {
            if (source !is Player) {
                source.sendMessage("You must be a player to use this command")
                return@runs
            }
            val chunk = (source as Player).location.chunk
            val entities = chunk.entities
            val entitiesGroupedByType = entities.groupBy { it.type }

            sendMessage(
                source,
                Component.newline() + text {
                    content("現在のチャンク(${chunk.x}/${chunk.z})に存在するエンティティ")
                } + Component.newline() + text {
                    content("総数: ${entities.size}")
                } + entitiesGroupedByType.map { (entityType, entities) ->
                    Component.newline() + translatable {
                        key(entityType)
                    } + text { content(": ${entities.size}") }
                }.reduce { first, second -> first.append(second) }
            )
        }
    }

    private fun entitiesRankingCommand() = command<CommandSender>("entitiesRanking") {
        runs {
            if (source !is Player) {
                source.sendMessage("You must be a player to use this command")
                return@runs
            }

            val world = (source as Player).location.world
            val chunks = world.loadedChunks.sortedBy { it.entities.size }.take(n = 5)

            sendMessage(
                source,
                Component.newline() + text {
                    content("${world.name}内でエンティティが多いチャンク")
                } + Component.newline() + chunks.map { chunk ->
                    text {
                        content("${chunk.x}/${chunk.z} : ${chunk.entities.size}")
                        clickEvent(
                            ClickEvent.runCommand("/tpchunk ${world.name} ${chunk.x} ${chunk.z}")
                        )
                    }
                }.reduce { first, second -> first.append(second) }
            )
        }
    }

    private fun tpChunkCommand() = command<CommandSender>("tpChunk") {
        val world by world("world")
        val x by integer("x")
        val z by integer("z")

        required { it.hasPermission(Permissions.ADMIN) }

        @Suppress("MagicNumber")
        runs {
            if (source !is Player) {
                source.sendMessage("You must be a player to use this command")
                return@runs
            }
            val blockX = x * 16 + 8
            val blockZ = z * 16 + 8
            val blockY = world.getHighestBlockYAt(blockX, blockZ)

            (source as Player).teleport(Location(world, blockX.toDouble(), blockY.toDouble(), blockZ.toDouble()))
        }
    }
}
