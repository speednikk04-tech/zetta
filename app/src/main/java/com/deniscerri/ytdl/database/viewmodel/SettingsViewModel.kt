package com.deniscerri.ytdl.database.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.deniscerri.ytdl.App
import com.deniscerri.ytdl.database.DBManager
import com.deniscerri.ytdl.database.models.SearchSettingsItem
import com.deniscerri.ytdl.database.repository.CommandTemplateRepository
import com.deniscerri.ytdl.database.repository.CookieRepository
import com.deniscerri.ytdl.database.repository.DownloadRepository
import com.deniscerri.ytdl.database.repository.HistoryRepository
import com.deniscerri.ytdl.database.repository.ObserveSourcesRepository
import com.deniscerri.ytdl.database.repository.SearchHistoryRepository
import com.deniscerri.ytdl.ui.more.settings.SettingsRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {
    private val workManager : WorkManager = WorkManager.getInstance(application)
    private val preferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    private val historyRepository : HistoryRepository
    private val downloadRepository : DownloadRepository
    private val cookieRepository : CookieRepository
    private val commandTemplateRepository : CommandTemplateRepository
    private val searchHistoryRepository : SearchHistoryRepository
    private val observeSourcesRepository : ObserveSourcesRepository

    private val _settingsFlow = MutableStateFlow<List<SearchSettingsItem>>(emptyList())
    val settingsFlow: StateFlow<Pair<List<SearchSettingsItem>, String>>
    private val _searchQuery = MutableStateFlow("")

    init {
        val dbManager = DBManager.getInstance(application)
        historyRepository = HistoryRepository(dbManager.historyDao)
        downloadRepository = DownloadRepository(dbManager.downloadDao)
        cookieRepository = CookieRepository(dbManager.cookieDao)
        commandTemplateRepository = CommandTemplateRepository(dbManager.commandTemplateDao)
        searchHistoryRepository = SearchHistoryRepository(dbManager.searchHistoryDao)
        observeSourcesRepository = ObserveSourcesRepository(dbManager.observeSourcesDao)

        settingsFlow = combine(_settingsFlow, _searchQuery) { items, query ->
            if (query.isBlank()) {
                Pair(emptyList(), "")
            } else {
                Pair(items, query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), ""))
    }

    fun indexSearchSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val indexedItems = SettingsRegistry.indexAll(App.instance)
            _settingsFlow.emit(indexedItems)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

}
