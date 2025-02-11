package com.example.ass07


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface RoomAPI {

    // ดึงข้อมูลห้องทั้งหมด
    @GET("/getroom")
    fun retrieveAllRooms(): Call<List<Room>>

    // เพิ่มห้องใหม่
//    @POST("/addroom")
//    fun insertRoom(
//        @Body roomName: String,
//        @Body roomTypeId: Int,
//        @Body roomStatus: Int,
//        @Body additionalInfo: String
//    ): Call<Room>

    companion object {
        // สร้าง Retrofit instance และ return API
        fun create(): RoomAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // URL ของ API ของคุณ
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RoomAPI::class.java)
        }
    }
}

