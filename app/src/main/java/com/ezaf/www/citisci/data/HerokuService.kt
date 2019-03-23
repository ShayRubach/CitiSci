package com.ezaf.www.citisci.data
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*

interface HerokuService {
    @GET("https://buxa.herokuapp.com/")
    fun getMyData(): Call<ResponseBody>

    @GET("https://tempcitisci.herokuapp.com/experiment/all")
    fun getAllExperiments(): Call<ResponseBody>

    @GET("https://tempcitisci.herokuapp.com/experiment/findByID/:{id}")
    fun getExpById(@Path("id")id: String): Call<ResponseBody>

    @POST("https://tempcitisci.herokuapp.com/experiment/create")
    fun createDummyExp(): Call<ResponseBody>

    @PUT("https://tempcitisci.herokuapp.com/experiment/addSamples/:{id}")
    fun putSample(@Path("id")id: String): Call<ResponseBody>

//    In the body of the msg send a json like:
//    {
//
//        "samples":
//        {
//
//            "x":45,
//
//            "y":15
//
//        }
//
//    }
}
