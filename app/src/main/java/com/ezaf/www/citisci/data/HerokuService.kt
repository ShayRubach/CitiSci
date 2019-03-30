package com.ezaf.www.citisci.data
import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.RequestBody
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*

interface HerokuService {

    @GET("https://tempcitisci.herokuapp.com/api/v1/experiments")
    fun getAllExperiments(): Call<JsonElement>

    @GET("hhttps://tempcitisci.herokuapp.com/api/v1/experiments/{id}")
    fun getExpById(@Path("id")id: String): Call<ResponseBody>

    @PUT("https://tempcitisci.herokuapp.com/api/v1/experiments/{id}/samples/")
    fun putSample(@Path("id") id: String, @Body body: String): Call<ResponseBody>

}
