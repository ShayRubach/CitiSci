package com.ezaf.www.citisci.utils


import androidx.lifecycle.ViewModel
import com.ezaf.www.citisci.data.Experiment
import com.ezaf.www.citisci.data.Repository

class ExpViewModel(private val expRepository: Repository) : ViewModel() {

    fun joinExp(exp: Experiment) = expRepository.joinExp(exp)
    fun getExp(id: String) = expRepository.getExp(id)

}