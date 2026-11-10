package com.smarthotel.app.ui.account

import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class AccountFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_account

    override fun buildContent(): View {
        val phone = appState.userPhone()
        return buildMainScreen("My Account", "Account", false, { navigator.showAccount() }) {
            val profile = card().apply {
                background = gradient("#05214A", "#0795A8", dp(18))
                addView(text(appState.userName(), 24, android.graphics.Color.WHITE, true))
                addView(text(appState.userEmail(), 16, android.graphics.Color.WHITE).apply { topMargin(dp(6)) })
                if (phone.isNotBlank()) {
                    addView(text(phone, 16, android.graphics.Color.WHITE).apply { topMargin(dp(6)) })
                }
                addView(outlineButton("Edit Profile") { showEditProfileDialog { navigator.showAccount() } }.apply { topMargin(dp(14)) })
            }
            addView(profile)
            addView(accountItem("Personal Information", "Update your personal details") { showEditProfileDialog { navigator.showAccount() } })
            addView(accountItem("My Bookings", "View and manage your bookings") { navigator.showBookings() })
            addView(accountItem("Payment Methods", "Manage your saved cards") { showPaymentMethodsDialog() })
            addView(accountItem("Notifications", "Manage notification preferences") { showNotificationsDialog() })
            addView(accountItem("Digital Check-in", "Save time with easy check-in") { navigator.showDigitalCheckIn() })
            addView(accountItem("Help & Support", "Get help and view FAQs") { navigator.showHelpSupport() })
            addView(accountItem("Log Out", "Sign out from your account", danger = true) {
                host.signOutUser()
                toast("Logged out")
                navigator.showLogin()
            })
            addView(card().apply {
                addView(text("Book more, get more!", 18, navy, true))
                addView(text("Join Smart Hotel Rewards and enjoy member benefits.", 14, muted).apply { topMargin(dp(4)) })
                addView(actionButton("Join Now") { toast("Rewards joined") }.apply { topMargin(dp(12)) })
            }.apply { topMargin(dp(18)) })
        }
    }
}
