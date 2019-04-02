package com.ezaf.www.citisci.utils.service
import com.ezaf.www.citisci.data.exp.ExpSample
import com.google.gson.JsonElement
import retrofit2.Call;
import retrofit2.http.*

interface HerokuService {

    @GET("https://tempcitisci.herokuapp.com/api/v1/experiments")
    fun getAllExperiments(): Call<JsonElement>

    @GET("https://tempcitisci.herokuapp.com/api/v1/experiments/{id}")
    fun getExpById(@Path("id")id: String): Call<JsonElement>

    @PUT("https://tempcitisci.herokuapp.com/api/v1/experiments/{id}/samples")
    fun putSample(@Path("id") id: String, @Body body: ExpSample): Call<ExpSample>

}