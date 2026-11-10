package com.smarthotel.app.ui.auth

import android.app.Fragment
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import com.smarthotel.app.MainActivity
import com.smarthotel.app.R
import com.smarthotel.app.data.AppState
import com.smarthotel.app.ui.common.ScreenNavigator
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val host = activity as MainActivity
        val navigator = activity as ScreenNavigator
        host.setDarkSystemBars()

        val fullName = view.findViewById<EditText>(R.id.fullNameInput)
        val email = view.findViewById<EditText>(R.id.signUpEmailInput)
        val phone = view.findViewById<EditText>(R.id.phoneNumberInput)
        val password = view.findViewById<EditText>(R.id.signUpPasswordInput)
        val confirm = view.findViewById<EditText>(R.id.confirmPasswordInput)
        val termsCheckBox = view.findViewById<CheckBox>(R.id.termsCheckBox)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val passwordToggleButton = view.findViewById<ImageButton>(R.id.signUpPasswordToggleButton)
        val confirmPasswordToggleButton = view.findViewById<ImageButton>(R.id.confirmPasswordToggleButton)
        val createAccountButton = view.findViewById<Button>(R.id.createAccountButton)
        val loginLinkButton = view.findViewById<View>(R.id.loginLinkButton)

        backButton.setOnClickListener { navigator.showLogin() }
        passwordToggleButton.setOnClickListener { togglePasswordVisibility(password) }
        confirmPasswordToggleButton.setOnClickListener { togglePasswordVisibility(confirm) }
        createAccountButton.setOnClickListener {
            val fullNameValue = AppState.normalizeDigits(fullName.text.toString().trim())
            val emailValue = AppState.normalizeDigits(email.text.toString().trim())
            val phoneValue = AppState.normalizeDigits(phone.text.toString().trim())
            val passwordValue = AppState.normalizeDigits(password.text.toString())
            val confirmValue = AppState.normalizeDigits(confirm.text.toString())
            when {
                fullNameValue.isBlank() || emailValue.isBlank() || phoneValue.isBlank() || passwordValue.isBlank() || confirmValue.isBlank() -> host.toast("Please complete all fields")
                !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> host.toast("Please enter a valid email address")
                passwordValue != confirmValue -> host.toast("Passwords do not match")
                !termsCheckBox.isChecked -> host.toast("Please accept the terms")
                else -> {
                    host.auth().createUserWithEmailAndPassword(emailValue, passwordValue)
                        .addOnCompleteListener(host) { task ->
                            if (task.isSuccessful) {
                                host.auth().currentUser?.updateProfile(
                                    UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullNameValue)
                                        .build()
                                )
                                host.appState.updateProfile(fullNameValue, emailValue, phoneValue)
                                host.appState.syncFromFirebaseUser(host.auth().currentUser)
                                host.toast("Account created")
                                navigator.showHome()
                            } else {
                                host.toast(task.exception?.localizedMessage ?: "Unable to create account")
                            }
                        }
                }
            }
        }
        loginLinkButton.setOnClickListener { navigator.showLogin() }
    }
}
