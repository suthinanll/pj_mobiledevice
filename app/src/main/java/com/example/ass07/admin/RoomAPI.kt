package com.example.ass07.admin


import com.example.ass07.customer.BookingData
import com.example.ass07.customer.Mypet.PetType
import com.example.ass07.customer.PaymentMethodData
import com.example.ass07.customer.PetData
import com.example.ass07.customer.RoomData
import com.example.ass07.customer.RoomTypeData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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



    @Multipart
    @POST("/addRoomType")
    fun addRoomType(
        @Part("name_type") name_type: RequestBody,
        @Part("price_per_day") price_per_day: RequestBody,
        @Part("pet_type") pet_type: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<RoomTypeResponse>

    @Multipart
    @POST("addRoomType")
    fun uploadRoomData(
        @Part image: MultipartBody.Part, // Image part
        @Part("name_type") name_type: RequestBody,  // Room name part
        @Part("price_per_day") price_per_day: RequestBody, // Price per day part
        @Part("pet_type") pet_type: RequestBody  // Pet type part
    ): Call<RoomType>


    @GET("updateRoomType/{room_type_id}")
    fun getRoomTypeById(
        @Path("room_type_id") roomTypeId: Int,
    ): Call<RoomType>

    @Multipart
    @PUT("updateRoomType/{room_type_id}")
    fun updateRoomTypeWithImage(
        @Path("room_type_id") room_type_id: Int,
        @Part image: MultipartBody.Part,
        @Part("name_type") name_type: RequestBody,
        @Part("price_per_day") price_per_day: RequestBody,
        @Part("pet_type") pet_type: RequestBody
    ): Call<RoomTypeResponse>



    @FormUrlEncoded
    @PUT("updateRoomType/{room_type_id}")
    fun updateRoomTypeNoImage(
        @Path("room_type_id") room_type_id: Int,
        @Field("name_type") name_type: String,
        @Field("price_per_day") price_per_day: Double,
        @Field("pet_type") pet_type: Int
    ): Call<RoomTypeResponse>


    @GET("updateroom/{room_id}")
    fun getRoomById(
        @Path("room_id") room_id: Int,
    ): Call<Room>


    @FormUrlEncoded
    @PUT("updateroom/{room_id}")
    fun updateroom(
        @Path("room_id") room_id: Int,
        @Field("room_type_id") roomTypeId: Any,
        @Field("room_status") roomStatus: Int
    ): Call<Room>

    @FormUrlEncoded
    @POST("softDeleteRoom")
    fun softDeleteRoom(
        @Field("room_id") room_id: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("/softDeleteRoomType")
    fun softDeleteRoomType(
        @Field("room_type_id") room_type_id: Int
    ): Call<Void>

    @GET("get-booking/{user_id}")
    fun getBooking(@Path("user_id") userId: Int) : Call<List<BookingData>>

    @GET("get-payment-method/{method_id}")
    fun getPaymentMethod(
        @Path("method_id") methodId: Int
    ) : Call<PaymentMethodData>

    @GET("get-pet/{pet_id}")
    fun getPet(
        @Path("pet_id") petId: Int
    ) : Call<PetData>

    @GET("get-room/{room_id}")
    fun getRoom(
        @Path("room_id") roomId: Int
    ) : Call<RoomData>

    @GET("get-room-type/{type_id}")
    fun getRoomType(
        @Path("type_id") typeId : Int
    ) : Call<RoomTypeData>

    @PUT("update-booking-status/{booking_id}")
    fun updateBookingStatus(
        @Path("booking_id") bookingId: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("insert-booking")
    fun insertBooking(
        @Field("check_in") checkIn : String,
        @Field("check_out") checkOut : String,
        @Field("additional_info") additionalInfo : String,
        @Field("pay") pay : Int,
        @Field("total_pay") totalPay : Int,
        @Field("payment_method") paymentMethod : Int,
        @Field("pet_id") petId : Int,
        @Field("room_id") roomId : Int
    ) : Call<ResponseBody>



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

