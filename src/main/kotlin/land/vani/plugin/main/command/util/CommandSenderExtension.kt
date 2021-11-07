package land.vani.plugin.main.command.util

import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
inline fun CommandSender.requirePlayer() {
    contract { returns() implies (this@requirePlayer is Player) }
    if (this !is Player) {
        sendMessage(text {
            content("このコマンドはゲーム内からのみ実行できます")
            color(NamedTextColor.RED)
        })
        return
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun unknownCommand(sender: CommandSender) {
    sender.sendMessage(text {
        content("不明なコマンドです")
        color(NamedTextColor.RED)
    })
    return
}
