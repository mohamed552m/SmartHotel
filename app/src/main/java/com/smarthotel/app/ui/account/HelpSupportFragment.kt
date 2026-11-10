package com.smarthotel.app.ui.account

import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class HelpSupportFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_help_support

    override fun buildContent(): View {
        return buildMainScreen("Help & Support", "Account", true, { navigator.showAccount() }) {
            addView(card().apply {
                addView(text("How can we help?", 22, navy, true))
                addView(text("Smart Hotel support is available 24/7.", 16, muted).apply { topMargin(dp(6)) })
                addView(outlineButton("Call Support") { toast("Calling support...") }.apply { topMargin(dp(14)) })
                addView(outlineButton("Send Message") { toast("Message sent to support") }.apply { topMargin(dp(10)) })
            })
            addView(featureCard("Booking changes", "Update guests or cancel from My Bookings.") { navigator.showBookings() })
            addView(featureCard("Payment help", "Manage your payment methods from Account.") { showPaymentMethodsDialog() })
        }
    }
}
