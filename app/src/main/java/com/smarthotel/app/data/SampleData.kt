package com.smarthotel.app.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object SampleData {
    val rooms = listOf(
        Room("standard", "Standard Room", "Standard", "room_standard", 650, 2, 1, 1, 4.6, "Free cancellation", "#D9ECF2", "#F7FAFC"),
        Room("deluxe", "Deluxe Smart Room", "Deluxe", "room_deluxe", 850, 2, 1, 1, 4.9, "Free cancellation", "#B98D55", "#F5E7D2"),
        Room("luxury", "Luxury Suite", "Suite", "room_luxury_suite", 1200, 3, 2, 1, 4.8, "Breakfast included", "#C7A46C", "#FFF4DE"),
        Room("family", "Family Suite", "Suite", "room_family_suite", 1450, 4, 2, 2, 4.7, "Best for families", "#A4C3D9", "#F7E8D6"),
        Room("vip", "VIP Suite", "Suite", "room_vip_suite", 1800, 4, 2, 2, 5.0, "Private lounge access", "#06376B", "#0FA3B1")
    )

    fun sampleBookings(today: LocalDate): MutableList<Booking> {
        val refFormatter = DateTimeFormatter.ofPattern("yyMMdd", Locale.US)
        return mutableListOf(
            Booking(
                ref = "SH-${today.format(refFormatter)}-2401",
                roomId = "deluxe",
                checkIn = today.plusDays(2),
                checkOut = today.plusDays(5),
                adults = 2,
                children = 0,
                payment = "Credit / Debit Card",
                status = "Confirmed",
                createdAt = today
            ),
            Booking(
                ref = "SH-${today.minusDays(7).format(refFormatter)}-1874",
                roomId = "standard",
                checkIn = today.minusDays(12),
                checkOut = today.minusDays(10),
                adults = 2,
                children = 0,
                payment = "Cash at Hotel",
                status = "Completed",
                createdAt = today.minusDays(15)
            ),
            Booking(
                ref = "SH-${today.minusDays(14).format(refFormatter)}-0922",
                roomId = "luxury",
                checkIn = today.minusDays(18),
                checkOut = today.minusDays(15),
                adults = 3,
                children = 0,
                payment = "Apple Pay",
                status = "Cancelled",
                createdAt = today.minusDays(20)
            )
        )
    }
}
