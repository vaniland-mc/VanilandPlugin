package land.vani.plugin.core.features

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.timer.timerFlow
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.text.Component

object AutoMessage : Feature<AutoMessage>() {
    override val key: Key<AutoMessage> = Key("autoMessage")

    private var messages: Sequence<Component> = sequenceOf()
    private var messagesIterator: Iterator<Component> = messages.iterator()

    private var job: Job? = null

    override suspend fun onEnable(plugin: VanilandPlugin) {
        job?.cancel()
        messages = getMessages(plugin)
        messagesIterator = messages.iterator()

        job = timerFlow(plugin.autoMessageConfig.period).onEach {
            withContext(plugin.mainThreadDispatcher) {
                val message = messagesIterator.next()
                plugin.server.broadcast(message)
            }
        }.launchIn(plugin + Dispatchers.Unconfined)
    }

    override suspend fun onDisable(plugin: VanilandPlugin) {
        job?.cancel()
    }

    fun reload(plugin: VanilandPlugin) {
        messages = getMessages(plugin)
        messagesIterator = messages.iterator()
    }

    private fun getMessages(plugin: VanilandPlugin) = generateSequence {
        plugin.autoMessageConfig.messages.map {
            plugin.autoMessageConfig.prefix + it
        }
    }.flatten()
}
