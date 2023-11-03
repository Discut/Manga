package managa.source

/**
 * Its a basic interface from [Source], extension developer can extend it.
 * it could be online source or local source.
 */
interface Source {
    val id: Long
    val name: String
    val language: String
}