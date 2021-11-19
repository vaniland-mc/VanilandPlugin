package land.vani.plugin.main.feature.command.util

import com.github.syari.spigot.api.command.CommandArgument
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun getSenderOrTarget(sender: CommandSender, args: CommandArgument, targetNameIndex: Int): Player? =
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
        getTarget(sender, args, targetNameIndex)
    }

fun getTarget(sender: CommandSender, args: CommandArgument, targetNameIndex: Int): Player? {
    val targetName = args[targetNameIndex]
    return Bukkit.getPlayer(targetName) ?: run {
        sender.sendMessage(
            text {
                content("対象プレイヤーが見つかりませんでした")
                color(NamedTextColor.RED)
            }
        )
        null
    }
}
