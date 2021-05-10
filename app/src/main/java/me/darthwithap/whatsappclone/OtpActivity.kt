package me.darthwithap.whatsappclone

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otp.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var exitSnackbar: Snackbar
    private lateinit var phNo: String
    private var otpCooldownTime = 10
    private var isCoolDown = true
    private lateinit var etList: List<EditText>
    private lateinit var auth: FirebaseAuth
    private lateinit var credential: PhoneAuthCredential
    private lateinit var resendTimerSpannableString: SpannableString
    private lateinit var clickableResendTimerSpan: ClickableSpan
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = ""
    private var codeEntered = ""
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var imm: InputMethodManager
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        phNo = intent.getStringExtra("phoneNumber").toString()
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        initViews()
        setResetTimeSpan("in", timerToText(otpCooldownTime))
        startTimer()
        verify()

        btnContinue.setOnClickListener {
            getCodeEntered()
            if (codeEntered.length == 6) {
                verifyPhoneNumberWithCode(storedVerificationId, codeEntered)
            } else
                Toast.makeText(this, "Invalid otp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCodeEntered() {
        val sb = StringBuilder()
        etList.forEach {
            sb.append(it.text.toString())
        }
        codeEntered = sb.toString().trim()
    }

    //[START verify]
    private fun verify() {
        //Initialize phone_auth_callbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(cred: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: $cred")
                Toast.makeText(this@OtpActivity, "$cred and will sign you in.", Toast.LENGTH_SHORT)
                    .show()
                val code = cred.smsCode
                if (code != null) {
                    Log.d(TAG, "onVerificationCompleted: $code")
                    verifyPhoneNumberWithCode(storedVerificationId, code)
                } else signInWithPhoneAuthCredential(cred)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(TAG, "onVerificationFailed: ${e.localizedMessage}")
                Toast.makeText(this@OtpActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                Log.d(TAG, "onCodeSent: s - $verificationId and t - $token")
                Toast.makeText(this@OtpActivity, "Code has been sent", Toast.LENGTH_SHORT).show()
                //GET FOCUS ON EDIT TEXTS
                Handler(Looper.getMainLooper()).postDelayed({
                    pbDetectingOtp.visibility = View.GONE
                    etNum1.requestFocus()
                    imm.showSoftInput(etNum1, InputMethodManager.SHOW_IMPLICIT)
                }, 250)
            }
        }

        //startVerification
        startPhoneNumberVerification(phNo)
    }
    //[END verify]

    //[START resend_verification_code]
    private fun resendVerificationCode(
        phNo: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        pbDetectingOtp.visibility = View.VISIBLE
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phNo)
            .setTimeout(otpCooldownTime.toLong(), TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    //[END resend_verification_code]

    //[START verify_phone_number_with_code]
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        //pbDetectingOtp.visibility = View.VISIBLE
        credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }
    //[END verify_phone_number_with_code]

    //[START start_phone_number_verification]
    private fun startPhoneNumberVerification(phNo: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phNo)
            .setTimeout(otpCooldownTime.toLong(), TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    //[END start_phone_number_verification]

    //[START sign_in_with_phone_auth_credential]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    pbDetectingOtp.visibility = View.GONE
                    //INTENT TO MAIN CHAT SCREEN
                    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    Toast.makeText(this, "signIn Success!", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                } else {
                    pbDetectingOtp.visibility = View.GONE
                    Toast.makeText(this, "signIn Failure", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Incorrect code entered", Toast.LENGTH_SHORT).show()
                    }
                    //UPDATE UI
                }
            }
    }
    //[END sign_in_with_phone_auth_credential]

    //[START start_timer]
    private fun startTimer() {
        isCoolDown = false
        setResetTimeSpan("in", timerToText(otpCooldownTime))
        countdownTimer = object : CountDownTimer((otpCooldownTime * 1000).toLong(), 1000) {
            override fun onFinish() {
                isCoolDown = true
                setResetTimeSpan("", "")
            }

            override fun onTick(millisUntilFinished: Long) {
                setResetTimeSpan("in", timerToText((millisUntilFinished / 1000).toInt()))
            }
        }.start()
    }
    //[END start_timer]

    //[START init_views]
    private fun initViews() {
        exitSnackbar =
            Snackbar.make(llParent, "Please press back again to exit.", Snackbar.LENGTH_SHORT)

        //imm
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //setting listeners to otp edit texts
        etList = listOf(etNum1, etNum2, etNum3, etNum4, etNum5, etNum6)
        for (i in (0..4)) etList[i].addTextChangedListener(OtpTextWatcher(etList[i], etList[i + 1]))
        etNum6.addTextChangedListener(OtpTextWatcher(etNum6, null))
        for (i in 5.downTo(1)) etList[i].setOnKeyListener(OtpKeyEvent(etList[i], etList[i - 1]))

        //Number Span
        val numberSpannableString = SpannableString(getString(R.string.enter_otp_sent, phNo))
        val clickableNumberSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                AlertDialog.Builder(this@OtpActivity)
                    .setCancelable(true)
                    .setMessage("Do you want to change the number?")
                    .setPositiveButton("Yes") { _, _ ->
                        startActivity(
                            Intent(this@OtpActivity, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                        widget.invalidate()
                    }.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        numberSpannableString.setSpan(
            clickableNumberSpan,
            18,
            18 + phNo.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tvEnterOtpTitle.movementMethod = LinkMovementMethod.getInstance()
        tvEnterOtpTitle.text = numberSpannableString
    }
    //[END init_views]

    //[START set_reset_time_span]
    private fun setResetTimeSpan(prefix: String, time: String) {
        //ResendTimer Span
        resendTimerSpannableString =
            SpannableString(getString(R.string.resend_otp_in, prefix, time))
        clickableResendTimerSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //resend otp.
                if (isCoolDown) {
                    if (resendToken != null) resendVerificationCode(phNo, resendToken!!)
                    else startPhoneNumberVerification(phNo)
                    startTimer()
                } else Toast.makeText(
                    this@OtpActivity,
                    "Another request in progress",
                    Toast.LENGTH_SHORT
                ).show()
                widget.invalidate()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = if (!isCoolDown) Color.parseColor(
                    String.format(
                        "#%06x", ContextCompat.getColor(
                            this@OtpActivity,
                            R.color.regular_gray
                        )
                    )
                ) else ds.linkColor
                ds.isUnderlineText = false
            }
        }
        resendTimerSpannableString.setSpan(
            clickableResendTimerSpan,
            0,
            10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tvResendTimer.movementMethod = LinkMovementMethod.getInstance()
        tvResendTimer.text = resendTimerSpannableString
    }
    //[END set_reset_time_span]

    private fun timerToText(time: Int) =
        if (time % 60 >= 10) "${time / 60}:${time % 60}" else "${time / 60}:0${time % 60}"

    //[START OtpKeyEvent CLASS]
    internal inner class OtpKeyEvent(
        private val currentView: EditText,
        private val previousView: EditText?
    ) :
        View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL
                && currentView.id != R.id.etNum1 && currentView.text.isEmpty()
            ) {
                previousView?.text = null
                previousView?.requestFocus()
                return true
            }
            return false
        }
    }
    //[END OtpKeyEvent CLASS]

    //[START OtpTextWatcher CLASS]
    internal inner class OtpTextWatcher(
        private val currentView: View,
        private val nextView: View?
    ) :
        TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (currentView.id) {
                R.id.etNum1 -> if (text.length == 1) nextView?.requestFocus()
                R.id.etNum2 -> if (text.length == 1) nextView?.requestFocus()
                R.id.etNum3 -> if (text.length == 1) nextView?.requestFocus()
                R.id.etNum4 -> if (text.length == 1) nextView?.requestFocus()
                R.id.etNum5 -> if (text.length == 1) nextView?.requestFocus()
                R.id.etNum6 -> if (text.length == 1) nextView?.requestFocus()
            }
            getCodeEntered()
            if (codeEntered.length == 6) {
                imm.hideSoftInputFromWindow(currentView.windowToken, 0)
                verifyPhoneNumberWithCode(storedVerificationId, codeEntered)
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    //[END OtpTextWatcher CLASS]

    override fun onBackPressed() {
        if (exitSnackbar.isShown) {
            super.onBackPressed()
            return
        }
        exitSnackbar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer
    }

    companion object {
        private const val TAG = "OtpActivity"
    }
}