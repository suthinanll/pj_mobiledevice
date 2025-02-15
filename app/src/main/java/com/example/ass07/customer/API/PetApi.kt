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
    @GET("/getPetTypes")
    fun getPetTypes(): Call<List<PetType>>

    @GET("allpet")
    fun retrievepetMember(): Call<List<petMember>>

    @FormUrlEncoded
    @POST("pet")
    fun insertPet(
        @Field("Pet_name") petName: String,
        @Field("Pet_Gender") petGender: String,
        @Field("Pet_breed") petBreed: String,
        @Field("Pet_age") petAge: Int,
        @Field("Pet_weight") petWeight: Int,
        @Field("additional_info") additionalInfo: String,
        @Field("Pet_type_id") Pet_type_id: Int,
        @Field("user_id") userId: Int
    ): Call<petMember>

    @FormUrlEncoded
    @POST("softDeletePet")
    fun softDeletePet(
        @Field("pet_id") petId: Int,
        @Field("deleted_at") deleteAt: String?
    ): Call<Void>

    @PUT("updatePet/{id}")
    fun updatePet(
        @Path("id") petID: Int,
        @Body petData: UpdatePetRequest
    ): Call<petMember>

    @GET("getPet/{id}")
    fun getPet(@Path("id") petID: Int): Call<petMember>

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