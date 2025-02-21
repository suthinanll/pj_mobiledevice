package com.example.ass07.admin
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomViewModel : ViewModel() {
    private val _room = MutableLiveData<Room?>()  // เปลี่ยนจาก pet เป็น room
    val room: LiveData<Room?> = _room  // เปลี่ยนจาก pet เป็น room

    fun loadRoom(room_id: Int) {
        val createClient = RoomAPI.create()
        createClient.getRoomById(room_id).enqueue(object : Callback<Room> {
            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                if (response.isSuccessful) {
                    _room.value = response.body()  // เก็บข้อมูลห้องใน _room
                }
            }

            override fun onFailure(call: Call<Room>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch room data: ${t.message}")
            }
        })
    }
}