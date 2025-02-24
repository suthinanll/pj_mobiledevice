const express = require("express");
const router = express.Router();
const dbConn = require("../db"); // เชื่อมต่อกับฐานข้อมูล

// ดึงข้อมูลการจองทั้งหมด
router.get("/bookings", function (req, res) {
  const query = `
    SELECT
      bookings.*,
      pets.pet_name, pets.pet_gender, pets.pet_breed, pets.pet_age, pets.pet_height, pets.pet_weight,
      users.name , users.tell_number, users.email,
      rooms.room_id , rooms.type_type_id ,rooms.status,
      room_type.name_type, room_type.price_per_day , room_type.image, room_type.pet_type,
      pet_type.pet_name_type,
      payment_methods.method_name
    FROM bookings
    JOIN pets ON bookings.pet_id = pets.pet_id
    JOIN users ON pets.user_id = users.user_id
    JOIN rooms ON bookings.room_id = rooms.room_id
    JOIN room_type ON rooms.type_type_id = room_type.type_id
    JOIN pet_type ON room_type.pet_type = pet_type.pet_type_id
    JOIN payment_methods ON bookings.payment_method = payment_methods.method_id
    ORDER BY bookings.booking_id ASC`;
  
  dbConn.query(query, function (error, results) {
    if (error) throw error;
    return res.send(results);
  });
});

module.exports = router;
