package com.example.ass07.admin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookingAPI {

    //  ดึงข้อมูลการจองทั้งหมด (ที่ยังไม่ถูกลบ)
    @GET("bookings")
    fun getBookings(): Call<List<Booking>>

    //  อัปเดตข้อมูลการจอง
    @PUT("bookings/{id}")
    fun updateBooking(
        @Path("id") bookingId: Int,
        @Body bookingData: Booking
    ): Call<Map<String, String>> // ใช้ Map รับ message กลับมา

    // ยกเลิก
    @DELETE("bookings/{id}")
    fun deleteBooking(
        @Path("id") bookingId: Int
    ): Call<Map<String, String>> // ใช้ Map รับ message กลับมา


    companion object {
        fun create(): BookingAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(BookingAPI::class.java)
        }
    }
}