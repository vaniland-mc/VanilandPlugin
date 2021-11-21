package land.vani.plugin.main.feature.command

import com.github.syari.spigot.api.command.command
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.feature.command.util.getTarget
import land.vani.plugin.main.permission.OP_COMMAND
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.UserManager
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.entity.Player
import org.koin.core.component.get

private const val OP_PERMS_GROUP = "op-perms"

fun VanilandPlugin.opCommand() {
    val luckPerms = get<LuckPerms>()
    val userManager = luckPerms.userManager

    command("giveop") {
        permission = OP_COMMAND
        execute {
            val target = if (sender !is Player) {
                getTarget(sender, args, 0) ?: return@execute
            } else {
                sender as Player
            }
            onOpCommand(userManager, target)
        }
    }
    command("removeop") {
        permission = OP_COMMAND
        execute {
            val target = if (sender !is Player) {
                getTarget(sender, args, 0) ?: return@execute
            } else {
                sender as Player
            }
            onDeopCommand(userManager, target)
        }
    }
}

private fun onOpCommand(userManager: UserManager, target: Player) {
    if (target.isOp) {
        target.sendMessage(text {
            content("既にOP権限が付与されています")
            color(NamedTextColor.RED)
        })
        return
    }
    target.isOp = true
    userManager.modifyUser(target.uniqueId) { user ->
        user.data().add(InheritanceNode.builder(OP_PERMS_GROUP).build())
    }
    target.sendMessage(text {
        content(target.name)
        color(NamedTextColor.AQUA)
    } + text {
        content("にOP権限を付与しました")
        color(NamedTextColor.WHITE)
    })
}

private fun onDeopCommand(userManager: UserManager, target: Player) {
    if (target.isOp) {
        target.sendMessage(text {
            content("OP権限が付与されていません")
            color(NamedTextColor.RED)
        })
        return
    }
    target.isOp = false
    userManager.modifyUser(target.uniqueId) { user ->
        user.data().remove(InheritanceNode.builder(OP_PERMS_GROUP).build())
    }
    target.sendMessage(text {
        content(target.name)
        color(NamedTextColor.AQUA)
    } + text {
        content("からOP権限を剥奪しました")
        color(NamedTextColor.WHITE)
    })
}
