package land.vani.plugin.core.config

class ObservableList<T>(
    private val delegate: MutableList<T>,
    private val addCallback: (T) -> Unit,
    private val removeCallback: () -> Unit,
) : MutableList<T> by delegate {
    override fun add(index: Int, element: T) {
        delegate.add(index, element)
        addCallback(element)
    }

    override fun remove(element: T): Boolean {
        return delegate.remove(element).also {
            removeCallback()
        }
    }

    override fun removeAt(index: Int): T {
        return delegate.removeAt(index).also {
            removeCallback()
        }
    }
}
