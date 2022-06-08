package land.vani.plugin.main.feature.listener

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import com.github.syari.spigot.api.event.Events

fun Events.registerAdvancementListener() {
    event<PlayerAdvancementCriterionGrantEvent> { event ->
        if (event.player.isOp) event.isCancelled = true
    }
}
