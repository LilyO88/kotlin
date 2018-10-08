package es.iessaladillo.pedrojoya.pr007.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.iessaladillo.pedrojoya.pr007.R
import es.iessaladillo.pedrojoya.pr007.extensions.labelTextView
import es.iessaladillo.pedrojoya.pr007.extensions.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val isValid: Boolean
        get() = txtUsername.text.isNotBlank() && txtPassword.text.isNotBlank()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
    }

    private fun setupViews() {
        btnAccept.setOnClickListener { signIn() }
        btnCancel.setOnClickListener { resetViews() }
        txtUsername.labelTextView(lblUsername, afterTextChange = { checkIsValidForm() })
        txtPassword.labelTextView(lblPassword, afterTextChange = { checkIsValidForm() })
    }

    private fun signIn() {
        toast(getString(R.string.main_signing_in, txtUsername.text))
    }

    private fun resetViews() {
        txtUsername.setText("")
        txtPassword.setText("")
    }

    private fun checkIsValidForm() {
        btnAccept.isEnabled = isValid
    }

}
