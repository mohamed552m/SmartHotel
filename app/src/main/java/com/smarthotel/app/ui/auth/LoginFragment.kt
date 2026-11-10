package com.smarthotel.app.ui.auth

import android.app.Fragment
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.smarthotel.app.MainActivity
import com.smarthotel.app.R
import com.smarthotel.app.data.AppState
import com.smarthotel.app.ui.common.ScreenNavigator

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val host = activity as MainActivity
        val navigator = activity as ScreenNavigator
        host.setDarkSystemBars()
        val email = view.findViewById<EditText>(R.id.emailInput)
        val password = view.findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = view.findViewById<ImageButton>(R.id.passwordToggleButton)
        val forgotPassword = view.findViewById<TextView>(R.id.forgotPasswordButton)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val continueAsGuestButton = view.findViewById<Button>(R.id.continueAsGuestButton)
        val signUpButton = view.findViewById<TextView>(R.id.signUpButton)

        passwordToggle.setOnClickListener { togglePasswordVisibility(password) }
        forgotPassword.setOnClickListener { host.showForgotPasswordDialog() }
        loginButton.setOnClickListener {
            val emailValue = AppState.normalizeDigits(email.text.toString().trim())
            val passwordValue = AppState.normalizeDigits(password.text.toString())
            when {
                emailValue.isBlank() || passwordValue.isBlank() -> host.toast("Please enter email and password")
                !emailValue.contains("@") -> host.toast("Please login with email for this version")
                !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> host.toast("Please enter a valid email address")
                else -> {
                    host.auth().signInWithEmailAndPassword(emailValue, passwordValue)
                        .addOnCompleteListener(host) { task ->
                            if (task.isSuccessful) {
                                host.appState.markEmailLogin(emailValue)
                                host.appState.syncFromFirebaseUser(host.auth().currentUser)
                                navigator.showHome()
                            } else {
                                host.toast(task.exception?.localizedMessage ?: "Login failed")
                            }
                        }
                }
            }
        }
        continueAsGuestButton.setOnClickListener {
            host.appState.setGuestSession()
            host.toast("Continuing as guest")
            navigator.showHome()
        }
        signUpButton.setOnClickListener { navigator.showSignUp() }
    }
}
