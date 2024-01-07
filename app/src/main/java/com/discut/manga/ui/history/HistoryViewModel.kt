package com.discut.manga.ui.history

import androidx.lifecycle.viewModelScope
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.service.history.IHistoryProvider
import com.discut.manga.ui.history.component.HistoryItemType
import com.discut.manga.util.get
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import manga.core.preference.HistoryPreference
import manga.core.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyProvider: IHistoryProvider
) :
    BaseViewModel<HistoryState, HistoryEvent, HistoryEffect>() {

    private val historyPreference = PreferenceManager.get<HistoryPreference>()

    init {
        sendEvent(HistoryEvent.Init)
        historyPreference.getHistoryListLayoutAsFlow().distinctUntilChanged().map {
            HistoryItemType.values()[it]
        }.onEach {
            sendEvent(HistoryEvent.ChangedListLayout(it))
        }.launchIn(viewModelScope)
    }

    override fun initialState(): HistoryState = HistoryState()

    override suspend fun handleEvent(event: HistoryEvent, state: HistoryState): HistoryState {
        return when (event) {
            is HistoryEvent.Init -> {

                state.copy(
                    histories = withIOContext {
                        subscribe()
                    },
                    historyListLayout = HistoryItemType.values()[historyPreference.getHistoryListLayout()]
                )
            }

            is HistoryEvent.Remove -> {
                withIOContext {
                    historyProvider.removeAll(listOf(event.history))
                }
                state
            }

            is HistoryEvent.ClearAll -> {
                withIOContext {
                    val map = state.histories.value.filterIsInstance<HistoryAction.Item>()
                        .map { it.history }
                    historyProvider.removeAll(map)
                }
                state
            }

            is HistoryEvent.ChangeListLayout -> {
                historyPreference.setHistoryListLayout(event.layout.ordinal)
                state
            }

            is HistoryEvent.ChangedListLayout -> {
                state.copy(
                    historyListLayout = event.layout
                )
            }
        }
    }

    private fun paresDateToHistoryAction(histories: List<MangaChapterHistory>): List<HistoryAction> {
        var lastHistoryDate = Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))
        val historyActions: MutableList<HistoryAction> = mutableListOf()
        histories.forEachIndexed { index, mangaChapterHistory ->
            mangaChapterHistory.readAt.toDate().let {
                if (abs(lastHistoryDate.rinse().time - it.rinse().time - 24 * 60 * 60 * 1000) < 1000 * 5) {
                    historyActions.add(
                        HistoryAction.Header(paresDateString(it))
                    )
                    lastHistoryDate = it
                }
                historyActions.add(
                    HistoryAction.Item(mangaChapterHistory)
                )
            }
        }
        return historyActions
    }

    private fun paresDateString(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        val dayDiff = diff / (24 * 60 * 60 * 1000)
        return if (dayDiff == 0L) {
            "Today"
        } else if (dayDiff == 1L) {
            "Yesterday"
        } else {
            SimpleDateFormat("dd-MMM-yyyy").format(date)
        }

    }

    private suspend fun subscribe(): StateFlow<List<HistoryAction>> =
        historyProvider.subscribeAll()
            .map { it ->
                paresDateToHistoryAction(it.sortedBy { it.readAt }.reversed())
            }.stateIn(CoroutineScope(Dispatchers.IO))

    private fun Long.toDate(): Date {
        return Date(this)
    }

    private fun Date.rinse(): Date {
        hours = 0
        minutes = 0
        seconds = 0
        return this
    }

}
