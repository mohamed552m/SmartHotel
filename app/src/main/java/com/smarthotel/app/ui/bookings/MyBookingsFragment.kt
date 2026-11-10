package com.smarthotel.app.ui.bookings

import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class MyBookingsFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_my_bookings

    override fun buildContent(): View {
        return buildMainScreen("My Bookings", "Booking", false, { navigator.showBookings() }) {
            addView(chipRow(listOf("Upcoming", "Completed", "Cancelled"), appState.currentBookingTab) {
                appState.currentBookingTab = it
                navigator.showBookings()
            })
            val visible = appState.bookings.filter {
                when (appState.currentBookingTab) {
                    "Cancelled" -> it.status == "Cancelled"
                    "Completed" -> it.status == "Completed"
                    else -> it.status == "Confirmed"
                }
            }
            addView(text("${appState.currentBookingTab} Bookings", 21, navy, true).apply { topMargin(dp(22)) })
            if (visible.isEmpty()) {
                addView(emptyState("No ${appState.currentBookingTab} bookings", "Your Smart Hotel bookings will appear here.").apply { topMargin(dp(22)) })
                addView(actionButton("Book a Room") { navigator.showSearch() }.apply { topMargin(dp(16)) })
            } else {
                visible.forEach { addView(bookingCard(it) { navigator.showBookings() }.apply { topMargin(dp(14)) }) }
            }
            addView(card().apply {
                addView(text("Need help with your booking?", 18, navy, true))
                addView(text("Support is available 24/7 for Smart Hotel guests.", 14, muted).apply { topMargin(dp(4)) })
                addView(outlineButton("Contact Support") { navigator.showHelpSupport() }.apply { topMargin(dp(12)) })
            }.apply { topMargin(dp(18)) })
        }
    }
}
