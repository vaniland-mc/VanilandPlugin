package land.vani.plugin.util

import org.bukkit.entity.Entity

val Entity.displayName: String
    get() = customName ?: name
