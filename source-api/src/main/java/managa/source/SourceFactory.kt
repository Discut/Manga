package managa.source

/**
 * Create a list of [Source] at runtime
 * Its a interface
 */
interface SourceFactory {
    /**
     * Create a list of [Source] from extension
     */
    fun createSources(): List<Source>
}