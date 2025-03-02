package com.example.ass07.customer.API

import com.example.ass07.customer.Home.AvailableRoomsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SearchApi {
    @GET("availableRooms")
    fun getAvailableRooms(
        @Query("check_in") checkIn: String,
        @Query("check_out") checkOut: String,
        @Query("pet_type_id") petTypeId: Int? = null
    ): Call<AvailableRoomsResponse>


    @GET("availableRooms/{type_type_id}")
    fun getAvailableRoomsByType(
        @Path("type_type_id") typeId: Int,
        @Query("check_in") checkIn: String,
        @Query("check_out") checkOut: String,
        @Query("pet_type_id") petTypeId: Int? = null
    ) : Call<AvailableRoomsResponse>



    companion object{
        fun create() : SearchApi {
            val SearchClient : SearchApi = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SearchApi::class.java)
            return SearchClient
        }
    }

}
