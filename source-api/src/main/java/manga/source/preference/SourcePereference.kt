package manga.source.preference

interface SourcePreference {

    fun textFiledPreference(builder: () -> SourcePreferenceType.TextFiled)

    fun <T : Any> listSelectPreference(builder: () -> SourcePreferenceType.ListSelect<T>)

    fun switchPreference(builder: () -> SourcePreferenceType.Switch)

}

internal class SourcePreferenceImpl : SourcePreference {
    internal val preferences = mutableListOf<SourcePreferenceType<*>>()
    override fun textFiledPreference(builder: () -> SourcePreferenceType.TextFiled) {
        builder().apply(preferences::add)
    }

    override fun <T : Any> listSelectPreference(builder: () -> SourcePreferenceType.ListSelect<T>) {
        builder().apply(preferences::add)
    }

    override fun switchPreference(builder: () -> SourcePreferenceType.Switch) {
        builder().apply(preferences::add)
    }

}