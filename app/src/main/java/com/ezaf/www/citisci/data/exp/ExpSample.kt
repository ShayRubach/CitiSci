package com.ezaf.www.citisci.data.exp

class ExpSample(val experimentID: String,
                val actionID: String,
                val participantEmail: String = "participant@gmail.com",
                val sample: Any)

class LatLong(val latitude: Double, val longitude: Double)