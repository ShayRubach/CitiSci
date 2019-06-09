package com.ezaf.www.citisci.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        hideStatusBar()

        loginBtn.setOnClickListener {
            SharedDataHelper.currUser = emailTextBox.text.toString().toLowerCase()
            goToMainActivity()
        }

        if(!SharedDataHelper.currUser.isEmpty()){
            goToMainActivity()
        }
    }

    private fun hideStatusBar() {
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}
