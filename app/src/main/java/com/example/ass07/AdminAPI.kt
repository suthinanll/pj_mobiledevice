package com.example.ass07

import android.telecom.Call

interface AdminAPI {
    @GET("allMember")
    fun retrieveEmp(): Call<List<Member>>
    @FormUrlEncoded
    @POST("emp")
    fun insertEmp(
        @Field("emp_name") emp_name: String,
        @Field("emp_gender") emp_gender: String,
        @Field("emp_email") emp_email: String,
        @Field("emp_salary") emp_salary: Int): Call<Emp>


    companion object {
        fun create(): EmpApi {
            val empClient : EmpApi = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EmpApi ::class.java)
            return empClient
        }
    }
}