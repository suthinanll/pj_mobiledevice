package com.example.ass07


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface RoomAPI {

    // ดึงข้อมูลห้องทั้งหมด
    @GET("/getroom")
    fun retrieveAllRooms(): Call<List<Room>>

    // เพิ่มห้องใหม่
    @FormUrlEncoded
    @POST("/addroom")
    fun insertRoom(
        @Field("type_type_id") typeTypeId: String,
        @Field("status") status: Int,
    ): Call<Room>

    companion object {
        fun create(): RoomAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RoomAPI::class.java)
        }
    }
}

