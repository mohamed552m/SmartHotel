package com.smarthotel.app.ui.welcome

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smarthotel.app.MainActivity
import com.smarthotel.app.R
import com.smarthotel.app.ui.common.ScreenNavigator

class WelcomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val host = activity as MainActivity
        val navigator = activity as ScreenNavigator
        host.setDarkSystemBars()
        view.findViewById<View>(R.id.getStartedButton).setOnClickListener {
            when {
                host.auth().currentUser != null -> navigator.showHome()
                host.appState.isSignedIn() && host.appState.isGuestSession -> navigator.showHome()
                else -> navigator.showLogin()
            }
        }
    }
}
