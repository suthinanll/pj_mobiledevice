var express = require("express");
var app = express();
var bodyParser = require("body-parser");
var mysql = require("mysql");
require("dotenv").config();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.get("/", function (req, res) {
  return res.send({ error: false, message: "Test Student Web API" });
});

var dbConn = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_DATABASE,
  port: process.env.DB_PORT,
});

dbConn.connect();

// app.get("/allEmp", function (req, res) {
//   dbConn.query("SELECT * FROM employee", function (error, results, fields) {
//     if (error) throw error;
//     return res.send(results);
//   });
// });

// app.post("/emp", function (req, res) {
//   var std = req.body;

//   if (!std) {
//     return res
//       .status(400)
//       .send({ error: true, message: "Please provide student" });
//   }

//   dbConn.query(
//     "INSERT INTO employee SET ? ",
//     std,
//     function (error, results, fields) {
//       if (error) throw error;
//       return res.send(results);
//     }
//   );
// });

// app.put("/update_emp/:emp_id", function (req, res) {
//   var emp = req.body;
//   var id = req.params.emp_id;
//   if (!emp) {
//     return res
//       .status(400)
//       .send({ error: std, message: "Please provide student" });
//   }
//   dbConn.query(
//     "UPDATE employee SET ? WHERE emp_id = ?",
//     [emp, id],
//     function (error, results, fields) {
//       if (error) throw error;
//       return res.send(results);
//     }
//   );
// })

// app.delete("/delete_emp/:emp_id", function (req, res) {
//   var id = req.params.emp_id;
//   dbConn.query("DELETE FROM employee WHERE emp_id = ?", [id], function (
//     error,
//     results,
//     fields
//   ) {
//     if (error) throw error;
//     return res.send(results);
//   });
// });

const bcrypt = require('bcryptjs');


app.post("/insertAccount",async function(req,res){
  let post  = req.body
  let name = post.name
  let tell_number = post.tell_number
  let email = post.email
  let password = post.password
  let user_type = post.user_type

  const salt = await bcrypt.genSalt(10)
  let password_hash = await bcrypt.hash(password,salt)

  if(!post){
      return res.status(400).send({ error: post, message: 'Please provide student data' })
  }

  dbConn.query('SELECT * FROM users WHERE name = ? ',name,function(error,results,fields){
      if(error) throw error
      if(results[0]){
          return res.status(400).send({ error: true, message: 'The user name already in the database.' })
      }else{
          if(!user_type){
            var insertData = `INSERT INTO users (name,password,tell_number,email,user_type)  
            VALUES('${name}','${password_hash}','${tell_number}','${email}', 2)`
          }else{
            var insertData = `INSERT INTO users (name,password,tell_number,email,user_type)  
            VALUES('${name}','${password_hash}','${tell_number}','${email}',2)`;
          }

          dbConn.query(insertData,function(error,results,fields){
              if(error) throw error
              return res.send(results)
          })
      }
  })
})

app.post("/login",async function(req,res){  
  let user = req.body
  let name = user.name
  let password = user.password

  if(!name || !password){
      return res.status(400).send({ error: name, message: 'Please provide name and password' })
  }

  dbConn.query('SELECT * FROM users WHERE name = ? ',[name],function(error,results,fields){
      if(error) throw error
      if(results[0]){
          bcrypt.compare(password,results[0].password,function(err,result){
              if(err) throw err
              if(result){
                  return res.send({ "success": 1,"name":results[0].name,"user_type":results[0].user_type })
              }else{
                  return res.send({ "success": 0 })
              }
          })
      }else{
          return res.send({ "success": 0 })
      }
  })
})

// app.get("/search/:std_id",function(req,res){
//   var id = req.params.std_id
//   if(!id){
//       return res.status(400).send({ error: true, message: 'Please provide student id' })
//   }
//   dbConn.query('SELECT * FROM register_student WHERE std_id = ?',[id],function(error,results,fields){
//       if(error) throw error
//       if(results[0]){
//           return res.send({"std_id":results[0].std_id,"std_name":results[0].std_name,"std_gender":results[0].std_gender,
//               "role":results[0].role})
//       }else{
//           return res.send({ error: true, message: 'Student id not found' })
//       }
//   })
// })


