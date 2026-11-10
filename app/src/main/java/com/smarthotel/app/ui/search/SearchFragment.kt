package com.smarthotel.app.ui.search

import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class SearchFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_search

    override fun buildContent(): View {
        return buildMainScreen("Search", "Search", true, { navigator.showHome() }) {
            addView(text("Search Smart Hotel rooms and refine by room type.", 16, muted).apply { topMargin(dp(4)) })
            addView(searchBox(appState.currentQuery) {
                appState.currentQuery = it
                navigator.showSearch()
            }.apply { topMargin(dp(18)) })
            addView(chipRow(listOf("Any type", "Standard", "Deluxe", "Suite"), appState.currentCategory) {
                appState.currentCategory = it
                navigator.showSearch()
            }.apply { topMargin(dp(16)) })
            val filtered = filteredRooms()
            addView(text("${filtered.size} room${if (filtered.size == 1) "" else "s"} available", 15, teal, true).apply { topMargin(dp(18)) })
            if (filtered.isEmpty()) {
                addView(emptyState("No matching rooms", "Try another Smart Hotel room type.").apply { topMargin(dp(26)) })
            } else {
                filtered.forEach { addView(roomListCard(it) { navigator.showSearch() }.apply { topMargin(dp(14)) }) }
            }
        }
    }
}
