package com.example.ass07.admin


import com.example.ass07.customer.Mypet.PetType
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
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
        @Field("pet_type") pet_type: String,
        @Field("image") image: String?
    ): Call<RoomTypeResponse>


    @GET("updateroom/{room_id}")
    fun getRoomById(
        @Path("room_id") room_id: Int,
    ): Call<Room>



//    @PUT("updateroom/{room_id}")
//    fun updateroom(
//        @Path("room_id") room_id: Int,
//        @Field("room_type_id") roomTypeId: Any,
//        @Field("room_status") roomStatus: Int,
//        @Field("pet_type") pet_type:String,
//        @Field("image") image:String
//    ): Call<Room>

    @FormUrlEncoded
    @PUT("updateroom/{room_id}")
    fun updateroom(
        @Path("room_id") room_id: Int,
        @Field("room_type_id") roomTypeId: Int,
        @Field("room_status") roomStatus: Int
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

