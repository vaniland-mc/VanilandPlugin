package land.vani.plugin.core.features

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.AutoMessagesConfig
import land.vani.plugin.core.timer.timerFlow
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.text.Component

class AutoMessage(
    private val plugin: VanilandPlugin,
    private val autoMessagesConfig: AutoMessagesConfig,
) : Feature<AutoMessage>() {
    companion object : Key<AutoMessage>("autoMessage")

    override val key: Key<AutoMessage> = Companion

    private var messages: Sequence<Component> = sequenceOf()
    private var messagesIterator: Iterator<Component> = messages.iterator()

    private var job: Job? = null

    override suspend fun onEnable() {
        job?.cancel()
        messages = getMessages()
        messagesIterator = messages.iterator()

        job = timerFlow(autoMessagesConfig.period).onEach {
            withContext(plugin.mainThreadDispatcher) {
                val message = messagesIterator.next()
                plugin.server.broadcast(message)
            }
        }.launchIn(plugin + Dispatchers.Unconfined)
    }

    override suspend fun onDisable() {
        job?.cancel()
    }

    fun reload() {
        messages = getMessages()
        messagesIterator = messages.iterator()
    }

    private fun getMessages() = generateSequence {
        autoMessagesConfig.messages.map {
            autoMessagesConfig.prefix + it
        }
    }.flatten()
}
