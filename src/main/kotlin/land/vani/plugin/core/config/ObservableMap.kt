package land.vani.plugin.core.config

class ObservableMap<K, V>(
    private val delegate: MutableMap<K, V>,
    private val putCallback: (K, V) -> Unit,
    private val removeCallback: (K) -> Unit,
    private val clearCallback: () -> Unit,
) : MutableMap<K, V> by delegate {
    override fun put(key: K, value: V): V? {
        return delegate.put(key, value).also {
            putCallback(key, value)
        }
    }

    override fun remove(key: K): V? {
        return delegate.remove(key).also {
            removeCallback(key)
        }
    }

    override fun clear() {
        delegate.clear().also {
            clearCallback()
        }
    }
}
