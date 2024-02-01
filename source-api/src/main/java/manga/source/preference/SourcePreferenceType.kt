package manga.source.preference

sealed interface SourcePreferenceType {

    var key: String
    var title: String
    var summary: String?

    data class TextFiled(
        override var key: String,
        override var title: String,
        override var summary: String? = null,
        var defaultValue: String,
        var onConfirm: (String) -> Unit,
    ) : SourcePreferenceType

}