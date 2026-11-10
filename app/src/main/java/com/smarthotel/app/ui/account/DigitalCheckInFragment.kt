package com.smarthotel.app.ui.account

import android.os.Bundle
import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class DigitalCheckInFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_digital_check_in
    private var bookingRef: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingRef = arguments?.getString(ARG_BOOKING_REF)
    }

    override fun buildContent(): View {
        val booking = appState.bookingByRef(bookingRef)
        return buildMainScreen("Digital Check-in", "Account", true, { navigator.showAccount() }) {
            addView(card().apply {
                addView(text("Smart Hotel Digital Check-in", 23, navy, true))
                addView(text("Use your phone as the smart lock key from 2:00 PM on check-in day.", 16, muted).apply { topMargin(dp(8)) })
                addView(actionButton("Start Check-in") {
                    if (booking == null) toast("Create a booking first") else toast("Digital check-in ready for ${booking.ref}")
                }.apply { topMargin(dp(16)) })
            })
        }
    }

    companion object {
        private const val ARG_BOOKING_REF = "booking_ref"

        fun newInstance(ref: String?): DigitalCheckInFragment {
            return DigitalCheckInFragment().apply {
                arguments = Bundle().apply { putString(ARG_BOOKING_REF, ref) }
            }
        }
    }
}
