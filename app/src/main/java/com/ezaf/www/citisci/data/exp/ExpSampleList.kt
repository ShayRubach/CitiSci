package com.ezaf.www.citisci.data.exp

class ExpSampleList {
    private val samples = mutableListOf<ExpSample>()

    fun addSample(sample: ExpSample) = samples.add(sample)
}