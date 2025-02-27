package com.example.ass07.admin

import retrofit2.http.GET

import com.example.ass07.customer.Mypet.AddPetTypeResponse
import com.example.ass07.customer.Mypet.PetType
import com.example.ass07.customer.Mypet.UpdatePetRequest
import com.example.ass07.customer.Mypet.petMember
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PetApi {
    @GET("getPetTypes")
    fun getPetTypes(): Call<List<PetType>>

    @GET("allpet")
    fun retrievepetMember(): Call<List<petMember>>


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