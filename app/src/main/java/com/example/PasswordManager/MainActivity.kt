package com.example.PasswordManager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity() : AppCompatActivity() {

    val REQUEST_CODE_SET_DEFAULT:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mAutofillManager=getSystemService(AutofillManager::class.java)

        if (mAutofillManager != null && mAutofillManager.hasEnabledAutofillServices()) {

            val intent: Intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
            intent.data = Uri.parse("package:com.example.anndroid.autofill.service")
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT)


        }


        save_btn.setOnClickListener {
            val editor:SharedPreferences.Editor =
                getSharedPreferences("EMAIL_STORAGE", Context.MODE_PRIVATE).edit()
            editor.putString("email",email.text.toString())
            editor.putString("password",password.text.toString())
            editor.apply()
        }
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SET_DEFAULT -> onDefaultServiceSet(
                resultCode
            )
        }
    }
    private fun onDefaultServiceSet(resultCode: Int) {

        when (resultCode) {
            RESULT_OK -> {
                Snackbar.make(
                    findViewById(R.id.main_activity),
                    "Autofill service set.", Snackbar.LENGTH_SHORT
                )
                    .show()
            }
            RESULT_CANCELED -> {
                Snackbar.make(
                    findViewById(R.id.main_activity),
                    "Autofill service not set.", Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}