app.get('/allpet', function (req, res) {
  // ตรวจสอบว่ามีการล็อกอินหรือไม่
  // if (!req.session.userId) { 
  //     return res.status(401).send({ error: true, message: 'Unauthorized: Please log in' });
  // }

  //const userId = req.session.userId; // ดึง userId จาก session

  const query = `
      SELECT pets.Pet_id, pets.User_id, pets.Pet_name, pets.Pet_age, pets.Pet_breed, 
          pets.Pet_weight, pets.Pet_Gender, pets.additional_info, pet_type.Pet_nametype, pets.deleted_at
      FROM pets
      INNER JOIN pet_type ON pets.Pet_type_id = pet_type.Pet_type_id
      WHERE pets.deleted_at IS NULL;
  `; //WHERE pets.User_id = ?
  dbConn.query(query, function (error, results, fields) {
      if (error) {
          return res.status(500).send({ error: true, message: 'Database query failed', details: error });
      }
      return res.send(results);
  });
});

// 📌 เพิ่มข้อมูลสัตว์เลี้ยง
app.post('/pet', function (req, res) {
  // if (!req.session.userId) {
  //     return res.status(401).send({ message: 'Unauthorized: Please log in' });
  // } ตรวจสอบ req.session.userId ก่อนใช้

  var pets = req.body;
  if (!pets || !pets.Pet_name || !pets.Pet_type_id) {
      return res.status(400).send({ message: 'Please provide pet name and type' });
  }

  //pets.User_id = req.session.userId;  // ✅ กำหนด User_id จาก session

  dbConn.query('INSERT INTO pets SET ?', pets, function (error, results, fields) {
      if (error) {
          return res.status(500).send({ error: true, message: 'Failed to insert pet data', details: error });
      }
      return res.send({ message: 'Pet added successfully', id: results.insertId });
  });
});

