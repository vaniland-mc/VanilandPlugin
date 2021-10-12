package land.vani.plugin

import com.github.syari.spigot.api.event.events
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import land.vani.plugin.command.inspectorCommand
import land.vani.plugin.di.makeModules
import land.vani.plugin.listener.mcBansLookup
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import kotlin.coroutines.CoroutineContext

class VanilandPlugin : JavaPlugin(), KoinComponent {
    override fun onEnable() {
        setupKoin()
        registerEvents()
        registerCommands()
    }

    override fun onDisable() {
        cancel("Disabling plugin")
    }

    private fun setupKoin() {
        val modules = makeModules(this)
        startKoin {
            modules(modules)
        }
    }

    private fun registerEvents() {
        events {
            mcBansLookup(get())
        }
    }

    private fun registerCommands() {
        inspectorCommand()
    }

    companion object : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job()
    }
}
