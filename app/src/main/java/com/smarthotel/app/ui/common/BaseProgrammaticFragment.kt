package com.smarthotel.app.ui.common

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import com.smarthotel.app.MainActivity
import com.smarthotel.app.R
import com.smarthotel.app.data.Booking
import com.smarthotel.app.data.Room
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.max

abstract class BaseProgrammaticFragment : Fragment() {
    private val heartFilled = "\u2665"
    private val heartOutline = "\u2661"

    protected open val layoutResId: Int = R.layout.fragment_home
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US)
    private val compactFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)

    protected val navigator: ScreenNavigator
        get() = activity as ScreenNavigator

    protected val host: MainActivity
        get() = activity as MainActivity

    protected val appState
        get() = host.appState

    protected val navy = Color.rgb(5, 33, 74)
    protected val teal = Color.rgb(7, 149, 168)
    protected val surface = Color.rgb(246, 250, 253)
    protected val muted = Color.rgb(100, 116, 139)
    protected val lightLine = Color.rgb(225, 232, 240)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val root = view.findViewById<FrameLayout>(R.id.fragmentRoot)
        root.removeAllViews()
        root.addView(buildContent(), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    protected abstract fun buildContent(): View

    protected fun buildMainScreen(
        title: String,
        activeNav: String,
        back: Boolean,
        onBack: () -> Unit,
        body: LinearLayout.() -> Unit
    ): View {
        host.setLightSystemBars()
        val root = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(28), dp(20), dp(16))
            setBackgroundColor(surface)
        }
        val header = row(Gravity.CENTER_VERTICAL)
        header.addView(if (back) smallButton("<") { onBack() } else logoMark(), LinearLayout.LayoutParams(dp(48), dp(48)))
        header.addView(text(title, 28, navy, true).apply { gravity = Gravity.CENTER }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        header.addView(smallButton(if (title == "My Account") "!" else "A") {
            if (title == "My Account") showNotificationsDialog() else navigator.showAccount()
        }, LinearLayout.LayoutParams(dp(48), dp(48)))
        root.addView(header)

        val scroll = ScrollView(activity).apply { overScrollMode = View.OVER_SCROLL_NEVER }
        val content = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(20), 0, dp(24))
            body()
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        host.showBottomNav(activeNav)
        return root
    }

    protected fun searchSummaryCard(onClick: () -> Unit) = card().apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        setOnClickListener { onClick() }
        addView(text("Smart Hotel\nChoose your room", 16, navy, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(text("${appState.checkIn.format(compactFormatter)} - ${appState.checkOut.format(compactFormatter)}\n${nights()} nights", 15, muted), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(text("${appState.adultGuests + appState.childGuests} Guests\n1 room", 15, muted), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(smallButton("Go") { onClick() }, LinearLayout.LayoutParams(dp(54), dp(54)))
    }

    protected fun searchBox(initialText: String, onSearch: (String) -> Unit) = card().apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(12), dp(10), dp(10), dp(10))
        val input = EditText(activity).apply {
            hint = "Search Smart Hotel rooms"
            setSingleLine(true)
            setText(initialText)
            background = null
            setTextColor(navy)
            setHintTextColor(muted)
            textSize = 15f
            setPadding(dp(8), 0, dp(8), 0)
        }
        addView(input, LinearLayout.LayoutParams(0, dp(52), 1f))
        addView(actionButton("Go") { onSearch(input.text.toString()) }, LinearLayout.LayoutParams(dp(74), dp(52)))
    }

    protected fun filterRow(refresh: () -> Unit) = LinearLayout(activity).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        addView(filterChip("Price", appState.currentPriceFilter != "Any", appState.currentPriceFilter) {
            toast("Price filter: ${appState.cyclePriceFilter()}")
            refresh()
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
        addView(space(8, 0))
        addView(filterChip("Amenities", appState.currentAmenityFilter != "All", appState.currentAmenityFilter) {
            toast("Amenity filter: ${appState.cycleAmenityFilter()}")
            refresh()
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
        addView(space(8, 0))
        addView(filterChip("Rating", appState.currentRatingFilter != "All", appState.currentRatingFilter) {
            toast("Rating filter: ${appState.cycleRatingFilter()}")
            refresh()
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
    }

    protected fun roomListCard(room: Room, refresh: () -> Unit) = card().apply {
        orientation = LinearLayout.HORIZONTAL
        isClickable = true
        setOnClickListener { navigator.showRoomDetails(room.id) }
        addView(roomImage(room), LinearLayout.LayoutParams(dp(128), dp(132)))
        val details = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
            addView(row(Gravity.CENTER_VERTICAL).apply {
                addView(text(room.name, 18, navy, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(text("* ${room.rating}", 15, teal, true))
            })
            addView(text("${room.guests} guests  |  ${room.beds} bed  |  ${room.baths} bath", 14, muted).apply { topMargin(dp(6)) })
            addView(text(room.tag, 14, teal).apply { topMargin(dp(10)) })
            addView(row(Gravity.CENTER_VERTICAL).apply {
                addView(text("EGP ${money(room.price)} / night", 18, teal, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(smallButton(if (appState.favorites.contains(room.id)) heartFilled else heartOutline) {
                    val added = appState.toggleFavorite(room.id)
                    toast(if (added) "Added to Favorites" else "Removed from Favorites")
                    refresh()
                }, LinearLayout.LayoutParams(dp(48), dp(48)))
            }.apply { topMargin(dp(8)) })
        }
        addView(details, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    }

    protected fun horizontalRooms(items: List<Room>, compact: Boolean = false, refresh: () -> Unit) = LinearLayout(activity).apply {
        orientation = LinearLayout.HORIZONTAL
        items.forEach { room ->
            addView(card().apply {
                isClickable = true
                setOnClickListener { navigator.showRoomDetails(room.id) }
                val imageWrap = FrameLayout(activity).apply {
                    addView(roomImage(room), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    addView(smallButton(if (appState.favorites.contains(room.id)) heartFilled else heartOutline) {
                        val added = appState.toggleFavorite(room.id)
                        toast(if (added) "Added to Favorites" else "Removed from Favorites")
                        refresh()
                    }, FrameLayout.LayoutParams(dp(42), dp(42), Gravity.TOP or Gravity.END).apply {
                        topMargin = dp(8)
                        rightMargin = dp(8)
                    })
                }
                addView(imageWrap, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(if (compact) 76 else 120)))
                addView(text(room.name, if (compact) 15 else 18, navy, true).apply { topMargin(dp(10)) })
                addView(text("From EGP ${money(room.price)}", 14, teal, true).apply { topMargin(dp(4)) })
            }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = dp(10)
            })
        }
    }

    protected fun roomHero(room: Room, refresh: () -> Unit) = FrameLayout(activity).apply {
        background = rounded(Color.WHITE, dp(22))
        clipToOutline = true
        addView(ImageView(activity).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(roomImageRes(room))
        }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        addView(smallButton(if (appState.favorites.contains(room.id)) heartFilled else heartOutline) {
            val added = appState.toggleFavorite(room.id)
            toast(if (added) "Added to Favorites" else "Removed from Favorites")
            refresh()
        }, FrameLayout.LayoutParams(dp(56), dp(56), Gravity.TOP or Gravity.END).apply {
            topMargin = dp(14)
            rightMargin = dp(14)
        })
        addView(smallButton("Share") { shareRoom(room) }, FrameLayout.LayoutParams(dp(74), dp(44), Gravity.BOTTOM or Gravity.END).apply {
            bottomMargin = dp(14)
            rightMargin = dp(14)
        })
    }.apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(260))
    }

    protected fun summaryRoomCard(room: Room) = card().apply {
        orientation = LinearLayout.HORIZONTAL
        addView(roomImage(room), LinearLayout.LayoutParams(dp(126), dp(112)))
        addView(LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
            addView(text(room.name, 22, navy, true))
            addView(text("${room.guests} Guests  |  ${room.beds} Bed  |  ${room.baths} Bath", 14, muted).apply { topMargin(dp(8)) })
            addView(text("* ${room.rating}", 15, teal, true).apply { topMargin(dp(8)) })
            addView(text("EGP ${money(room.price)} / night", 18, teal, true).apply { topMargin(dp(8)) })
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    }

    protected fun bookingDetailsCard(booking: Booking, room: Room) = sectionCard("Booking Details").apply {
        addView(summaryRoomCard(room))
        addView(text("${booking.checkIn.format(displayFormatter)} - ${booking.checkOut.format(displayFormatter)}  |  ${booking.nights()} nights", 15, muted).apply { topMargin(dp(10)) })
        addView(text("${booking.adults + booking.children} guests  |  ${booking.status}  |  ${booking.ref}", 15, teal, true).apply { topMargin(dp(6)) })
    }

    protected fun bookingCard(booking: Booking, refresh: () -> Unit) = card().apply {
        val room = appState.roomById(booking.roomId)
        addView(row(Gravity.CENTER_VERTICAL).apply {
            addView(text(booking.status, 14, if (booking.status == "Cancelled") Color.rgb(190, 40, 40) else teal, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(text(booking.ref, 14, teal, true))
        })
        addView(bookingDetailsCard(booking, room).apply { topMargin(dp(10)) })
        addView(row(Gravity.CENTER).apply {
            addView(outlineButton("View Details") { navigator.showBookingDetails(booking.ref) }, LinearLayout.LayoutParams(0, dp(52), 1f))
            addView(space(10, 0))
            addView(actionButton("Manage Booking") { manageBookingDialog(booking, refresh) }, LinearLayout.LayoutParams(0, dp(52), 1f))
        }.apply { topMargin(dp(12)) })
    }

    protected fun dateSummary() = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        addView(row(Gravity.CENTER).apply {
            addView(text("Check-in\n${appState.checkIn.format(displayFormatter)}\n2:00 PM", 16, navy, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(text("Check-out\n${appState.checkOut.format(displayFormatter)}\n12:00 PM", 16, navy, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        })
        addView(text("${nights()} Nights", 18, teal, true).apply {
            gravity = Gravity.CENTER
            topMargin(dp(14))
        })
    }

    protected fun priceDetails(room: Room, booking: Booking? = null) = sectionCard("Price Details").apply {
        val nightCount = booking?.nights() ?: nights()
        val subtotal = room.price * nightCount
        val taxes = taxesFor(nightCount)
        addView(priceLine("EGP ${money(room.price)} x $nightCount nights", subtotal))
        addView(priceLine("Taxes & fees", taxes))
        addView(View(activity).apply { setBackgroundColor(lightLine) }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)).apply {
            topMargin = dp(12)
            bottomMargin = dp(12)
        })
        addView(priceLine(if (booking == null) "Total" else "Total Paid", subtotal + taxes, true))
    }

    protected fun paymentMethodRow(label: String, refresh: () -> Unit) = row(Gravity.CENTER_VERTICAL).apply {
        background = rounded(Color.WHITE, dp(12), if (appState.selectedPaymentMethod == label) teal else lightLine)
        setPadding(dp(12), dp(10), dp(12), dp(10))
        isClickable = true
        setOnClickListener {
            appState.selectedPaymentMethod = label
            refresh()
        }
        addView(text(if (appState.selectedPaymentMethod == label) "(o)" else "( )", 18, teal, true))
        addView(text(label, 16, navy, true).apply { marginStart(dp(12)) }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(text(">", 18, muted, true))
        topMargin(dp(10))
    }

    protected fun accountItem(title: String, subtitle: String, danger: Boolean = false, onClick: () -> Unit) = card().apply {
        isClickable = true
        setOnClickListener { onClick() }
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(text(if (danger) "!" else ">", 22, if (danger) Color.rgb(180, 28, 28) else teal, true), LinearLayout.LayoutParams(dp(34), ViewGroup.LayoutParams.WRAP_CONTENT))
        addView(LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            addView(text(title, 18, if (danger) Color.rgb(180, 28, 28) else navy, true))
            addView(text(subtitle, 14, muted).apply { topMargin(dp(4)) })
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(text(">", 22, muted, true))
        topMargin(dp(12))
    }

    protected fun sectionCard(title: String, action: String? = null, onAction: (() -> Unit)? = null) = card().apply {
        addView(row(Gravity.CENTER_VERTICAL).apply {
            addView(text(title, 20, navy, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            if (action != null && onAction != null) addView(link(action) { onAction() })
        })
    }

    protected fun featureCard(title: String, subtitle: String, onClick: () -> Unit) = card().apply {
        isClickable = true
        setOnClickListener { onClick() }
        addView(row(Gravity.CENTER_VERTICAL).apply {
            addView(LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                addView(text(title, 18, navy, true))
                addView(text(subtitle, 14, muted).apply { topMargin(dp(4)) })
            }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(text(">", 24, navy, true))
        })
        topMargin(dp(12))
    }

    protected fun stepper(step: Int) = row(Gravity.CENTER).apply {
        listOf("Details", "Payment", "Confirmed").forEachIndexed { index, label ->
            val active = index + 1 == step
            addView(text("${index + 1} $label", 15, if (active) Color.WHITE else teal, true).apply {
                gravity = Gravity.CENTER
                background = rounded(if (active) navy else Color.TRANSPARENT, dp(18), teal)
                setPadding(dp(10), dp(6), dp(10), dp(6))
            }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        }
    }

    protected fun chipRow(items: List<String>, active: String, onPick: (String) -> Unit) = LinearLayout(activity).apply {
        orientation = LinearLayout.HORIZONTAL
        items.forEachIndexed { index, item ->
            addView(TextView(activity).apply {
                text = item
                gravity = Gravity.CENTER
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(if (item == active) Color.WHITE else navy)
                background = rounded(if (item == active) teal else Color.WHITE, dp(22), if (item == active) teal else lightLine)
                isClickable = true
                setOnClickListener { onPick(item) }
            }, LinearLayout.LayoutParams(0, dp(46), 1f).apply {
                if (index < items.lastIndex) marginEnd = dp(8)
            })
        }
    }

    protected fun LinearLayout.addSectionHeader(title: String, action: String, onClick: () -> Unit) {
        addView(row(Gravity.CENTER_VERTICAL).apply {
            addView(text(title, 22, navy, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(link(action) { onClick() })
            topMargin(dp(22))
        })
    }

    protected fun emptyState(title: String, subtitle: String) = card().apply {
        gravity = Gravity.CENTER_HORIZONTAL
        addView(text(title, 22, navy, true).apply { gravity = Gravity.CENTER })
        addView(text(subtitle, 15, muted).apply { gravity = Gravity.CENTER; topMargin(dp(8)) })
    }

    protected fun amenitiesGrid() = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        listOf(
            listOf("Wi-Fi", "Smart TV", "Mini Bar"),
            listOf("Air Conditioning", "Room Service", "Digital Check-in")
        ).forEach { line ->
            addView(row(Gravity.CENTER).apply {
                line.forEach { label ->
                    addView(outlineButton(label) {
                        if (label == "Digital Check-in") navigator.showDigitalCheckIn() else toast("$label available")
                    }, LinearLayout.LayoutParams(0, dp(46), 1f).apply { marginEnd = dp(8) })
                }
            }.apply { topMargin(dp(10)) })
        }
    }

    protected fun filteredRooms(): List<Room> {
        val filtered = appState.rooms.filter {
            (appState.currentCategory == "Any type" || it.category == appState.currentCategory) &&
                (appState.currentQuery.isBlank() || it.name.contains(appState.currentQuery, ignoreCase = true)) &&
                matchesAmenityFilter(it) &&
                matchesRatingFilter(it)
        }
        return when (appState.currentPriceFilter) {
            "Low" -> filtered.sortedBy { it.price }
            "High" -> filtered.sortedByDescending { it.price }
            else -> filtered
        }
    }

    protected fun roomImage(room: Room) = FrameLayout(activity).apply {
        background = rounded(Color.WHITE, dp(14))
        clipToOutline = true
        addView(ImageView(activity).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(roomImageRes(room))
        }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    protected fun priceLine(label: String, value: Int, strong: Boolean = false) = row(Gravity.CENTER_VERTICAL).apply {
        addView(text(label, if (strong) 18 else 15, navy, strong), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(text("EGP ${money(value)}", if (strong) 22 else 15, if (strong) teal else navy, true))
        topMargin(dp(8))
    }

    protected fun card() = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = rounded(Color.WHITE, dp(18), lightLine)
        elevation = dp(2).toFloat()
    }

    protected fun row(gravityValue: Int = Gravity.CENTER_VERTICAL) = LinearLayout(activity).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = gravityValue
    }

    protected fun text(value: String, size: Int, color: Int, bold: Boolean = false) = TextView(activity).apply {
        text = value
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    protected fun actionButton(label: String, onClick: () -> Unit) = TextView(activity).apply {
        text = label
        gravity = Gravity.CENTER
        textSize = 16f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(Color.WHITE)
        background = gradient("#0795A8", "#05214A", dp(18))
        isClickable = true
        setOnClickListener { onClick() }
        setPadding(dp(14), 0, dp(14), 0)
        minHeight = dp(54)
    }

    protected fun outlineButton(label: String, onClick: () -> Unit) = TextView(activity).apply {
        text = label
        gravity = Gravity.CENTER
        textSize = 15f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(navy)
        background = rounded(Color.WHITE, dp(15), lightLine)
        isClickable = true
        setOnClickListener { onClick() }
        setPadding(dp(12), 0, dp(12), 0)
        minHeight = dp(46)
    }

    protected fun smallButton(label: String, onClick: () -> Unit) = TextView(activity).apply {
        text = label
        gravity = Gravity.CENTER
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(if (label == heartFilled) Color.rgb(190, 40, 80) else navy)
        background = rounded(Color.WHITE, dp(24), lightLine)
        isClickable = true
        setOnClickListener { onClick() }
        elevation = dp(2).toFloat()
    }

    protected fun link(label: String, onClick: () -> Unit) = TextView(activity).apply {
        text = label
        textSize = 15f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(teal)
        isClickable = true
        setOnClickListener { onClick() }
    }

    protected fun logoMark() = TextView(activity).apply {
        text = "H"
        gravity = Gravity.CENTER
        textSize = 22f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        setTextColor(Color.WHITE)
        background = rounded(navy, dp(24))
    }

    protected fun showForgotPasswordDialog() {
        val email = EditText(activity).apply {
            hint = "Email or phone number"
            setSingleLine(true)
        }
        AlertDialog.Builder(activity)
            .setTitle("Reset Password")
            .setMessage("Enter your Smart Hotel account email or phone.")
            .setView(email)
            .setPositiveButton("Send Reset") { _, _ -> toast("Password reset instructions sent") }
            .setNegativeButton("Cancel", null)
            .show()
    }

    protected fun showCheckoutPicker(after: () -> Unit) {
        val start = appState.checkOut
        val dialog = DatePickerDialog(activity, { _, year, month, day ->
            val picked = LocalDate.of(year, month + 1, day)
            appState.checkIn = LocalDate.now()
            appState.checkOut = if (picked.isAfter(appState.checkIn)) picked else appState.checkIn.plusDays(1)
            after()
        }, start.year, start.monthValue - 1, start.dayOfMonth)
        dialog.datePicker.minDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        dialog.show()
    }

    protected fun showGuestSelector(after: () -> Unit) {
        val container = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), 0, dp(18), 0)
        }
        val adults = NumberPicker(activity).apply {
            minValue = 1
            maxValue = 6
            value = appState.adultGuests
            setFormatter { String.format(Locale.US, "%d", it) }
        }
        val children = NumberPicker(activity).apply {
            minValue = 0
            maxValue = 4
            value = appState.childGuests
            setFormatter { String.format(Locale.US, "%d", it) }
        }
        container.addView(text("Adults", 16, navy, true))
        container.addView(adults)
        container.addView(text("Children", 16, navy, true))
        container.addView(children)
        AlertDialog.Builder(activity)
            .setTitle("Guests")
            .setView(container)
            .setPositiveButton("Apply") { _, _ ->
                appState.adultGuests = adults.value
                appState.childGuests = children.value
                after()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    protected fun manageBookingDialog(booking: Booking, refresh: () -> Unit) {
        val options = if (booking.status == "Confirmed") {
            arrayOf("View Details", "Digital Check-in", "Cancel Booking", "Share Booking")
        } else {
            arrayOf("View Details", "Share Booking")
        }
        AlertDialog.Builder(activity)
            .setTitle("Manage Booking")
            .setItems(options) { _, which ->
                when (options[which]) {
                    "View Details" -> navigator.showBookingDetails(booking.ref)
                    "Digital Check-in" -> navigator.showDigitalCheckIn(booking.ref)
                    "Cancel Booking" -> {
                        appState.cancelBooking(booking.ref)
                        toast("Booking cancelled")
                        refresh()
                    }
                    "Share Booking" -> shareBooking(booking)
                }
            }
            .show()
    }

    protected fun showEditProfileDialog(after: () -> Unit) {
        val name = EditText(activity).apply { hint = "Full name"; setText(appState.userName()) }
        val email = EditText(activity).apply { hint = "Email"; setText(appState.userEmail()) }
        val phone = EditText(activity).apply { hint = "Phone"; setText(appState.userPhone()) }
        val box = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), 0, dp(18), 0)
            addView(name)
            addView(email)
            addView(phone)
        }
        AlertDialog.Builder(activity)
            .setTitle("Edit Profile")
            .setView(box)
            .setPositiveButton("Save") { _, _ ->
                appState.updateProfile(name.text.toString(), email.text.toString(), phone.text.toString())
                toast("Profile updated")
                after()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    protected fun showPaymentMethodsDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Payment Methods")
            .setItems(arrayOf("Credit / Debit Card", "Meeza Card", "Apple Pay", "Cash at Hotel")) { _, which ->
                appState.selectedPaymentMethod = arrayOf("Credit / Debit Card", "Meeza Card", "Apple Pay", "Cash at Hotel")[which]
                toast("${appState.selectedPaymentMethod} selected")
            }
            .show()
    }

    protected fun showNotificationsDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Notifications")
            .setMultiChoiceItems(
                arrayOf("Booking updates", "Payment reminders", "Smart check-in alerts"),
                booleanArrayOf(true, true, false)
            ) { _, _, _ -> }
            .setPositiveButton("Save") { _, _ -> toast("Notification preferences saved") }
            .setNegativeButton("Cancel", null)
            .show()
    }

    protected fun showRatingDialog() {
        val picker = NumberPicker(activity).apply {
            minValue = 1
            maxValue = 5
            value = 5
            setFormatter { String.format(Locale.US, "%d", it) }
        }
        AlertDialog.Builder(activity)
            .setTitle("Rating / Feedback")
            .setMessage("How was your Smart Hotel experience?")
            .setView(picker)
            .setPositiveButton("Submit") { _, _ -> toast("Thanks for your ${picker.value}-star feedback") }
            .setNegativeButton("Cancel", null)
            .show()
    }

    protected fun info(title: String, message: String) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    protected fun shareRoom(room: Room) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out ${room.name} at Smart Hotel for EGP ${money(room.price)} per night.")
        }
        startActivity(Intent.createChooser(intent, "Share Room"))
    }

    protected fun shareBooking(booking: Booking) {
        val room = appState.roomById(booking.roomId)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Smart Hotel booking ${booking.ref}: ${room.name}, ${booking.checkIn.format(displayFormatter)} to ${booking.checkOut.format(displayFormatter)}.")
        }
        startActivity(Intent.createChooser(intent, "Share Booking"))
    }

    protected fun nights(): Int = max(1, ChronoUnit.DAYS.between(appState.checkIn, appState.checkOut).toInt())
    protected fun taxesFor(nightCount: Int): Int = nightCount * 100
    protected fun totalPrice(room: Room): Int = room.price * nights() + taxesFor(nights())
    protected fun money(value: Int): String = String.format(Locale.US, "%,d", value)
    protected fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    protected fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    protected fun space(width: Int, height: Int) = Space(activity).apply { layoutParams = LinearLayout.LayoutParams(dp(width), dp(height)) }
    protected fun roomImageRes(room: Room): Int = resources.getIdentifier(room.imageAssetName, "drawable", activity.packageName)

    protected fun rounded(color: Int, radius: Int, strokeColor: Int? = null): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
            if (strokeColor != null) setStroke(dp(1), strokeColor)
        }
    }

    protected fun gradient(start: String, end: String, radius: Int = 0, strokeColor: Int? = null): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(Color.parseColor(start), Color.parseColor(end))).apply {
            cornerRadius = radius.toFloat()
            if (strokeColor != null) setStroke(dp(1), strokeColor)
        }
    }

    protected fun View.topMargin(value: Int) {
        val lp = (layoutParams as? ViewGroup.MarginLayoutParams)
            ?: LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.topMargin = value
        layoutParams = lp
    }

    protected fun View.marginStart(value: Int) {
        val lp = (layoutParams as? ViewGroup.MarginLayoutParams)
            ?: LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.marginStart = value
        layoutParams = lp
    }

    private fun filterChip(title: String, active: Boolean, value: String, onClick: () -> Unit) = TextView(activity).apply {
        text = if (active) "$title: $value" else title
        gravity = Gravity.CENTER
        textSize = 13f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(if (active) Color.WHITE else navy)
        background = rounded(if (active) teal else Color.WHITE, dp(16), if (active) teal else lightLine)
        isClickable = true
        setOnClickListener { onClick() }
        setPadding(dp(8), 0, dp(8), 0)
    }

    private fun matchesAmenityFilter(room: Room): Boolean {
        return when (appState.currentAmenityFilter) {
            "Breakfast" -> room.tag.contains("Breakfast", ignoreCase = true)
            "Family" -> room.name.contains("Family", ignoreCase = true) || room.tag.contains("famil", ignoreCase = true)
            else -> true
        }
    }

    private fun matchesRatingFilter(room: Room): Boolean {
        return when (appState.currentRatingFilter) {
            "4.8+" -> room.rating >= 4.8
            "4.9+" -> room.rating >= 4.9
            else -> true
        }
    }
}
