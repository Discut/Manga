package discut.manga.data.download

enum class DownloadState(
    val code: Int
) {
    Waiting(0),
    Downloading(1),
    Completed(2)
}