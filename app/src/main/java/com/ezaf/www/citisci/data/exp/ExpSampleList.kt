package com.ezaf.www.citisci.data.exp

class ExpSampleList {
    private val samples = mutableListOf<ExpSample>()

    fun addSample(sample: ExpSample) = samples.add(sample)

    override fun toString(): String {
        return samples[0].sample.toString()
    }
}