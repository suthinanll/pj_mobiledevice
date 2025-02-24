const express = require("express");
const bcrypt = require("bcryptjs");
const router = express.Router();
const dbConn = require("../db"); // เชื่อมต่อกับฐานข้อมูล

// สมัครสมาชิก
router.post("/insertAccount", async function (req, res) {
  let post = req.body;
  let { name, tell_number, email, password, user_type } = post;

  const salt = await bcrypt.genSalt(10);
  let password_hash = await bcrypt.hash(password, salt);

  if (!post) {
    return res.status(400).send({ error: true, message: "Please provide user data" });
  }

  dbConn.query("SELECT * FROM users WHERE name = ?", name, function (error, results) {
    if (error) throw error;
    if (results[0]) {
      return res.status(400).send({ error: true, message: "The user name already in the database." });
    } else {
      var insertData = `INSERT INTO users (name, password, tell_number, email, user_type)
                        VALUES('${name}', '${password_hash}', '${tell_number}', '${email}', ${user_type || 2})`;
      dbConn.query(insertData, function (error, results) {
        if (error) throw error;
        return res.send(results);
      });
    }
  });
});

// ล็อกอิน
router.post("/login", async function (req, res) {
  let { name, password } = req.body;

  if (!name || !password) {
    return res.status(400).send({ error: true, message: "Please provide name and password" });
  }

  dbConn.query("SELECT * FROM users WHERE email = ? OR tell_number = ?", [name, name], function (error, results) {
    if (error) throw error;
    if (results[0]) {
      bcrypt.compare(password, results[0].password, function (err, result) {
        if (err) throw err;
        if (result) {
          return res.send({
            success: 1,
            name: results[0].name,
            user_type: results[0].user_type,
            user_id: results[0].user_id,
            email: results[0].email,
            tell_number: results[0].tell_number,
          });
        } else {
          return res.send({ success: 0 });
        }
      });
    } else {
      return res.send({ success: 0 });
    }
  });
});

// ดึงข้อมูลสัตว์เลี้ยง
router.get("/allpet", function (req, res) {
  const query = `
    SELECT pets.pet_id, pets.user_id, pets.pet_name, pets.pet_age, pets.pet_breed,
    pets.pet_weight, pets.pet_gender, pets.additional_info, pet_type.pet_name_type
    FROM pets
    INNER JOIN pet_type ON pets.pet_type_id = pet_type.pet_type_id
    WHERE pets.deleted_at IS NULL;
  `;
  dbConn.query(query, function (error, results) {
    if (error) {
      return res.status(500).send({ error: true, message: "Database query failed", details: error });
    }
    return res.send(results);
  });
});

// เพิ่มสัตว์เลี้ยง
router.post("/pet", function (req, res) {
  var pets = req.body;
  if (!pets || !pets.pet_name || !pets.pet_type_id) {
    return res.status(400).send({ message: "Please provide pet name and type" });
  }

  dbConn.query("INSERT INTO pets SET ?", pets, function (error, results) {
    if (error) {
      return res.status(500).send({ error: true, message: "Failed to insert pet data", details: error });
    }
    return res.send({ message: "Pet added successfully", id: results.insertId });
  });
});

module.exports = router;
