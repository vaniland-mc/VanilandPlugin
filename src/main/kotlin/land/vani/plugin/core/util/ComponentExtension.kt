package land.vani.plugin.core.util

import net.kyori.adventure.text.Component

fun Iterable<Component>.joinToComponent(
    separator: Component = Component.newline(),
    prefix: Component = Component.empty(),
    postfix: Component = Component.empty(),
    limit: Int = -1,
    truncated: Component = Component.text("..."),
): Component = joinTo(Component.empty(), separator, prefix, postfix, limit, truncated)

@Suppress("LongParameterList")
fun Iterable<Component>.joinTo(
    buffer: Component,
    separator: Component = Component.newline(),
    prefix: Component = Component.empty(),
    postfix: Component = Component.empty(),
    limit: Int = -1,
    truncated: Component = Component.text("..."),
): Component {
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            buffer.append(element)
        } else {
            break
        }
    }
    if (limit in 0 until count) buffer.append(truncated)
    buffer.append(postfix)

    return buffer
}
