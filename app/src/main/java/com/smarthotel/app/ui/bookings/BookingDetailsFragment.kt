package com.smarthotel.app.ui.bookings

import android.os.Bundle
import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class BookingDetailsFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_booking_details
    private var bookingRef: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingRef = arguments?.getString(ARG_BOOKING_REF)
    }

    override fun buildContent(): View {
        val booking = appState.bookingByRef(bookingRef) ?: return buildMainScreen("Booking Details", "Booking", true, { navigator.showBookings() }) {
            addView(emptyState("No booking found", "Create a Smart Hotel booking first."))
        }
        val room = appState.roomById(booking.roomId)
        return buildMainScreen("Booking Details", "Booking", true, { navigator.showBookings() }) {
            addView(bookingDetailsCard(booking, room))
            addView(priceDetails(room, booking).apply { topMargin(dp(14)) })
            addView(actionButton("Digital Check-in") { navigator.showDigitalCheckIn(booking.ref) }.apply { topMargin(dp(14)) })
            if (booking.status == "Confirmed") {
                addView(outlineButton("Cancel Booking") {
                    appState.cancelBooking(booking.ref)
                    toast("Booking cancelled")
                    navigator.showBookings()
                }.apply { topMargin(dp(12)) })
            }
            addView(outlineButton("Rate Your Stay") { showRatingDialog() }.apply { topMargin(dp(12)) })
        }
    }

    companion object {
        private const val ARG_BOOKING_REF = "booking_ref"

        fun newInstance(ref: String): BookingDetailsFragment {
            return BookingDetailsFragment().apply {
                arguments = Bundle().apply { putString(ARG_BOOKING_REF, ref) }
            }
        }
    }
}
