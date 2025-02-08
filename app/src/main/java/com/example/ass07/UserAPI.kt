package com.example.ass07

import android.telecom.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UserAPI {

    @GET("allMember")
    fun retrieveMember(): Call<List<Member>>
    @FormUrlEncoded
    @POST("emp")
    fun insertEmp(
        @Field("name") emp_name: String,
        @Field("tell_number") emp_gender: String,
        @Field("email") emp_email: String,
        @Field(" ") emp_salary: Int): Call<Member>


    companion object {
        fun create(): UserAPI {
            val empClient : UserAPI = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserAPI ::class.java)
            return empClient
        }
    }
}