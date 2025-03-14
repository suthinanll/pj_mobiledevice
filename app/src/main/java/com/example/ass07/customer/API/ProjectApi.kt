package com.example.ass07.customer.API

import com.example.ass07.customer.LoginRegister.LoginClass
import com.example.ass07.customer.Profile.UpdateProfileRequest
import com.example.ass07.customer.Profile.UpdateProfileResponse
import com.example.ass07.customer.Profile.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface projectApi {
    @FormUrlEncoded
    @POST("login")
    fun login_acc(
        @Field("name") name : String,
        @Field("password") password : String
    ): Call<LoginClass>

//    @GET("search/{std_id}")
//    fun searchStudent(
//        @Path("std_id") std_id : String
//    ) : Call<ProfileClass>

    @FormUrlEncoded
    @POST("insertAccount")
    fun register_acc(
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("tell_number") tell_number: String,
        @Field("email") email: String,
        @Field("user_type") user_type: Int
    ): Call<LoginClass>


    @GET("profile/{id}")
    fun getProfileByID(@Path("id") userID: Int): Call<User>

    @PUT("profile/edit/{id}")
    fun updateProfile(
        @Path("id") userId: Int,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>





    companion object{
        fun create() : projectApi {
            val studentClient : projectApi = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(projectApi::class.java)
            return studentClient
        }
    }

}