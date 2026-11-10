package com.smarthotel.app.ui.confirmed

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class BookingConfirmedFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_booking_confirmed
    private var bookingRef: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingRef = arguments?.getString(ARG_BOOKING_REF)
    }

    override fun buildContent(): View {
        val booking = appState.bookingByRef(bookingRef) ?: return buildMainScreen("Booking Confirmed", "Booking", true, { navigator.showBookings() }) {
            addView(emptyState("No booking found", "Create a Smart Hotel booking first."))
        }
        val room = appState.roomById(booking.roomId)
        return buildMainScreen("Booking Confirmed", "Booking", true, { navigator.showBookings() }) {
            addView(stepper(3).apply { topMargin(dp(4)) })
            addView(card().apply {
                gravity = Gravity.CENTER_HORIZONTAL
                addView(text("Your booking is confirmed!", 24, navy, true).apply { gravity = Gravity.CENTER })
                addView(text("Booking Reference", 15, muted).apply { gravity = Gravity.CENTER; topMargin(dp(10)) })
                addView(text(booking.ref, 26, teal, true).apply { gravity = Gravity.CENTER })
                addView(outlineButton("Copy Reference") { toast("Reference copied: ${booking.ref}") }.apply { topMargin(dp(12)) })
            }.apply { topMargin(dp(18)) })
            addView(bookingDetailsCard(booking, room).apply { topMargin(dp(14)) })
            addView(priceDetails(room, booking).apply { topMargin(dp(14)) })
            addView(featureCard("Check-in is from 2:00 PM", "Use digital check-in with the smart lock.") { navigator.showDigitalCheckIn(booking.ref) })
            addView(featureCard("Need help?", "Our support team is available 24/7.") { navigator.showHelpSupport() })
            addView(row(Gravity.CENTER).apply {
                addView(outlineButton("Share Booking") { shareBooking(booking) }, LinearLayout.LayoutParams(0, dp(56), 1f))
                addView(space(12, 0))
                addView(actionButton("View My Bookings") { navigator.showBookings() }, LinearLayout.LayoutParams(0, dp(56), 1f))
            }.apply { topMargin(dp(16)) })
        }
    }

    companion object {
        private const val ARG_BOOKING_REF = "booking_ref"

        fun newInstance(ref: String?): BookingConfirmedFragment {
            return BookingConfirmedFragment().apply {
                arguments = Bundle().apply { putString(ARG_BOOKING_REF, ref) }
            }
        }
    }
}
