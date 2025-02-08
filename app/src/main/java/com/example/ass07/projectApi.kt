package com.example.ass07

import android.provider.ContactsContract.CommonDataKinds.Email
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface projectApi {
    @GET("allUser")
    fun retrievepj(): Call<List<users>>


    @FormUrlEncoded
    @POST("Register")
    fun insertuser(
        @Field("name") name: String,
        @Field("tell_number") tell_number: Int,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("user_type") user_type: Int = 2


        ):Call<users>


    companion object{
        fun create(): projectApi{
            val empClient: projectApi = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(projectApi::class.java)
            return empClient
        }
    }

}