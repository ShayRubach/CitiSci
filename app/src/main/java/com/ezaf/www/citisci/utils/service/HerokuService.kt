package com.ezaf.www.citisci.utils.service
import com.ezaf.www.citisci.data.exp.ExpSampleList
import com.ezaf.www.citisci.utils.db.JoinExpRequest
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface HerokuService {

    @GET("https://citisci-services.herokuapp.com/api/v1/experiments/participants")
    fun getAllExperiments(): Call<JsonElement>

    @GET("https://citisci-services.herokuapp.com/api/v1/experiments/users/{email}/p")
    fun getMyExperiments(@Path("email")email: String): Call<JsonElement>

    @GET("https://citisci-services.herokuapp.com/api/v1/experiments/{id}")
    fun getExpById(@Path("id")id: String): Call<JsonElement>

    @POST("https://citisci-services.herokuapp.com/api/v1/samples/{type}")
    fun putSampleList(@Body body: ExpSampleList, @Path("type")type: String): Call<ExpSampleList>

    @PUT("https://citisci-services.herokuapp.com/api/v1/experiments/users/subscribe/{email}")
    fun joinExp(@Body body: JoinExpRequest, @Path("email")email: String): Call<JoinExpRequest>

}
