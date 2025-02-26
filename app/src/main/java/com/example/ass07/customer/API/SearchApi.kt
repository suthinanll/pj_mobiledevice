package com.example.ass07.customer.API

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET


interface SearchApi {
    @GET("search-rooms")
    fun searchRooms(
        @Field("pet_type_id") petTypeId: Int,
        @Field("check_in") checkIn: String,
        @Field("check_out") checkOut: String
    ): Call<asd>
}
g