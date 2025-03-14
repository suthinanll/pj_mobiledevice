package com.example.ass07.customer.API

import com.example.ass07.customer.Mypet.PetType
import com.example.ass07.customer.Mypet.UpdatePetRequest
import com.example.ass07.customer.Mypet.petMember
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

interface PetApi {




    @GET("getPetTypes")
    fun getPetTypes(): Call<List<PetType>>

    @GET("allpet")
    fun allpet(): Call<List<petMember>>

    @GET("mypet/{user_id}")
    fun mypet(
        @Path("user_id") user_id: Int): Call<List<petMember>>

    @GET("mypet-by-pet-id/{user_id}/{pet_type_id}")
    fun myPetByPetId(
        @Path("user_id") userId: Int,
        @Path("pet_type_id") petId: Int
    ) : Call<List<petMember>>

    @FormUrlEncoded
    @POST("pet")
    fun insertPet(
        @Field("pet_name") petName: String,
        @Field("pet_gender") petGender: String,
        @Field("user_id") userId: Int,
        @Field("pet_type_id") petTypeId: Int,
        @Field("pet_breed") petBreed: String?,
        @Field("pet_age") petAge: Int?,
        @Field("pet_weight") petWeight: Double?,
        @Field("additional_info") additionalInfo: String?

    ): Call<petMember>

//    @FormUrlEncoded
//    @POST("addPetType") //
//    fun addPetType(@Field("pet_name_type") petTypeName: String): Call<AddPetTypeResponse> // Use the new data class

    @POST("addPetType")
    fun addPetType(@Body petName: String): Call<Void>

    @GET("getPet/{id}")
    fun getPet(@Path("id") petID: Int): Call<petMember>

    @FormUrlEncoded
    @POST("softDeletePet")
    fun softDeletePet(
        @Field("pet_id") petId: Int,
        @Field("deleted_at") deleteAt: String?
    ): Call<Void>

    @PUT("updatePet/{id}")
    fun updatePet(
        @Path("id") id: Int,
        @Body petData: UpdatePetRequest
    ): Call<petMember>


    companion object {
        fun create(): PetApi {
            val petClient: PetApi = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PetApi::class.java)
            return petClient
        }
    }

}