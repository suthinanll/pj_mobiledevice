const express = require("express");
const router = express.Router();
const dbConn = require("../db"); // เชื่อมต่อกับฐานข้อมูล

// ดึงข้อมูลสัตว์เลี้ยงตาม `user_id`
router.get("/mypet/:id", function (req, res) {
    let user_id = req.params.id;
    const query = `
    SELECT pets.pet_id, pets.user_id, pets.pet_name, pets.pet_age, pets.pet_breed,
    pets.pet_weight, pets.pet_gender, pets.additional_info, pet_type.pet_name_type
    FROM pets
    INNER JOIN pet_type ON pets.pet_type_id = pet_type.pet_type_id
    WHERE pets.deleted_at IS NULL AND pets.user_id = ?;
  `;
    dbConn.query(query, [user_id], function (error, results) {
        if (error) {
            return res.status(500).send({ error: true, message: "Database query failed", details: error });
        }
        return res.send(results);
    });
});

//เพิ่มสัตว์เลี้ยง
router.post('/pet', function (req, res) {
    // if (!req.session.userId) {
    //     return res.status(401).send({ message: 'Unauthorized: Please log in' });
    // } ตรวจสอบ req.session.userId ก่อนใช้

    var pets = req.body;
    if (!pets || !pets.pet_name || !pets.pet_type_id) {
        return res.status(400).send({ message: 'Please provide pet name and type' });
    }

    //pets.User_id = req.session.userId;  // ✅ กำหนด `User_id` จาก session

    dbConn.query('INSERT INTO pets SET ?', pets, function (error, results, fields) {
        if (error) {
            return res.status(500).send({ error: true, message: 'Failed to insert pet data', details: error });
        }
        return res.send({ message: 'Pet added successfully', id: results.insertId });
    });
});

// อัปเดตข้อมูลสัตว์เลี้ยง
router.put("/updatePet/:id", (req, res) => {
    console.log("Received Data from Android:", req.body);
    const petID = req.params.id;
    const { pet_name, pet_gender, pet_breed, pet_age, pet_weight, additional_info, pet_type_id } = req.body;

    if (!pet_name || !pet_gender || !pet_breed || !pet_age || !pet_weight || !additional_info || !pet_type_id) {
        console.log("❌ Missing Data:", {
            petID,
            petName,
            petGender,
            petBreed,
            petAge,
            petWeight,
            additionalInfo,
            petTypeId
        });
        return res.status(400).json({ error: true, message: "Missing required fields" });
    }

    const query = `UPDATE pets SET pet_name = ?, pet_gender = ?, pet_breed = ?, pet_age = ?, pet_weight = ?, additional_info = ?, pet_type_id = ?, updated_at = NOW() WHERE pet_id = ?`;

    dbConn.query(query, [pet_name, pet_gender, pet_breed, pet_age, pet_weight, additional_info, pet_type_id, petID], (error, results) => {
        if (error) {
            return res.status(500).json({ error: true, message: "Database update failed", details: error });
        }
        res.json({ message: "Pet updated successfully" });
    });
});

router.post('/softDeletePet', function (req, res) {
    const { pet_id, deleted_at } = req.body;

    if (!pet_id || !deleted_at) {
        console.error("Missing parameters:", { pet_id, deleted_at });
        return res.status(400).send({ message: "Missing required parameters" });
    }

    const query = `UPDATE pets SET deleted_at = ? WHERE pet_id = ?`;

    dbConn.query(query, [deleted_at, pet_id], function (error, results) {
        if (error) {
            console.error("Database error:", error);
            return res.status(500).send({ error: true, message: "Database update failed", details: error });
        }
        if (results.affectedRows === 0) {
            return res.status(404).send({ message: "Pet ID not found" });
        }
        return res.send({ message: "Soft delete successful" });
    });
});

module.exports = router;
