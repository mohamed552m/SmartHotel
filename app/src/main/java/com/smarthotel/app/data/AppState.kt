package com.smarthotel.app.data

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppState(context: Context) {
    private val prefs = context.getSharedPreferences("smart_hotel_state", Context.MODE_PRIVATE)

    val rooms: List<Room> = SampleData.rooms
    var favorites: MutableSet<String> = prefs.getStringSet("favorites", emptySet())?.toMutableSet() ?: mutableSetOf()
    var bookings: MutableList<Booking> = loadBookings()
    var selectedRoomId: String = prefs.getString("selected_room_id", "deluxe") ?: "deluxe"
    var checkIn: LocalDate = LocalDate.now()
    var checkOut: LocalDate = LocalDate.now().plusDays(1)
    var adultGuests: Int = 2
    var childGuests: Int = 0
    var selectedPaymentMethod: String = "Credit / Debit Card"
    var currentQuery: String = ""
    var currentCategory: String = "Any type"
    var currentBookingTab: String = "Upcoming"
    var currentPriceFilter: String = "Any"
    var currentAmenityFilter: String = "All"
    var currentRatingFilter: String = "All"
    var lastBookingRef: String? = null
    var isGuestSession: Boolean = prefs.getBoolean("guest_mode", false)

    fun isSignedIn(): Boolean = prefs.getBoolean("signed_in", false)

    fun setSignedIn(value: Boolean) {
        prefs.edit().putBoolean("signed_in", value).apply()
    }

    fun setGuestSession() {
        isGuestSession = true
        prefs.edit()
            .putBoolean("guest_mode", true)
            .putBoolean("signed_in", true)
            .putString("user_name", "Guest User")
            .putString("user_email", "Guest account")
            .putString("user_phone", "")
            .apply()
    }

    fun clearGuestSession() {
        isGuestSession = false
        prefs.edit().putBoolean("guest_mode", false).apply()
    }

    fun clearLocalSession() {
        isGuestSession = false
        prefs.edit()
            .putBoolean("signed_in", false)
            .putBoolean("guest_mode", false)
            .apply()
    }

    fun syncFromFirebaseUser(user: FirebaseUser?) {
        if (user == null) return
        isGuestSession = false
        val savedName = prefs.getString("user_name", "") ?: ""
        val localPhone = prefs.getString("user_phone", "") ?: ""
        val finalName = normalizeDigits(
            when {
                !user.displayName.isNullOrBlank() -> user.displayName!!
                savedName.isNotBlank() && savedName != "Guest User" -> savedName
                else -> "Smart Hotel User"
            }
        )
        val finalEmail = normalizeDigits(user.email ?: "Guest account")
        prefs.edit()
            .putBoolean("signed_in", true)
            .putBoolean("guest_mode", false)
            .putString("user_name", finalName)
            .putString("user_email", finalEmail)
            .putString("user_phone", normalizeDigits(localPhone))
            .apply()
    }

    fun markEmailLogin(email: String) {
        val cleanEmail = normalizeDigits(email.trim())
        val currentName = prefs.getString("user_name", "") ?: ""
        val finalName = if (currentName.isBlank() || currentName == "Guest User") "Smart Hotel User" else normalizeDigits(currentName)
        prefs.edit()
            .putBoolean("signed_in", true)
            .putBoolean("guest_mode", false)
            .putString("user_name", finalName)
            .putString("user_email", cleanEmail)
            .apply()
    }

    fun roomById(id: String): Room = rooms.firstOrNull { it.id == id } ?: rooms[1]

    fun selectedRoom(): Room = roomById(selectedRoomId)

    fun selectRoom(roomId: String) {
        selectedRoomId = roomId
        prefs.edit().putString("selected_room_id", roomId).apply()
    }

    fun toggleFavorite(roomId: String): Boolean {
        val added = if (favorites.contains(roomId)) {
            favorites.remove(roomId)
            false
        } else {
            favorites.add(roomId)
            true
        }
        prefs.edit().putStringSet("favorites", favorites).apply()
        return added
    }

    fun addBooking(booking: Booking) {
        bookings.add(0, booking)
        lastBookingRef = booking.ref
        saveBookings()
    }

    fun bookingByRef(ref: String?): Booking? {
        if (ref.isNullOrBlank()) return bookings.firstOrNull { it.ref == lastBookingRef }
        return bookings.firstOrNull { it.ref == ref }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        prefs.edit()
            .putString("user_name", normalizeDigits(name))
            .putString("user_email", normalizeDigits(email))
            .putString("user_phone", normalizeDigits(phone))
            .apply()
    }

    fun userName(): String = normalizeDigits(prefs.getString("user_name", "Smart Hotel User") ?: "Smart Hotel User")
    fun userEmail(): String = normalizeDigits(prefs.getString("user_email", "Guest account") ?: "Guest account")
    fun userPhone(): String = normalizeDigits(prefs.getString("user_phone", "") ?: "")
    fun firstName(): String = userName().split(" ").firstOrNull()?.ifBlank { "Smart" } ?: "Smart"

    fun bookingRef(): String = "SH-${LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd", Locale.US))}-${(1000..9999).random()}"

    fun cancelBooking(ref: String) {
        bookings.find { it.ref == ref }?.status = "Cancelled"
        saveBookings()
    }

    fun cyclePriceFilter(): String {
        currentPriceFilter = when (currentPriceFilter) {
            "Any" -> "Low"
            "Low" -> "High"
            else -> "Any"
        }
        return currentPriceFilter
    }

    fun cycleAmenityFilter(): String {
        currentAmenityFilter = when (currentAmenityFilter) {
            "All" -> "Breakfast"
            "Breakfast" -> "Family"
            else -> "All"
        }
        return currentAmenityFilter
    }

    fun cycleRatingFilter(): String {
        currentRatingFilter = when (currentRatingFilter) {
            "All" -> "4.8+"
            "4.8+" -> "4.9+"
            else -> "All"
        }
        return currentRatingFilter
    }

    private fun loadBookings(): MutableList<Booking> {
        val raw = prefs.getString("bookings", "[]") ?: "[]"
        if (raw == "[]") {
            return SampleData.sampleBookings(LocalDate.now())
        }
        val array = JSONArray(raw)
        val list = mutableListOf<Booking>()
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            list.add(
                Booking(
                    ref = item.getString("ref"),
                    roomId = item.getString("roomId"),
                    checkIn = LocalDate.parse(item.getString("checkIn")),
                    checkOut = LocalDate.parse(item.getString("checkOut")),
                    adults = item.getInt("adults"),
                    children = item.getInt("children"),
                    payment = item.getString("payment"),
                    status = item.getString("status"),
                    createdAt = LocalDate.parse(item.getString("createdAt"))
                )
            )
        }
        return list
    }

    private fun saveBookings() {
        val array = JSONArray()
        bookings.forEach {
            array.put(JSONObject().apply {
                put("ref", it.ref)
                put("roomId", it.roomId)
                put("checkIn", it.checkIn.toString())
                put("checkOut", it.checkOut.toString())
                put("adults", it.adults)
                put("children", it.children)
                put("payment", it.payment)
                put("status", it.status)
                put("createdAt", it.createdAt.toString())
            })
        }
        prefs.edit().putString("bookings", array.toString()).apply()
    }

    companion object {
        fun normalizeDigits(value: String): String {
            val builder = StringBuilder()
            value.forEach { ch ->
                if (Character.isDigit(ch)) {
                    val numeric = Character.getNumericValue(ch)
                    builder.append(if (numeric in 0..9) ('0'.code + numeric).toChar() else ch)
                } else {
                    builder.append(ch)
                }
            }
            return builder.toString()
        }
    }
}
