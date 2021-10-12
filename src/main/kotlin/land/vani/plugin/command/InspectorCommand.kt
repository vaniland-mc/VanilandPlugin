package land.vani.plugin.command

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.permission.INSPECTOR_MODE
import org.bukkit.Bukkit

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

        }
    }
}
