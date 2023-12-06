package discut.manga.source.local.metadata

import managa.source.domain.SManga
import java.io.InputStream

interface IMetadataParser {
    fun parse(metadata: InputStream, manga: SManga)
}