app.post('/softDeletePet', function (req, res) {
  // เปลี่ยนชื่อ deleted_at เป็น deletedAt เพื่อป้องกันการ redeclare
  const { pet_id, deleted_at: deletedAt } = req.body;

  if (!pet_id || !deletedAt) {
      console.error("Missing parameters:", { pet_id, deletedAt });
      return res.status(400).send({ message: "Missing required parameters" });
  }

  // ใช้เครื่องหมายอัญประกาศให้ถูกต้องใน SQL Query
  const query = "UPDATE pets SET deleted_at = ? WHERE Pet_id = ?";

  dbConn.query(query, [deletedAt, pet_id], function (error, results) {
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


app.use(express.json());

app.get('/getPetTypes', function (req, res) {
  dbConn.query('SELECT Pet_type_id, Pet_name_type FROM pet_type', function (error, results) {
      if (error) {
          return res.status(500).send({ error: true, message: "Database query failed", details: error });
      }
      return res.json(results);
  });
});

app.put('/updatePet/:id', (req, res) => {
  console.log("Received Data from Android:", req.body); // ✅ Debug log

  const petID = req.params.id;
  const { petName, petGender, petBreed, petAge, petWeight, additionalInfo, Pet_type_id } = req.body;

  // ตรวจสอบค่าที่ได้รับ
  if (!petID || !petName || !petGender || !petBreed || !petAge || !petWeight || !additionalInfo || !Pet_type_id) {
      console.log("❌ Missing Data:", { petID, petName, petGender, petBreed, petAge, petWeight, additionalInfo, Pet_type_id });
      return res.status(400).json({ error: true, message: "Missing required fields", received: req.body });
  }

  const query = `UPDATE pets SET 
      Pet_name = ?, Pet_Gender = ?, Pet_breed = ?, Pet_age = ?, Pet_weight = ?, 
      additional_info = ?, Pet_type_id = ?, updated_at = NOW() 
      WHERE Pet_id = ?`;

  dbConn.query(query, [petName, petGender, petBreed, petAge, petWeight, additionalInfo, Pet_type_id, petID], (error, results) => {
      if (error) {
          console.error("Database error:", error);
          return res.status(500).json({ error: true, message: "Database update failed", details: error });
      }
      if (results.affectedRows === 0) {
          return res.status(404).json({ message: "Pet ID not found" });
      }
      res.json({ message: "Pet updated successfully" });
  });
});

app.get('/getPet/:id', (req, res) => {
  const petID = req.params.id;
  const query = "SELECT * FROM pets WHERE Pet_id = ?";

  dbConn.query(query, [petID], (error, results) => {
      if (error) {
          return res.status(500).json({ error: true, message: "Database query failed", details: error });
      }
      if (results.length === 0) {
          return res.status(404).json({ message: "Pet not found" });
      }
      res.json(results[0]); // ส่งข้อมูลสัตว์เลี้ยงที่เจอ
  });
});


app.get('/getroom', (req, res) => {
  let query = `
      SELECT
          r.room_id,
          rt.name_type AS room_type,
          rt.price_per_day,
          r.status AS room_status,
          pt.Pet_name_type AS pet_type
      FROM rooms r
      JOIN room_type rt ON r.type_type_id = rt.type_id
      JOIN pet_type pt ON rt.pet_type  = pt.Pet_type_id
      WHERE r.deleted_at IS NULL
      AND rt.deleted_at IS NULL
      AND pt.deleted_at IS NULL;
  `;

  dbConn.query(query, (error, results) => {
      if (error) {
          console.error("Database Error:", error);
          return res.status(500).send({ error: true, message: "Internal Server Error" });
      }
      console.log("Sent all room data successfully");
      return res.json(results);
  });
});


//  เพิ่มข้อมูลห้องพัก
app.post('/addroom', function (req, res) {
  const { type_type_id, status } = req.body;
  if (!type_type_id || status === undefined) {
      return res.status(400).send({ message: 'Please provide room type ID and status' });
  }

  dbConn.query('SELECT name_type, price_per_day, image, Pet_type_id FROM room_type WHERE type_id = ?', [type_type_id], function (error, results) {
      if (error) {
          return res.status(500).send({ message: 'Database error', details: error });
      }
      if (results.length === 0) {
          return res.status(400).send({ message: 'Invalid room_type ID' });
      }

      const roomType = results[0];

      dbConn.query('SELECT Pet_nametype FROM pet_type WHERE Pet_type_id = ?', [roomType.pet_type], function (error, petResults) {
          if (error) {
              return res.status(500).send({ message: 'Database error', details: error });
          }
          const petType = petResults.length > 0 ? petResults[0].Pet_name_type : null;

          dbConn.query('INSERT INTO rooms (type_type_id, status) VALUES (?, ?)', [type_type_id, status], function (error, results) {
              if (error) {
                  return res.status(500).send({ message: 'Failed to insert room data', details: error });
              }
              return res.send({
                  message: 'Room added successfully',
                  id: results.insertId,
                  room_type: roomType.name_type,
                  pet_type: petType,
                  price_per_day: roomType.price_per_day,
                  image: roomType.image
              });
          });
      });
  });
});

//การจอง Admin

// ดึงข้อมูลการจองทั้งหมด (รวม soft delete)
app.get("/bookings", function (req, res) {
  dbConn.query("SELECT * FROM bookings WHERE deleted_at IS NULL", function (error, results) {
    if (error) throw error;
    return res.send(results);
  });
});

// อัปเดตข้อมูลการจอง
app.put("/bookings/:id", function (req, res) {
  var bookingData = req.body;
  var bookingId = req.params.id;

  // ตรวจสอบว่ามีข้อมูลที่ต้องอัปเดตหรือไม่
  if (!bookingData || Object.keys(bookingData).length === 0) {
    return res.status(400).send({ error: true, message: "Please provide booking data" });
  }
  dbConn.query(
    "UPDATE bookings SET ? WHERE booking_id = ? AND deleted_at IS NULL",
    [bookingData, bookingId],
    function (error, results) {
      if (error) throw error;
      // เช็กว่ามีข้อมูลถูกอัปเดตจริงหรือไม่
      if (results.affectedRows === 0) {
        return res.status(404).send({ error: true, message: "Booking not found or already deleted" });
      }
      return res.send({ message: "Booking updated successfully" });
    }
  );
});


// Soft Delete การจอง
app.delete("/bookings/:id", function (req, res) {
  const bookingId = req.params.id;
  const deletedAt = new Date().toISOString().slice(0, 19).replace("T", " "); // เวลาปัจจุบัน

  dbConn.query(
    `UPDATE bookings SET deleted_at = ?
    WHERE booking_id = ? AND deleted_at IS NULL`,
    [deletedAt, bookingId],
    function (error, results) {
      if (error) throw error;
      return res.send({ message: "Booking soft deleted successfully" });
    }
  );
});

app.listen(3000, function () {
  console.log("Node app is running on port 3000");
});

module.exports = app;
