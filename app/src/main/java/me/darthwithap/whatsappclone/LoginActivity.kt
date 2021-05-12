package me.darthwithap.whatsappclone

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : BaseActivity(), NumVerifyClient.VolleyCallback {
    companion object {
        private const val PHONE_NUMBER = "phoneNumber"
        private const val CREDENTIAL_PICKER_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_login)

        etPhoneNumber.addTextChangedListener {
            btnContinue.isEnabled = !it.isNullOrBlank() && it.length == 10
        }

        Handler(Looper.getMainLooper()).postDelayed({
            phoneSelection()
        }, 700)

        ccp.registerPhoneNumberTextView(etPhoneNumber)

        btnContinue.setOnClickListener {
            if (ccp.number != null) {
                pbCheckingPhoneNumber.visibility = View.VISIBLE
                checkPhoneValidity(ccp.number)
            }
            else Toast.makeText(this, "Enter a number to continue", Toast.LENGTH_SHORT).show()
        }
    }

    private fun phoneSelection() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        val credentialsClient = Credentials.getClient(this, options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,
                Bundle()
            )
        } catch (ise: IntentSender.SendIntentException) {
        }
    }

    private fun checkPhoneValidity(phone: String) {
        NumVerifyClient(this).api(phone, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
            ccp.fullNumber = credential?.id
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST &&
            resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE
        ) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(result: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                pbCheckingPhoneNumber.visibility = View.GONE
                if (ccp.isValid && result) {
                    startActivity(
                        Intent(this, OtpActivity::class.java)
                            .putExtra(PHONE_NUMBER, ccp.number)
                    )
                    finish()
                } else
                    Toast.makeText(this, "Invalid number!", Toast.LENGTH_SHORT).show()
            },
            500
        )
    }

}