package com.smarthotel.app.ui.payment

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.smarthotel.app.R
import com.smarthotel.app.data.Booking
import com.smarthotel.app.ui.common.BaseProgrammaticFragment
import java.time.LocalDate

class PaymentFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_payment

    override fun buildContent(): View {
        val room = appState.selectedRoom()
        return buildMainScreen("Payment", "Booking", true, { navigator.showConfirmBooking() }) {
            addView(stepper(2).apply { topMargin(dp(4)) })
            addView(summaryRoomCard(room).apply { topMargin(dp(18)) })
            addView(priceDetails(room).apply { topMargin(dp(14)) })
            addView(sectionCard("Select Payment Method").apply {
                listOf("Credit / Debit Card", "Meeza Card", "Apple Pay", "Cash at Hotel").forEach {
                    addView(paymentMethodRow(it) { navigator.showPayment() })
                }
            }.apply { topMargin(dp(14)) })
            addView(card().apply {
                addView(text("Secure Payment", 20, navy, true))
                addView(text("Your payment information is encrypted and secure.", 15, muted).apply { topMargin(dp(6)) })
            }.apply { topMargin(dp(14)) })
            addView(row(Gravity.CENTER_VERTICAL).apply {
                addView(text("Total Amount\nEGP ${money(totalPrice(room))}", 22, teal, true), LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(actionButton("Pay Now") {
                    val booking = Booking(
                        ref = appState.bookingRef(),
                        roomId = room.id,
                        checkIn = appState.checkIn,
                        checkOut = appState.checkOut,
                        adults = appState.adultGuests,
                        children = appState.childGuests,
                        payment = appState.selectedPaymentMethod,
                        status = "Confirmed",
                        createdAt = LocalDate.now()
                    )
                    appState.addBooking(booking)
                    navigator.showBookingConfirmed(booking.ref)
                }, LinearLayout.LayoutParams(dp(180), dp(56)))
            }.apply { topMargin(dp(18)) })
        }
    }
}
