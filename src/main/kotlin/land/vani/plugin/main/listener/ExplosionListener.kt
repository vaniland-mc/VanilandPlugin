package land.vani.plugin.main.listener

import com.github.syari.spigot.api.event.Events
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityExplodeEvent

private val EXPLODE_CAUSE_ENTITIES = setOf(
    EntityType.CREEPER,
    EntityType.FIREBALL,
    EntityType.DRAGON_FIREBALL,
    EntityType.SMALL_FIREBALL,
    EntityType.MINECART_TNT,
    EntityType.PRIMED_TNT,
)

private const val POWER = 4f // TNT power

fun Events.registerExplosionListener() {
    event<EntityExplodeEvent> { event ->
        if (event.entityType in EXPLODE_CAUSE_ENTITIES) {
            event.isCancelled = true
            event.location.createExplosion(POWER, false, false)
        }
    }
}
