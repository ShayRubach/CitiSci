package com.ezaf.www.citisci

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ezaf.www.citisci.databinding.ActivityMainBinding
import com.ezaf.www.citisci.utils.MainViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import android.widget.Toast

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

//        mainViewModel.editTextContent.observe(this, Observer {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//        })

    }
}
