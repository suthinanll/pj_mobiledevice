package com.example.ass07.admin


import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface RoomAPI {
    @GET("getPetTypes")
    fun getPetTypes(): Call<List<PetType>>


    @GET("getRoomTypes")
    fun getRoomTypes(): Call<List<RoomType>>

    @GET("getroom")
    fun retrieveAllRooms(): Call<List<Room>>

    @FormUrlEncoded
    @POST("addroom")
    fun insertRoom(
        @Field("room_type_id") roomTypeId: Any,
        @Field("room_status") roomStatus: Int
    ): Call<Room>



    @FormUrlEncoded
    @POST("addRoomType")
    fun addRoomType(
        @Field("name_type") name_type: String,
        @Field("price_per_day") price_per_day: Double,
        @Field("pet_type") pet_type: String
    ): Call<RoomTypeResponse>



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

