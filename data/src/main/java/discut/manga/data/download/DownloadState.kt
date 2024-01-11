package discut.manga.data.download

enum class DownloadState(
    val code: Int
) {
    NotInQueue(-1),
    Waiting(0),
    Downloading(1),
    Completed(2),
    InQueue(3),
}