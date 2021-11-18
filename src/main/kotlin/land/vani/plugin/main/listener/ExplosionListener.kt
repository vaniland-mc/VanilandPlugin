package land.vani.plugin.main.listener

import com.github.syari.spigot.api.event.Events
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent

private val EXPLODE_CAUSE_ENTITIES = setOf(
    EntityType.CREEPER,
    EntityType.FIREBALL,
    EntityType.DRAGON_FIREBALL,
    EntityType.SMALL_FIREBALL,
    EntityType.MINECART_TNT,
    EntityType.PRIMED_TNT,
    EntityType.WITHER_SKULL,
    EntityType.WITHER,
)

fun Events.registerExplosionListener() {
    event<EntityExplodeEvent> { event ->
        if (event.entityType in EXPLODE_CAUSE_ENTITIES) {
            event.isCancelled = true
            event.location.createExplosion(event.yield, false, false)
        }
    }
}

fun Events.registerDisableWitherBlockBreak() {
    event<EntityChangeBlockEvent> { event ->
        if (event.entityType != EntityType.WITHER) return@event
        event.isCancelled = true
    }
}
