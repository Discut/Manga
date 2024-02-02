package manga.source.preference

sealed interface SourcePreferenceType<T : Any> {

    val key: String
    val defaultValue: T
    val title: String
    val summary: String?
    val summaryBuilder: ((Map<String, Any>) -> String)?

    data class TextFiled(
        override val key: String,
        override val defaultValue: String,
        override val title: String,
        override val summary: String? = null,
        override val summaryBuilder: ((Map<String, Any>) -> String)? = null,
    ) : SourcePreferenceType<String>

    data class ListSelect<T : Any>(
        override val key: String,
        override val defaultValue: T,
        override val title: String,
        override val summary: String? = null,
        override val summaryBuilder: ((Map<String, Any>) -> String)? = null,
        val values: List<String>,
    ) : SourcePreferenceType<T>

    data class Switch(
        override val key: String,
        override val defaultValue: Boolean,
        override val title: String,
        override val summary: String? = null,
        override val summaryBuilder: ((Map<String, Any>) -> String)? = null,
    ):SourcePreferenceType<Boolean>

}