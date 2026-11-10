package com.smarthotel.app.ui.details

import android.os.Bundle
import android.view.View
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.BaseProgrammaticFragment

class RoomDetailsFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_room_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomId = arguments?.getString(ARG_ROOM_ID) ?: appState.selectedRoomId
        appState.selectRoom(roomId)
    }

    override fun buildContent(): View {
        val room = appState.selectedRoom()
        return buildMainScreen("Smart Hotel", "Search", true, { navigator.showSearch() }) {
            addView(roomHero(room) { navigator.showRoomDetails(room.id) })
            addView(row(android.view.Gravity.CENTER_VERTICAL).apply {
                addView(text(room.name, 28, navy, true), android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(text("* ${room.rating}", 20, teal, true))
            }.apply { topMargin(dp(18)) })
            addView(text("${room.guests} guests  |  ${room.beds} bed  |  ${room.baths} bath", 15, muted))
            addView(card().apply { addView(text("Hosted by Smart Hotel", 17, navy, true)) }.apply { topMargin(dp(16)) })
            addView(text("Amenities", 22, navy, true).apply { topMargin(dp(18)) })
            addView(amenitiesGrid())
            addView(featureCard("Self check-in", "Check in anytime with the smart lock.") { navigator.showDigitalCheckIn() })
            addView(featureCard("Great check-in experience", "Based on guest feedback and ratings.") { showRatingDialog() })
            addView(text("Enjoy a comfortable and relaxing stay in our ${room.name}, featuring smart amenities, room service, and elegant design for a perfect experience.", 16, muted).apply { topMargin(dp(16)) })
            addView(card().apply {
                val r = row(android.view.Gravity.CENTER_VERTICAL)
                r.addView(text("Check-in\n2:00 PM", 17, navy, true), android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                r.addView(text("Check-out\n12:00 PM", 17, navy, true), android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(r)
            }.apply { topMargin(dp(16)) })
            addView(row(android.view.Gravity.CENTER_VERTICAL).apply {
                addView(text("EGP ${money(room.price)} / night", 26, teal, true), android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(actionButton("Book Now") { navigator.showConfirmBooking() }, android.widget.LinearLayout.LayoutParams(dp(180), dp(54)))
            }.apply { topMargin(dp(18)) })
        }
    }

    companion object {
        private const val ARG_ROOM_ID = "room_id"

        fun newInstance(roomId: String): RoomDetailsFragment {
            return RoomDetailsFragment().apply {
                arguments = Bundle().apply { putString(ARG_ROOM_ID, roomId) }
            }
        }
    }
}
