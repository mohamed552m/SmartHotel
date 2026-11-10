package com.smarthotel.app

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smarthotel.app.data.AppState
import com.smarthotel.app.ui.account.AccountFragment
import com.smarthotel.app.ui.account.DigitalCheckInFragment
import com.smarthotel.app.ui.account.HelpSupportFragment
import com.smarthotel.app.ui.auth.LoginFragment
import com.smarthotel.app.ui.auth.SignUpFragment
import com.smarthotel.app.ui.booking.ConfirmBookingFragment
import com.smarthotel.app.ui.bookings.BookingDetailsFragment
import com.smarthotel.app.ui.bookings.MyBookingsFragment
import com.smarthotel.app.ui.common.ScreenNavigator
import com.smarthotel.app.ui.confirmed.BookingConfirmedFragment
import com.smarthotel.app.ui.details.RoomDetailsFragment
import com.smarthotel.app.ui.favorites.FavoritesFragment
import com.smarthotel.app.ui.home.HomeFragment
import com.smarthotel.app.ui.payment.PaymentFragment
import com.smarthotel.app.ui.search.SearchFragment
import com.smarthotel.app.ui.welcome.WelcomeFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : Activity(), ScreenNavigator {
    lateinit var appState: AppState
        private set

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var contentFrame: FrameLayout
    private lateinit var bottomNav: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        appState = AppState(this)
        contentFrame = findViewById(R.id.contentFrame)
        bottomNav = findViewById(R.id.bottomNav)
        if (savedInstanceState == null) {
            firebaseAuth.currentUser?.let { currentUser ->
                appState.syncFromFirebaseUser(currentUser)
            }
            showWelcome()
        }
    }

    fun auth(): FirebaseAuth = firebaseAuth

    override fun showWelcome() {
        hideBottomNav()
        replace(WelcomeFragment(), false)
    }

    override fun showLogin() {
        hideBottomNav()
        replace(LoginFragment(), true)
    }

    override fun showSignUp() {
        hideBottomNav()
        replace(SignUpFragment(), true)
    }

    override fun showHome() {
        replace(HomeFragment(), false)
    }

    override fun showSearch() {
        replace(SearchFragment(), false)
    }

    override fun showFavorites() {
        replace(FavoritesFragment(), false)
    }

    override fun showBookings() {
        replace(MyBookingsFragment(), false)
    }

    override fun showAccount() {
        replace(AccountFragment(), false)
    }

    override fun showRoomDetails(roomId: String) {
        appState.selectRoom(roomId)
        replace(RoomDetailsFragment.newInstance(roomId), true)
    }

    override fun showConfirmBooking() {
        replace(ConfirmBookingFragment(), true)
    }

    override fun showPayment() {
        replace(PaymentFragment(), true)
    }

    override fun showBookingConfirmed(ref: String?) {
        replace(BookingConfirmedFragment.newInstance(ref), true)
    }

    override fun showBookingDetails(ref: String) {
        replace(BookingDetailsFragment.newInstance(ref), true)
    }

    override fun showDigitalCheckIn(ref: String?) {
        replace(DigitalCheckInFragment.newInstance(ref), true)
    }

    override fun showHelpSupport() {
        replace(HelpSupportFragment(), true)
    }

    fun showBottomNav(active: String) {
        bottomNav.visibility = View.VISIBLE
        bottomNav.removeAllViews()
        bottomNav.setPadding(dp(8), dp(8), dp(8), dp(10))
        listOf(
            NavItemSpec("Home", R.drawable.ic_nav_home),
            NavItemSpec("Search", R.drawable.ic_nav_search),
            NavItemSpec("Favorites", R.drawable.ic_nav_favorites),
            NavItemSpec("Booking", R.drawable.ic_nav_booking),
            NavItemSpec("Account", R.drawable.ic_nav_account)
        ).forEach { item ->
            val selected = item.label == active
            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = navItemBackground(selected)
                setPadding(dp(4), dp(8), dp(4), dp(8))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    when (item.label) {
                        "Home" -> showHome()
                        "Search" -> showSearch()
                        "Favorites" -> showFavorites()
                        "Booking" -> showBookings()
                        "Account" -> showAccount()
                    }
                }
            }
            container.addView(ImageView(this).apply {
                setImageResource(item.iconRes)
                imageTintList = ColorStateList.valueOf(if (selected) Color.WHITE else Color.rgb(168, 214, 222))
            }, LinearLayout.LayoutParams(dp(22), dp(22)))
            container.addView(TextView(this).apply {
                text = item.label
                gravity = Gravity.CENTER
                textSize = 11.5f
                setTextColor(if (selected) Color.WHITE else Color.rgb(214, 229, 236))
                setPadding(0, dp(4), 0, 0)
            })
            bottomNav.addView(container, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                marginStart = dp(4)
                marginEnd = dp(4)
            })
        }
    }

    fun hideBottomNav() {
        bottomNav.visibility = View.GONE
    }

    fun setDarkSystemBars() {
        val dark = Color.parseColor("#020814")
        window.statusBarColor = dark
        window.navigationBarColor = dark
    }

    fun setLightSystemBars() {
        val light = Color.rgb(246, 250, 253)
        window.statusBarColor = light
        window.navigationBarColor = light
    }

    fun showForgotPasswordDialog() {
        val email = EditText(this).apply {
            hint = "Email or phone number"
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your Smart Hotel account email or phone.")
            .setView(email)
            .setPositiveButton("Send Reset") { _, _ ->
                val value = AppState.normalizeDigits(email.text.toString().trim())
                when {
                    value.isBlank() -> toast("Please enter your email address")
                    !value.contains("@") -> toast("Please login with email for this version")
                    !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> toast("Please enter a valid email address")
                    else -> {
                        firebaseAuth.sendPasswordResetEmail(value)
                            .addOnCompleteListener(this) { task ->
                                toast(
                                    if (task.isSuccessful) "Reset email sent"
                                    else task.exception?.localizedMessage ?: "Unable to send reset email"
                                )
                            }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun signOutUser() {
        firebaseAuth.signOut()
        appState.clearLocalSession()
    }

    fun toast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun replace(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment)
        if (addToBackStack) transaction.addToBackStack(fragment.javaClass.simpleName)
        transaction.commit()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun navItemBackground(selected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadius = dp(18).toFloat()
            setColor(if (selected) Color.rgb(7, 149, 168) else Color.TRANSPARENT)
        }
    }

    private data class NavItemSpec(val label: String, val iconRes: Int)
}
