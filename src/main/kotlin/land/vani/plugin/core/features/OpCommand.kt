package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.OpInventoryConfig
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.model.user.UserManager
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OpCommand(
    private val plugin: VanilandPlugin,
    private val opInventoryConfig: OpInventoryConfig,
    private val userManager: UserManager,
) : Feature<OpCommand>() {
    companion object : Key<OpCommand>("opCommand") {
        private const val OP_PERMS_GROUP = "op-perms"
    }

    override val key: Key<OpCommand> = Companion

    override suspend fun onEnable() {
        registerCommands()
    }

    private suspend fun registerCommands() {
        plugin.registerCommand(createGiveOpCommand())
        plugin.registerCommand(createRemoveOpCommand())
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun createGiveOpCommand() = command<CommandSender>("giveOp") {
        required { it.hasPermission(Permissions.ADMIN) }

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

            if (source.isOp) {
                sendMessage(
                    source,
                    text {
                        content("既にOP権限が付与されています")
                        color(NamedTextColor.RED)
                    }
                )
                return@runs
            }
            source.isOp = true
            userManager.modifyUser((source as Player).uniqueId) { user ->
                user.data().add(InheritanceNode.builder(OP_PERMS_GROUP).build())
            }

            opInventoryConfig.playerInventories[(source as Player).uniqueId] =
                (source as Player).inventory.contents
            (source as Player).inventory.contents =
                opInventoryConfig.opInventories[(source as Player).uniqueId].orEmpty()

            sendMessage(
                source,
                text {
                    content("OP権限を付与しました")
                    color(NamedTextColor.GREEN)
                }
            )
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun createRemoveOpCommand() = command<CommandSender>("removeOp") {
        required { it.hasPermission(Permissions.ADMIN) }

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

            if (!source.isOp) {
                sendMessage(
                    source,
                    text {
                        content("既にOP権限が剥奪されています")
                        color(NamedTextColor.RED)
                    }
                )
                return@runs
            }
            source.isOp = false
            userManager.modifyUser((source as Player).uniqueId) { user ->
                user.data().remove(InheritanceNode.builder(OP_PERMS_GROUP).build())
            }

            opInventoryConfig.opInventories[(source as Player).uniqueId] =
                (source as Player).inventory.contents
            (source as Player).inventory.contents =
                opInventoryConfig.playerInventories[(source as Player).uniqueId].orEmpty()

            sendMessage(
                source,
                text {
                    content("OP権限を剥奪しました")
                    color(NamedTextColor.GREEN)
                }
            )
        }
    }
}
