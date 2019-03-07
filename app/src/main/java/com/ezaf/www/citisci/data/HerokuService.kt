package com.ezaf.www.citisci.data
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface HerokuService {
    @GET("https://buxa.herokuapp.com/")
    fun getMyData(): Call<ResponseBody>
}
