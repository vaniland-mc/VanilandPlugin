package land.vani.plugin.core.features

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import land.vani.mcorouhlin.paper.event.cancelIf
import land.vani.plugin.core.VanilandPlugin

class SuppressAdvancementOp(
    private val plugin: VanilandPlugin,
) : Feature<SuppressAdvancementOp>() {
    companion object : Key<SuppressAdvancementOp>("suppressAchievementsOp")

    override val key: Key<SuppressAdvancementOp> = Companion

    override suspend fun onEnable() {
        registerListeners()
    }

    private fun registerListeners() = plugin.events {
        cancelIf<PlayerAdvancementCriterionGrantEvent> { event ->
            event.player.isOp
        }
    }
}
