package com.example.ass07.admin

class RoomStatus {
    enum class RoomFilter {
        ALL,
        AVAILABLE,
        OCCUPIED,
        ROOM_TYPE,
        PET_TYPE
    }


    enum class RoomSort {
        PRICE_LOW_TO_HIGH,
        PRICE_HIGH_TO_LOW,
        NAME_A_TO_Z,
        NAME_Z_TO_A
    }
}