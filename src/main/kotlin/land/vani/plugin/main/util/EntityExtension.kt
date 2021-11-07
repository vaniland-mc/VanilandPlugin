package land.vani.plugin.main.util

import org.bukkit.entity.Entity

val Entity.displayName: String
    get() = customName ?: name
