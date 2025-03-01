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
            VALUES('${name}','${password_hash}','${tell_number}','${email}', 2)`;
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

  console.log(name)
  console.log(password)

  if(!name || !password){
      return res.status(400).send({ error: name, message: 'Please provide name and password' })
  }

  dbConn.query('SELECT * FROM users WHERE email = ? OR tell_number = ? ',[name, name],function(error,results,fields){
      if(error) throw error
      if(results[0]){
          bcrypt.compare(password,results[0].password,function(err,result){
              if(err) throw err
              if(result){
                  return res.send({ "success": 1,"name":results[0].name,"user_type":results[0].user_type,"user_id":results[0].user_id, "email":results[0].email, "tell":result.tell_number })
              }else{
                  console.log("wongpass")
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

    //const userId = req.session.userId; // ดึง `userId` จาก session

    const query = `
        SELECT pets.pet_id, pets.user_id, pets.pet_name, pets.pet_age, pets.pet_breed,
        pets.pet_weight, pets.pet_gender, pets.additional_info, pet_type.pet_name_type, pets.deleted_at
        FROM pets
        INNER JOIN pet_type ON pets.pet_type_id = pet_type.pet_type_id
        WHERE pets.deleted_at IS NULL;
    `; //WHERE pets.User_id = ?
    dbConn.query(query, function (error, results, fields) {
        if (error) {
            return res.status(500).send({ error: true, message: 'Database query failed', details: error });
        }
        return res.send(results);
    });
});


app.get('/mypet/:id', function (req, res) {
    let user_id = req.params.id;
    const query = `
        SELECT pets.pet_id, pets.user_id, pets.pet_name, pets.pet_age, pets.pet_breed,
        pets.pet_weight, pets.pet_gender, pets.additional_info, pet_type.pet_name_type, pets.deleted_at
        FROM pets
        INNER JOIN pet_type ON pets.pet_type_id = pet_type.pet_type_id
        WHERE pets.deleted_at IS NULL AND  pets.user_id = ?;
    `; //WHERE pets.User_id = ?
    dbConn.query(query,[user_id] , function (error, results, fields) {
        if (error) {
            return res.status(500).send({ error: true, message: 'Database query failed', details: error });
        }
        return res.send(results);
    });
});

app.put('/updatePet/:id', (req, res) => {
    console.log("Received Data from Android:", req.body);
    const petID = req.params.id;

    // แก้ไขการดึงค่าจาก req.body ให้ตรงกับที่ Android ส่งมา
    const petName = req.body.pet_name;
    const petGender = req.body.pet_gender;
    const petBreed = req.body.pet_breed;
    const petAge = req.body.pet_age;
    const petWeight = req.body.pet_weight;
    const additionalInfo = req.body.additional_info;
    const petTypeId = req.body.pet_type_id;

    // Check for missing data
    if (!petID || !petName || !petGender || !petBreed || !petAge || !petWeight || !additionalInfo || !petTypeId) {
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
        return res.status(400).json({
            error: true,
            message: "Missing required fields",
            received: req.body
        });
    }

    // SQL query ยังคงเหมือนเดิม
    const query = `UPDATE pets SET
        pet_name = ?,
        pet_gender = ?,
        pet_breed = ?,
        pet_age = ?,
        pet_weight = ?,
        additional_info = ?,
        pet_type_id = ?,
        updated_at = NOW()
        WHERE pet_id = ?`;

    // Execute the query
    dbConn.query(query, [
        petName,
        petGender,
        petBreed,
        petAge,
        petWeight,
        additionalInfo,
        petTypeId,
        petID
    ], (error, results) => {
        if (error) {
            console.error("Database error:", error);
            return res.status(500).json({
                error: true,
                message: "Database update failed",
                details: error
            });
        }
        if (results.affectedRows === 0) {
            return res.status(404).json({ message: "Pet ID not found" });
        }
        res.json({ message: "Pet updated successfully" });
    });
});


// 📌 เพิ่มข้อมูลสัตว์เลี้ยง
app.post('/pet', function (req, res) {
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


app.post('/softDeletePet', function (req, res) {
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


app.use(express.json());

app.get('/getPetTypes', function (req, res) {
    dbConn.query('SELECT pet_type_id, pet_name_type FROM pet_type', function (error, results) {
        if (error) {
            return res.status(500).send({ error: true, message: "Database query failed", details: error });
        }
        return res.json(results);
    });
});


app.post('/addPetType', function (req, res) {
    const petType = {
        pet_name_type: req.body.pet_name_type
    };

    // ตรวจสอบว่ามีชื่อประเภทสัตว์เลี้ยงส่งมาหรือไม่
    if (!petType.pet_name_type) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุชื่อประเภทสัตว์เลี้ยง"
        });
    }

    // ตรวจสอบว่ามีประเภทสัตว์เลี้ยงนี้อยู่แล้วหรือไม่
    dbConn.promise().query(
        'SELECT * FROM pet_type WHERE pet_name_type = ? AND deleted_at IS NULL',
        [petType.pet_name_type]
    ).then(function ([results]) {
        if (results.length > 0) {
            return res.status(400).send({
                error: true,
                message: "มีประเภทสัตว์เลี้ยงนี้อยู่แล้ว"
            });
        }

        // เพิ่มประเภทสัตว์เลี้ยงใหม่
        dbConn.promise().query(
            'INSERT INTO pet_type (pet_name_type) VALUES (?)',
            [petType.pet_name_type]
        ).then(function ([insertResult]) {
            // ดึงข้อมูลที่เพิ่มเข้าไปใหม่
            dbConn.promise().query(
                'SELECT * FROM pet_type WHERE pet_type_id = ?',
                [insertResult.insertId]
            ).then(function ([newPetType]) {
                return res.status(201).send({
                    error: false,
                    message: "เพิ่มประเภทสัตว์เลี้ยงสำเร็จ",
                    petType: newPetType[0]
                });
            }).catch(function (error) {
                return res.status(500).send({
                    error: true,
                    message: "เพิ่มข้อมูลสำเร็จแต่ไม่สามารถดึงข้อมูลได้",
                    details: error
                });
            });
        }).catch(function (error) {
            return res.status(500).send({
                error: true,
                message: "เกิดข้อผิดพลาดในการเพิ่มประเภทสัตว์เลี้ยง",
                details: error
            });
        });
    }).catch(function (error) {
        return res.status(500).send({
            error: true,
            message: "เกิดข้อผิดพลาดในการตรวจสอบประเภทสัตว์เลี้ยง",
            details: error
        });
    });
});

// อัพเดทประเภทสัตว์เลี้ยง
app.put('/updatePetType/:id', function(req, res) {
    const petTypeId = req.params.id;
    const updateData = {
        pet_name_type: req.body.pet_name_type,
        updated_at: new Date()
    };

    if (!updateData.pet_name_type) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุชื่อประเภทสัตว์เลี้ยง"
        });
    }

    dbConn.query(
        'UPDATE pet_type SET ? WHERE pet_type_id = ? AND deleted_at IS NULL',
        [updateData, petTypeId],
        function(error, results) {
            if (error) {
                return res.status(500).send({
                    error: true,
                    message: "เกิดข้อผิดพลาดในการอัพเดทประเภทสัตว์เลี้ยง",
                    details: error
                });
            }

            if (results.affectedRows === 0) {
                return res.status(404).send({
                    error: true,
                    message: "ไม่พบประเภทสัตว์เลี้ยงที่ต้องการอัพเดท"
                });
            }

            return res.send({
                error: false,
                message: "อัพเดทประเภทสัตว์เลี้ยงสำเร็จ"
            });
        }
    );
});

// ลบประเภทสัตว์เลี้ยง (Soft Delete)
app.delete('/deletePetType/:id', function(req, res) {
    const petTypeId = req.params.id;
    const updateData = {
        deleted_at: new Date()
    };

    // ตรวจสอบว่ามีสัตว์เลี้ยงที่ใช้ประเภทนี้อยู่หรือไม่
    dbConn.query(
        'SELECT COUNT(*) as count FROM pets WHERE pet_type_id = ? AND deleted_at IS NULL',
        [petTypeId],
        function(error, results) {
            if (error) {
                return res.status(500).send({
                    error: true,
                    message: "เกิดข้อผิดพลาดในการตรวจสอบการใช้งานประเภทสัตว์เลี้ยง",
                    details: error
                });
            }

            if (results[0].count > 0) {
                return res.status(400).send({
                    error: true,
                    message: "ไม่สามารถลบประเภทสัตว์เลี้ยงนี้ได้เนื่องจากมีสัตว์เลี้ยงใช้งานอยู่"
                });
            }

            dbConn.query(
                'UPDATE pet_type SET ? WHERE pet_type_id = ? AND deleted_at IS NULL',
                [updateData, petTypeId],
                function(error, results) {
                    if (error) {
                        return res.status(500).send({
                            error: true,
                            message: "เกิดข้อผิดพลาดในการลบประเภทสัตว์เลี้ยง",
                            details: error
                        });
                    }

                    if (results.affectedRows === 0) {
                        return res.status(404).send({
                            error: true,
                            message: "ไม่พบประเภทสัตว์เลี้ยงที่ต้องการลบ"
                        });
                    }

                    return res.send({
                        error: false,
                        message: "ลบประเภทสัตว์เลี้ยงสำเร็จ"
                    });
                }
            );
        }
    );
});

app.get('/getPet/:id', (req, res) => {
    const petID = req.params.id;
    const query = "SELECT * FROM pets WHERE pet_id = ?";

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

 


app.post('/addroom', async (req, res) => {
    const { room_type_id, room_status } = req.body;

    // Input validation
    if (!room_type_id || room_status === undefined) {
        return res.status(400).json({
            error: true,
            message: 'กรุณาระบุข้อมูลให้ครบถ้วน'
        });
    }

    try {
        // Check if room type exists and fetch its details
        const [roomTypeResults] = await dbConn.promise().query(
            'SELECT name_type, price_per_day, image, pet_type FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
            [room_type_id]
        );

        if (roomTypeResults.length === 0) {
            return res.status(400).json({
                error: true,
                message: 'ไม่พบประเภทห้องพักที่ระบุ'
            });
        }

        const roomType = roomTypeResults[0];

        // Fetch pet type name
        const [petTypeResults] = await dbConn.promise().query(
            'SELECT pet_name_type FROM pet_type WHERE pet_type_id = ? AND deleted_at IS NULL',
            [roomType.pet_type]
        );

        const petType = petTypeResults.length > 0 ? petTypeResults[0].pet_name_type : null;

        // Insert new room
        const [insertResult] = await dbConn.promise().query(
            'INSERT INTO rooms (type_type_id, status) VALUES (?, ?)',
            [room_type_id, room_status || null]
        );

        // Directly use the insertResult.insertId without querying again
        const roomId = insertResult.insertId;

        // Prepare response
        return res.status(201).json({
            error: false,
            message: 'เพิ่มห้องสำเร็จ',
            room: {
                id: roomId,
                room_type: roomType.name_type,
                pet_type: petType,
                price_per_day: roomType.price_per_day,
                image: roomType.image,
                status: room_status
            }
        });

    } catch (error) {
        console.error('Error in /addroom:', error);
        return res.status(500).json({
            error: true,
            message: 'เกิดข้อผิดพลาดในการเพิ่มห้อง',
            details: error.message
        });
    }
});


//const bcrypt = require('bcryptjs');


// Soft delete a room
app.post('/softDeleteRoom', function (req, res) {
    const { room_id, deleted_at } = req.body;

    if (!room_id || !deleted_at) {
        console.error("Missing parameters:", { room_id, deleted_at });
        return res.status(400).send({ message: "Missing required parameters" });
    }

    const query = `UPDATE rooms SET deleted_at = ? WHERE room_id = ?`;

    dbConn.query(query, [deleted_at, room_id], function (error, results) {
        if (error) {
            console.error("Database error:", error);
            return res.status(500).send({ error: true, message: "Database update failed", details: error });
        }
        if (results.affectedRows === 0) {
            return res.status(404).send({ message: "Room ID not found" });
        }
        return res.send({ message: "Soft delete successful" });
    });
});


app.get('/getRoomTypes', function (req, res) {
    dbConn.query('SELECT type_id, name_type FROM room_type', function (error, results) {
        if (error) {
            return res.status(500).send({ error: true, message: "Database query failed", details: error });
        }
        return res.json(results);
    });
});




app.post('/addRoomType', function (req, res) {
    const roomType = {
        name_type: req.body.name_type,
        price_per_day: req.body.price_per_day,
        pet_type: req.body.pet_type,
        image: req.body.image // ค่าภาพที่ส่งมาใน Base64
    };

    // เช็คค่า Base64 ที่ได้รับจาก Client
    console.log("Received Base64 Image:", req.body.image);

    // ตรวจสอบว่ามีชื่อประเภทห้องพักส่งมาหรือไม่
    if (!roomType.name_type || !roomType.price_per_day || !roomType.pet_type) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุชื่อประเภทห้องพัก ราคาต่อวัน และประเภทสัตว์เลี้ยง"
        });
    }

    // ตรวจสอบว่ามีประเภทห้องพักนี้อยู่แล้วหรือไม่
    dbConn.promise().query(
        'SELECT * FROM room_type WHERE name_type = ? AND deleted_at IS NULL',
        [roomType.name_type]
    ).then(function ([results]) {
        if (results.length > 0) {
            return res.status(400).send({
                error: true,
                message: "มีประเภทห้องพักนี้อยู่แล้ว"
            });
        }

        let imagePath = null;
        if (roomType.image) {
            // แปลง Base64 เป็นไฟล์
            const base64Data = roomType.image.replace(/^data:image\/\w+;base64,/, ""); // ลบ header
            const buffer = Buffer.from(base64Data, 'base64');
            imagePath = path.join(__dirname, 'uploads', `room_${Date.now()}.jpg`);
            console.log("Base64 Image: ", roomType.image);


            // บันทึกภาพลงในโฟลเดอร์ uploads
            fs.writeFile(imagePath, buffer, function (err) {
                if (err) {
                    return res.status(500).send({
                        error: true,
                        message: "ไม่สามารถบันทึกภาพได้",
                        details: err
                    });
                }

                // ใช้ sharp เพื่อจัดการกับขนาดของภาพ (ย่อภาพ, เปลี่ยนรูปแบบ)
                sharp(imagePath)
                    .resize(800, 600) // ปรับขนาดภาพ (สามารถปรับตามต้องการ)
                    .toFile(path.join(__dirname, 'uploads', `room_${Date.now()}_small.jpg`), (err, info) => {
                        if (err) {
                            return res.status(500).send({
                                error: true,
                                message: "ไม่สามารถย่อขนาดภาพได้",
                                details: err
                            });
                        }
                    });
            });
        }

        // เพิ่มประเภทห้องพักใหม่
        dbConn.promise().query(
            'INSERT INTO room_type (name_type, price_per_day, pet_type, image) VALUES (?, ?, ?, ?)',
            [roomType.name_type, roomType.price_per_day, roomType.pet_type, imagePath] // บันทึก path ของภาพ
        ).then(function ([insertResult]) {
            // ดึงข้อมูลที่เพิ่มเข้าไปใหม่
            dbConn.promise().query(
                'SELECT * FROM room_type WHERE type_id = ?',
                [insertResult.insertId]
            ).then(function ([newRoomType]) {
                return res.status(201).send({
                    error: false,
                    message: "เพิ่มประเภทห้องพักสำเร็จ",
                    roomType: newRoomType[0]
                });
            }).catch(function (error) {
                return res.status(500).send({
                    error: true,
                    message: "เพิ่มข้อมูลสำเร็จแต่ไม่สามารถดึงข้อมูลได้",
                    details: error
                });
            });
        }).catch(function (error) {
            return res.status(500).send({
                error: true,
                message: "เกิดข้อผิดพลาดในการเพิ่มประเภทห้องพัก",
                details: error
            });
        });

    }).catch(function (error) {
        return res.status(500).send({
            error: true,
            message: "เกิดข้อผิดพลาดในการตรวจสอบประเภทห้องพัก",
            details: error
        });
    });
});

const fs = require('fs');
const path = require('path');
const sharp = require('sharp'); // ใช้สำหรับจัดการรูปภาพ (เช่น การย่อขนาด)


app.get('/updateroomtype/:room_type_id', async (req, res) => {
    const { room_type_id } = req.params;  // Get room_type_id from URL parameters

    try {
        // Query the database to fetch the room type by room_type_id
        const [roomResults] = await dbConn.promise().query(
            'SELECT * FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
            [room_type_id]
        );

        // Check if the room type exists
        if (roomResults.length === 0) {
            return res.status(404).json({
                error: true,
                message: 'ไม่พบห้องที่ระบุ'  // "Room not found"
            });
        }

        // Room type found, return the first result (room type data)
        const roomtype = roomResults[0];
        console.log("Fetched roomtype:", roomtype);  // Log room details for debugging (remove in production)

        // Return the room type data
        return res.json(roomtype);

    } catch (error) {
        // Catch any errors and send a 500 Internal Server Error
        console.error("Error fetching room type:", error);
        return res.status(500).json({
            error: true,
            message: 'เกิดข้อผิดพลาดในการดึงข้อมูลห้องพัก',  // "Error fetching room data"
            details: error.message
        });
    }
});


app.put('/updateRoomType/:room_type_id', function (req, res) {
    const room_type_id = req.params.room_type_id; // รับ room_id จาก URL parameter
    const roomType = {
        name_type: req.body.name_type,
        price_per_day: req.body.price_per_day,
        pet_type: req.body.pet_type,
        image: req.body.image // ค่าภาพที่ส่งมาใน Base64
    };

    // เช็คค่า Base64 ที่ได้รับจาก Client
    console.log("Received Base64 Image:", req.body.image);

    // ตรวจสอบว่ามีชื่อประเภทห้องพักส่งมาหรือไม่
    if (!roomType.name_type || !roomType.price_per_day || !roomType.pet_type) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุชื่อประเภทห้องพัก ราคาต่อวัน และประเภทสัตว์เลี้ยง"
        });
    }

    // ตรวจสอบว่ามีประเภทห้องพักนี้อยู่แล้วหรือไม่
    dbConn.promise().query(
        'SELECT * FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
        [room_type_id]
    ).then(function ([results]) {
        if (results.length === 0) {
            return res.status(404).send({
                error: true,
                message: "ไม่พบประเภทห้องพักที่ต้องการอัปเดต"
            });
        }

        let imagePath = results[0].image; // หากไม่มีการอัปโหลดภาพใหม่ ให้ใช้ภาพเดิม
        if (roomType.image) {
            // แปลง Base64 เป็นไฟล์
            const base64Data = roomType.image.replace(/^data:image\/\w+;base64,/, ""); // ลบ header
            const buffer = Buffer.from(base64Data, 'base64');
            imagePath = path.join(__dirname, 'uploads', `room_${Date.now()}.jpg`);
            console.log("Base64 Image: ", roomType.image);

            // บันทึกภาพลงในโฟลเดอร์ uploads
            fs.writeFile(imagePath, buffer, function (err) {
                if (err) {
                    return res.status(500).send({
                        error: true,
                        message: "ไม่สามารถบันทึกภาพได้",
                        details: err
                    });
                }

                // ใช้ sharp เพื่อจัดการกับขนาดของภาพ (ย่อภาพ, เปลี่ยนรูปแบบ)
                sharp(imagePath)
                    .resize(800, 600) // ปรับขนาดภาพ (สามารถปรับตามต้องการ)
                    .toFile(path.join(__dirname, 'uploads', `room_${Date.now()}_small.jpg`), (err, info) => {
                        if (err) {
                            return res.status(500).send({
                                error: true,
                                message: "ไม่สามารถย่อขนาดภาพได้",
                                details: err
                            });
                        }
                    });
            });
        }

        // อัปเดตประเภทห้องพัก
        dbConn.promise().query(
            'UPDATE room_type SET name_type = ?, price_per_day = ?, pet_type = ?, image = ? WHERE type_id = ?',
            [roomType.name_type, roomType.price_per_day, roomType.pet_type, imagePath, room_type_id] // อัปเดตข้อมูล
        ).then(function () {
            // ดึงข้อมูลที่อัปเดตแล้ว
            dbConn.promise().query(
                'SELECT * FROM room_type WHERE type_id = ?',
                [room_type_id]
            ).then(function ([updatedRoomType]) {
                return res.status(200).send({
                    error: false,
                    message: "อัปเดตประเภทห้องพักสำเร็จ",
                    roomType: updatedRoomType[0]
                });
            }).catch(function (error) {
                return res.status(500).send({
                    error: true,
                    message: "อัปเดตข้อมูลสำเร็จแต่ไม่สามารถดึงข้อมูลได้",
                    details: error
                });
            });
        }).catch(function (error) {
            return res.status(500).send({
                error: true,
                message: "เกิดข้อผิดพลาดในการอัปเดตประเภทห้องพัก",
                details: error
            });
        });

    }).catch(function (error) {
        return res.status(500).send({
            error: true,
            message: "เกิดข้อผิดพลาดในการตรวจสอบประเภทห้องพัก",
            details: error
        });
    });
});


app.get('/updateroom/:room_id', async (req, res) => {
    const { room_id } = req.params;

    // Query the database to fetch the room by room_id
    const [roomResults] = await dbConn.promise().query(
        'SELECT * FROM rooms WHERE room_id = ? AND deleted_at IS NULL',
        [room_id]
    );

    if (roomResults.length === 0) {
        return res.status(404).json({
            error: true,
            message: 'ไม่พบห้องที่ระบุ'
        });
    }

    const room = roomResults[0];

    console.log("Fetched room:", room); // Log room details for debugging
    return res.json(room); // Return room data (including room_type_id)
});

app.put('/updateroom/:room_id', async (req, res) => {
    const { room_type_id, room_status } = req.body;
    const room_id = req.params.room_id;  // รับ room_id จาก URL parameter

    // Input validation
    if (!room_type_id || room_status === undefined || !room_id) {
        return res.status(400).json({
            error: true,
            message: 'กรุณาระบุข้อมูลให้ครบถ้วน'
        });
    }

    try {
        // Check if room exists and fetch its details
        const [roomResults] = await dbConn.promise().query(
            'SELECT * FROM rooms WHERE room_id = ? AND deleted_at IS NULL',
            [room_id]
        );

        if (roomResults.length === 0) {
            return res.status(400).json({
                error: true,
                message: 'ไม่พบห้องที่ระบุ'
            });
        }

        // Check if room type exists and fetch its details
        const [roomTypeResults] = await dbConn.promise().query(
            'SELECT name_type, price_per_day, image, pet_type FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
            [room_type_id]
        );

        if (roomTypeResults.length === 0) {
            return res.status(400).json({
                error: true,
                message: 'ไม่พบประเภทห้องพักที่ระบุ'
            });
        }

        const roomType = roomTypeResults[0];

        // Fetch pet type name
        const [petTypeResults] = await dbConn.promise().query(
            'SELECT pet_name_type FROM pet_type WHERE pet_type_id = ? AND deleted_at IS NULL',
            [roomType.pet_type]
        );

        const petType = petTypeResults.length > 0 ? petTypeResults[0].pet_name_type : null;

        // Update the room
        const [updateResult] = await dbConn.promise().query(
            'UPDATE rooms SET type_type_id = ?, status = ? WHERE room_id = ?',
            [room_type_id, room_status || null, room_id]
        );

        // Fetch the updated room details
        const [updatedRoomResults] = await dbConn.promise().query(
            'SELECT room_id FROM rooms WHERE room_id = ?',
            [room_id]
        );

        // Prepare response
        return res.status(200).json({
            error: false,
            message: 'อัปเดตห้องสำเร็จ',
            room: {
                id: room_id,
                room_type: roomType.name_type,
                pet_type: petType,
                price_per_day: roomType.price_per_day,
                image: roomType.image,
                status: room_status
            }
        });

    } catch (error) {
        console.error('Error in /updateroom:', error);
        return res.status(500).json({
            error: true,
            message: 'เกิดข้อผิดพลาดในการอัปเดตห้อง',
            details: error.message
        });
    }
});


//การจอง Admin

// ดึงข้อมูลการจองทั้งหมด พร้อมข้อมูลสัตว์เลี้ยงและเจ้าของ
app.get("/bookings", function (req, res) {
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
    // WHERE bookings.deleted_at IS NULL


  dbConn.query(query, function (error, results) {
    if (error) throw error;
    console.log("Sent all Booking data successfully");
    return res.send(results);
  });
});

// ดึงข้อมูลการจองตาม ID พร้อมข้อมูลสัตว์เลี้ยงและเจ้าของ
app.get("/bookings/:id", function (req, res) {
  const bookingId = req.params.id;

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
    WHERE bookings.booking_id = ? AND bookings.deleted_at IS NULL`;

  dbConn.query(query, [bookingId], function (error, results) {
    if (error) throw error;
    if (results.length === 0) {
      return res.status(404).send({ error: true, message: "Booking not found" });
    }
    return res.send(results[0]);
  });
});


