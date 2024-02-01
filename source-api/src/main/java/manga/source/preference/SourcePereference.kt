package manga.source.preference

interface SourcePreference {

    fun textFiledPreference(builder: SourcePreferenceType.TextFiled.() -> Unit)

}

internal class SourcePreferenceImpl : SourcePreference {
    internal val preferences = mutableListOf<SourcePreferenceType>()
    override fun textFiledPreference(builder: SourcePreferenceType.TextFiled.() -> Unit) {
        SourcePreferenceType.TextFiled(
            title = "",
            key = "",
            defaultValue = "",
            onConfirm = {})
            .apply(builder)
            .apply(preferences::add)
    }

}