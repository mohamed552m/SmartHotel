package com.smarthotel.app.ui.booking

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment
import java.time.format.DateTimeFormatter
import java.util.Locale

class ConfirmBookingFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_confirm_booking
    private val policyFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US)

    override fun buildContent(): View {
        val room = appState.selectedRoom()
        return buildMainScreen("Confirm Booking", "Booking", true, { navigator.showRoomDetails(room.id) }) {
            addView(stepper(1).apply { topMargin(dp(4)) })
            addView(summaryRoomCard(room).apply { topMargin(dp(18)) })
            addView(sectionCard("Stay Dates", "Edit") { showCheckoutPicker { navigator.showConfirmBooking() } }.apply {
                addView(dateSummary())
            }.apply { topMargin(dp(14)) })
            addView(sectionCard("Guests", "Edit") { showGuestSelector { navigator.showConfirmBooking() } }.apply {
                addView(text("${appState.adultGuests} Adults   ${if (appState.childGuests == 0) "No children" else "${appState.childGuests} Children"}", 17, navy, true))
            }.apply { topMargin(dp(14)) })
            addView(priceDetails(room).apply { topMargin(dp(14)) })
            addView(featureCard("Free cancellation", "Available until ${appState.checkIn.minusDays(1).format(policyFormatter)}.") {
                info("Policy", "You can cancel before check-in day with no hidden fees.")
            })
            addView(row(Gravity.CENTER_VERTICAL).apply {
                addView(text("Total Price\nEGP ${money(totalPrice(room))}", 22, teal, true), LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(actionButton("Continue to Payment") { navigator.showPayment() }, LinearLayout.LayoutParams(dp(210), dp(56)))
            }.apply { topMargin(dp(16)) })
        }
    }
}