// อัปเดตข้อมูลการจอง(ทั้งหมด)
app.put("/bookings/update/:id", function (req, res) {
  var bookingData = req.body;
  var bookingId = req.params.id;

  if (!bookingData || Object.keys(bookingData).length === 0) {
    return res.status(400).send({ error: true, message: "Please provide booking data" });
  }
  dbConn.query(
    "UPDATE bookings SET ? WHERE booking_id = ? AND deleted_at IS NULL",
    [bookingData, bookingId],
    function (error, results) {
      if (error) throw error;
      if (results.affectedRows === 0) {
        return res.status(404).send({ error: true, message: "Booking not found or already deleted" });
      }
      return res.send({ message: "Booking updated successfully" });
    }
  );
});

// การจอง Update status booking, rooms
app.put("/bookings/status/:id", function (req, res) {
    const bookingId = req.params.id;
    const booking_status = req.body.booking_status;

    if (booking_status === undefined) {
        return res.status(400).json({
        error: true,
        message: "Please provide booking status",
        receivedBody: req.body,
        });
    }

    // เริ่มต้นการทำงานใน callback
    dbConn.query(
        "UPDATE bookings SET booking_status = ? WHERE booking_id = ? AND deleted_at IS NULL",
        [booking_status, bookingId],
        function (err, bookingUpdateResult) {
            if (err) {
                console.error("Database error:", err);
                return res.status(500).json({ error: true, message: err.message });
            }

            if (bookingUpdateResult.affectedRows === 0) {
                return res.status(404).json({ error: true, message: "Booking not found or already deleted" });
            }

            // ดึง room_id ของการจองนี้
            dbConn.query(
                "SELECT room_id FROM bookings WHERE booking_id = ?",
                [bookingId],
                function (err, roomResult) {
                    if (err) {
                        console.error("Database error:", err);
                        return res.status(500).json({ error: true, message: err.message });
                    }

                    if (roomResult.length === 0) {
                        return res.status(404).json({ error: true, message: "Room not found for this booking" });
                    }

                    const roomId = roomResult[0].room_id;
                    let room_status = null;

                    // กำหนดค่า room_status ตาม booking_status
                    if (booking_status == 0) {
                        room_status = 0; // ยังไม่เช็คอิน -> ไม่ว่าง
                    } else if (booking_status == 1) {
                        room_status = 0; // เช็คอินแล้ว -> ไม่ว่าง
                    } else if (booking_status == 2) {
                        room_status = 2; // เช็คเอาท์แล้ว -> ทำความสะอาด
                    } else if (booking_status == 3) {
                        room_status = 1; // ยกเลิก -> ว่าง
                    }

                    if (room_status !== null) {
                        dbConn.query(
                            "UPDATE rooms SET status = ? WHERE room_id = ?",
                            [room_status, roomId],
                            function (err) {
                                if (err) {
                                    console.error("Database error:", err);
                                    return res.status(500).json({ error: true, message: err.message });
                                }

                                return res.json({ message: "Booking status and Room status updated successfully" });
                            }
                        );
                    } else {
                        return res.json({ message: "Booking status updated successfully, but no room status changed" });
                    }
                }
            );
        }
    );
});


