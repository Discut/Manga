package discut.manga.source.local.manager

import discut.manga.source.local.metadata.IMetadataParser
import managa.source.domain.SManga
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

const val NODE_ROOT = "ComicInfo"

class XmlMetadataParser : IMetadataParser {
    override fun parse(metadata: InputStream, manga: SManga) {
        val factory = DocumentBuilderFactory.newInstance()
        val xmlDoc = factory.newDocumentBuilder().parse(metadata)
        xmlDoc.documentElement.normalize()
        if (xmlDoc.documentElement.nodeName.equals(NODE_ROOT, true).not()) {
            return
        }
        val root = xmlDoc.documentElement
        root.getElementsByTagName("Title").let {
            if (it.length > 0) {
                manga.title = it.item(0).textContent
            }
        }
        root.getElementsByTagName("Writer").let {
            if (it.length > 0) {
                manga.artist = it.item(0).textContent
            }
        }
        root.getElementsByTagName("Author").let {
            if (it.length > 0) {
                manga.author = it.item(0).textContent
            }
        }
        root.getElementsByTagName("Summary").let {
            if (it.length > 0) {
                manga.description = it.item(0).textContent
            }
        }
        root.getElementsByTagName("Tags").let {
            if (it.length > 0) {
                manga.genre = it.item(0).textContent
            }
        }

    }
}