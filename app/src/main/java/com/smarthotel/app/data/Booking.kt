package com.smarthotel.app.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

data class Booking(
    val ref: String,
    val roomId: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val adults: Int,
    val children: Int,
    val payment: String,
    var status: String,
    val createdAt: LocalDate
) {
    fun nights(): Int = max(1, ChronoUnit.DAYS.between(checkIn, checkOut).toInt())
}
