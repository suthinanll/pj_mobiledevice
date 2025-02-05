package com.example.ass07

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetViewModel : ViewModel() {
    private val _pet = MutableLiveData<petMember?>()
    val pet: LiveData<petMember?> = _pet

    fun loadPet(petId: Int) {
        val createClient = PetApi.create()
        createClient.getPet(petId).enqueue(object : Callback<petMember> {
            override fun onResponse(call: Call<petMember>, response: Response<petMember>) {
                if (response.isSuccessful) {
                    _pet.value = response.body()
                }
            }

            override fun onFailure(call: Call<petMember>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch pet data: ${t.message}")
            }
        })
    }
}