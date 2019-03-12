package com.ezaf.www.citisci

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ezaf.www.citisci.databinding.ActivityMainBinding
import com.ezaf.www.citisci.utils.MainViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel = ViewModelProviders.of(this)
                .get(MainViewModel::class.java)

        DataBindingUtil.setContentView<ActivityMainBinding>(
                this, R.layout.activity_main
        ).apply {
            this.setLifecycleOwner(this@MainActivity)
            this.viewmodel = mainViewModel
        }

        goToCameraBtn.setOnClickListener{
            startActivity(Intent(this, CameraActivity::class.java))
            finish()
        }


//        mainViewModel.editTextContent.observe(this, Observer {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//        })

    }
}
