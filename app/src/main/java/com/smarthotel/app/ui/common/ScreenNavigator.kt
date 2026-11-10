package com.smarthotel.app.ui.common

interface ScreenNavigator {
    fun showWelcome()
    fun showLogin()
    fun showSignUp()
    fun showHome()
    fun showSearch()
    fun showFavorites()
    fun showBookings()
    fun showAccount()
    fun showRoomDetails(roomId: String)
    fun showConfirmBooking()
    fun showPayment()
    fun showBookingConfirmed(ref: String? = null)
    fun showBookingDetails(ref: String)
    fun showDigitalCheckIn(ref: String? = null)
    fun showHelpSupport()
}
