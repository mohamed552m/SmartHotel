package com.smarthotel.app.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.smarthotel.app.R
import com.smarthotel.app.data.Room
import com.smarthotel.app.ui.common.BaseProgrammaticFragment
import java.time.LocalTime

class HomeFragment : BaseProgrammaticFragment() {
    override val layoutResId: Int = R.layout.fragment_home

    private val favoriteFilled = "\u2665"
    private val favoriteOutline = "\u2661"

    override fun buildContent(): View {
        host.setLightSystemBars()
        host.showBottomNav("Home")
        return LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(surface)
            addView(buildHeroHeader())
            addView(android.widget.ScrollView(activity).apply {
                overScrollMode = View.OVER_SCROLL_NEVER
                setBackgroundColor(surface)
                addView(LinearLayout(activity).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(18), dp(18), dp(18), dp(28))
                    addView(chipRow(listOf("Any type", "Standard", "Deluxe", "Suite"), appState.currentCategory) {
                        appState.currentCategory = it
                        navigator.showHome()
                    })
                    filteredHomeRooms().forEach { room ->
                        addView(homeRoomCard(room).apply { topMargin(dp(18)) })
                    }
                })
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }
    }

    private fun buildHeroHeader() = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(242))
        background = headerBackground()
        setPadding(dp(22), dp(22), dp(22), dp(24))

        addView(row(Gravity.CENTER_VERTICAL).apply {
            addView(LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(logoSeal(), LinearLayout.LayoutParams(dp(54), dp(54)))
                addView(LinearLayout(activity).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(text("SMART HOTEL", 18, Color.rgb(237, 199, 113), true).apply {
                        letterSpacing = 0.12f
                        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    })
                    addView(text("YOUR COMFORT, OUR PRIORITY", 10, Color.rgb(224, 178, 88), true).apply {
                        letterSpacing = 0.08f
                        topMargin(dp(2))
                    })
                }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    marginStart = dp(12)
                })
            }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(profileBadge(), LinearLayout.LayoutParams(dp(52), dp(52)))
        })

        addView(text("${greeting()}, ${displayGuestName()}", 27, Color.WHITE, true).apply {
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            topMargin(dp(28))
        })
        addView(text("Discover a stay tailored to your comfort.", 16, Color.rgb(194, 205, 224)).apply {
            topMargin(dp(10))
        })
    }

    private fun homeRoomCard(room: Room) = card().apply {
        setPadding(0, 0, 0, 0)

        addView(FrameLayout(activity).apply {
            background = rounded(Color.WHITE, dp(26))
            clipToOutline = true
            addView(roomImage(room), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            addView(smallButton(if (appState.favorites.contains(room.id)) favoriteFilled else favoriteOutline) {
                val added = appState.toggleFavorite(room.id)
                toast(if (added) "Added to Favorites" else "Removed from Favorites")
                navigator.showHome()
            }, FrameLayout.LayoutParams(dp(52), dp(52), Gravity.TOP or Gravity.END).apply {
                topMargin = dp(16)
                rightMargin = dp(16)
            })
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210)))

        addView(LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            addView(text(room.name, 23, navy, true).apply {
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            })
            addView(text(homeSubtitle(room), 16, muted).apply { topMargin(dp(10)) })
            addView(text("Free Wi-Fi   |   A/C   |   Smart TV   |   Tea Maker", 15, muted).apply { topMargin(dp(16)) })
            addView(row(Gravity.CENTER_VERTICAL).apply {
                addView(LinearLayout(activity).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(text("From", 14, muted))
                    addView(text("EGP ${money(room.price)}", 22, teal, true).apply { topMargin(dp(4)) })
                }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(actionButton("View Details") { navigator.showRoomDetails(room.id) }, LinearLayout.LayoutParams(dp(168), dp(54)))
            }.apply { topMargin(dp(20)) })
        })
    }

    private fun filteredHomeRooms(): List<Room> {
        return appState.rooms.filter { appState.currentCategory == "Any type" || it.category == appState.currentCategory }
    }

    private fun homeSubtitle(room: Room): String {
        return when (room.id) {
            "deluxe" -> "Stylish comfort with modern amenities."
            "standard" -> "Essential comfort for a relaxing stay."
            "luxury" -> "Spacious elegance with exclusive touches."
            "family" -> "Designed for family comfort and calm."
            else -> "An elevated stay with refined luxury."
        }
    }

    private fun logoSeal() = FrameLayout(activity).apply {
        background = rounded(Color.TRANSPARENT, dp(27), Color.rgb(226, 187, 101))
        addView(text("H", 26, Color.rgb(244, 214, 144), true).apply {
            gravity = Gravity.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun profileBadge() = FrameLayout(activity).apply {
        background = rounded(Color.WHITE, dp(26), Color.rgb(226, 187, 101))
        addView(text(displayGuestName().take(1), 22, navy, true).apply {
            gravity = Gravity.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun headerBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#041B42"))
            cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, dp(40).toFloat(), dp(40).toFloat(), dp(40).toFloat(), dp(40).toFloat())
        }
    }

    private fun greeting(): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun displayGuestName(): String {
        return if (appState.isGuestSession) "Guest" else appState.firstName()
    }
}