// API สำหรับขยายเวลาการเข้าพัก
app.put("/bookings/extend/:id", function (req, res) {
    const bookingId = req.params.id;
    const { days, additionalCost } = req.body;

    // ตรวจสอบข้อมูลที่จำเป็น
    if (days === undefined || additionalCost === undefined) {
        return res.status(400).json({
            error: true,
            message: "Please provide days and additional cost",
            receivedBody: req.body,
        });
    }

    // ดึงข้อมูลการจองปัจจุบัน
    dbConn.query(
        "SELECT check_out, adjust, total_pay FROM bookings WHERE booking_id = ? AND deleted_at IS NULL",
        [bookingId],
        function (err, results) {
            if (err) {
                console.error("Database error:", err);
                return res.status(500).json({ error: true, message: err.message });
            }

            if (results.length === 0) {
                return res.status(404).json({
                    error: true,
                    message: "Booking not found or already deleted"
                });
            }

            const currentBooking = results[0];

            // คำนวณวันที่ check_out ใหม่
            const currentCheckOut = new Date(currentBooking.check_out);
            const newCheckOut = new Date(currentCheckOut.setDate(currentCheckOut.getDate() + parseInt(days)));

            // คำนวณค่าใช้จ่ายรวมใหม่
            const newAdjust = (currentBooking.adjust || 0) + parseInt(additionalCost);
            const newTotalPay = (currentBooking.total_pay || 0) + parseInt(additionalCost);

            // อัพเดทข้อมูลในฐานข้อมูล
            dbConn.query(
                "UPDATE bookings SET check_out = ?, adjust = ?, total_pay = ? WHERE booking_id = ? AND deleted_at IS NULL",
                [newCheckOut, newAdjust, newTotalPay, bookingId],
                function (err, updateResult) {
                    if (err) {
                        console.error("Database error:", err);
                        return res.status(500).json({ error: true, message: err.message });
                    }

                    if (updateResult.affectedRows === 0) {
                        return res.status(404).json({
                            error: true,
                            message: "Failed to update booking"
                        });
                    }

                    // ส่งข้อมูลที่อัพเดทกลับไป
                    return res.json({
                        message: "Booking extended successfully",
                        data: {
                            bookingId,
                            newCheckOut,
                            additionalCost,
                            newTotalPay
                        }
                    });
                }
            );
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


//โปรไฟล์ Backend
app.get("/profile/:id", function (req, res) {
  const userId = req.params.id;
  const query = `SELECT * FROM users WHERE user_id = ?`;

  dbConn.query(query, [userId], function (error, results) {
    if (error) throw error;
    if (results.length === 0) {
      return res.status(404).send({ error: true, message: "user data not found" });
    }
    console.log("User data "+userId+" get successfully")
    return res.send(results[0]);
  });
});

//แก้ไขโปรไฟล์
app.put("/profile/edit/:id", function (req, res) {
    const userId = req.params.id;
    const { name, email, tell_number, avatar } = req.body;
  
    // ตรวจสอบค่าที่จำเป็น
    if (!name || !email || !tell_number || avatar === undefined) {
      return res.status(400).json({
        error: true,
        message: "Please provide name, email, tell_number, and avatar",
      });
    }
  
    const query = `UPDATE users 
                   SET name = ?, email = ?, tell_number = ?, avatar = ?
                   WHERE user_id = ? AND deleted_at IS NULL`;
  
    dbConn.query(query, [name, email, tell_number, avatar, userId], function (error, results) {
      if (error) {
        console.error("Database error:", error);
        return res.status(500).json({ error: true, message: error.message });
      }
  
      if (results.affectedRows === 0) {
        return res.status(404).json({ error: true, message: "User not found or already deleted" });
      }
  
      console.log("User data " + name + " updated successfully");
      return res.json({ message: "User profile updated successfully" });
    });
  });
  

app.listen(3000, function () {
  console.log("Node app is running on port 3000");
});

module.exports = app;