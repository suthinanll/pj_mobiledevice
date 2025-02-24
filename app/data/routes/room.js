const express = require("express");
const router = express.Router();
const dbConn = require("../db");

// ดึงข้อมูลห้องพัก
router.get("/getroom", (req, res) => {
  let query = `
    SELECT r.room_id, rt.name_type AS room_type, rt.price_per_day,
    r.status AS room_status, pt.Pet_name_type AS pet_type
    FROM rooms r
    JOIN room_type rt ON r.type_type_id = rt.type_id
    JOIN pet_type pt ON rt.pet_type = pt.Pet_type_id
    WHERE r.deleted_at IS NULL
    AND rt.deleted_at IS NULL
    AND pt.deleted_at IS NULL;
  `;
  dbConn.query(query, (error, results) => {
    if (error) {
      return res.status(500).send({ error: true, message: "Internal Server Error" });
    }
    return res.json(results);
  });
});

// เพิ่มห้องพัก
router.post("/addroom", function (req, res) {
  const { type_type_id, status } = req.body;
  if (!type_type_id || status === undefined) {
    return res.status(400).send({ message: "Please provide room type ID and status" });
  }

  dbConn.query("SELECT name_type FROM room_type WHERE type_id = ?", [type_type_id], function (error, results) {
    if (error) {
      return res.status(500).send({ message: "Database error", details: error });
    }
    if (results.length === 0) {
      return res.status(400).send({ message: "Invalid room_type ID" });
    }
    const roomType = results[0];

    dbConn.query("INSERT INTO rooms (type_type_id, status) VALUES (?, ?)", [type_type_id, status], function (error, results) {
      if (error) {
        return res.status(500).send({ message: "Failed to insert room data", details: error });
      }
      return res.send({ message: "Room added successfully", id: results.insertId, room_type: roomType.name_type });
    });
  });
});

module.exports = router;
