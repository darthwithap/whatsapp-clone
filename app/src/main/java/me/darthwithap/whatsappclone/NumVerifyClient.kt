package me.darthwithap.whatsappclone

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

private const val TAG = "NumVerifyClient"

class NumVerifyClient(private val context: Context) {
    private var isValid = false
    private lateinit var url : String

    private fun getResult(callback: VolleyCallback)  {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                Log.d(TAG, ": ${it.get("valid").toString().toBoolean()}")
                isValid = it.get("valid").toString().toBoolean()
                callback.onSuccess(isValid)
            },
            Response.ErrorListener {
                Log.d(TAG, "getResult: $it")
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    fun api(number: String, callback: VolleyCallback) {
        url = "http://apilayer.net/api/validate?access_key=" +
                context.resources.getString(R.string.num_verify_access_key) +
                "&number=$number&country_code=&format=1"
        Log.d(TAG, "api: $url")
        getResult(callback)
    }

    interface VolleyCallback {
        fun onSuccess(result: Boolean)
    }
}