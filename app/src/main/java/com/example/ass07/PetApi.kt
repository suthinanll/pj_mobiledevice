package com.example.ass07

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface PetApi {
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
        @Field("Pet_nametype") petTypename: String,
        @Field("Pet_type_id") Pet_type_id: String,
        @Field("User_id") userId: Int
    ): Call<petMember>

    @FormUrlEncoded
    @POST("softDeletePet")
    fun softDeletePet(
        @Field("Pet_id") petId: Int
    ): Call<Void>


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