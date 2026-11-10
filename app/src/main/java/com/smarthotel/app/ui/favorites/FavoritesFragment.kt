package com.smarthotel.app.ui.favorites

import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class FavoritesFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_favorites

    override fun buildContent(): View {
        return buildMainScreen("Favorites", "Favorites", false, { navigator.showFavorites() }) {
            val favoriteRooms = appState.rooms.filter { appState.favorites.contains(it.id) }
            if (favoriteRooms.isEmpty()) {
                addView(emptyState("No favorite rooms yet", "Tap a heart on any Smart Hotel room to save it.").apply { topMargin(dp(40)) })
                addView(actionButton("Browse Rooms") { navigator.showSearch() }.apply { topMargin(dp(20)) })
            } else {
                favoriteRooms.forEach { addView(roomListCard(it) { navigator.showFavorites() }.apply { topMargin(dp(14)) }) }
            }
        }
    }
}
