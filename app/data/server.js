var express = require("express");
var app = express();
var bodyParser = require("body-parser");
var fs = require("fs");
var path = require("path");
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
                  return res.send({ "success": 1,"name":results[0].name,"user_type":results[0].user_type,"user_id":results[0].user_id, "email":results[0].email, "tell_number":results[0].tell_number })
              }else{
                  console.log("wrongpass")
                  return res.send({ "success": 0 })
              }
          })
      }else{
          return res.send({ "success": 0 })
      }
  })
})


//✨
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

//✨
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

//✨
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


//✨
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

//✨
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
        // return res.send(results);
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
          rt.image,
          rt.price_per_day,
          r.status,
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

 

app.post('/addroom' , async (req, res) => {
    const { room_type_id, room_status } = req.body;


    
    // Input validation
    if (!room_type_id || room_status === undefined) {
        return res.status(400).json({
            error: true,
            message: 'กรุณาระบุข้อมูลให้ครบถ้วน'
        });
    }

    // Check if room type exists and fetch its details
    dbConn.query(
        'SELECT name_type, price_per_day, image, pet_type FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
        [room_type_id],
        function (error, roomTypeResults) {
            if (error) {
                return res.status(500).json({
                    error: true,
                    message: 'เกิดข้อผิดพลาดในการตรวจสอบประเภทห้องพัก',
                    details: error.message
                });
            }

            if (roomTypeResults.length === 0) {
                return res.status(400).json({
                    error: true,
                    message: 'ไม่พบประเภทห้องพักที่ระบุ'
                });
            }

            const roomType = roomTypeResults[0];

            // Fetch pet type name
            dbConn.query(
                'SELECT pet_name_type FROM pet_type WHERE pet_type_id = ? AND deleted_at IS NULL',
                [roomType.pet_type],
                function (error, petTypeResults) {
                    if (error) {
                        return res.status(500).json({
                            error: true,
                            message: 'เกิดข้อผิดพลาดในการตรวจสอบประเภทสัตว์เลี้ยง',
                            details: error.message
                        });
                    }

                    const petType = petTypeResults.length > 0 ? petTypeResults[0].pet_name_type : null;

                    // Insert new room
                    dbConn.query(
                        'INSERT INTO rooms (type_type_id, status) VALUES (?, ?)',
                        [room_type_id, room_status || null],
                        function (error, insertResult) {
                            if (error) {
                                return res.status(500).json({
                                    error: true,
                                    message: 'เกิดข้อผิดพลาดในการเพิ่มห้อง',
                                    details: error.message
                                });
                            }

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
                        }
                    );
                }
            );
        }
    );
    // try {
    //     // Check if room type exists and fetch its details
    //     const [roomTypeResults] = await dbConn.promise().query(
    //         'SELECT name_type, price_per_day, image, pet_type FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
    //         [room_type_id]
    //     );

    //     if (roomTypeResults.length === 0) {
    //         return res.status(400).json({
    //             error: true,
    //             message: 'ไม่พบประเภทห้องพักที่ระบุ'
    //         });
    //     }

    //     const roomType = roomTypeResults[0];

    //     // Fetch pet type name
    //     const [petTypeResults] = await dbConn.promise().query(
    //         'SELECT pet_name_type FROM pet_type WHERE pet_type_id = ? AND deleted_at IS NULL',
    //         [roomType.pet_type]
    //     );

    //     const petType = petTypeResults.length > 0 ? petTypeResults[0].pet_name_type : null;

    //     // Insert new room
    //     const [insertResult] = await dbConn.promise().query(
    //         'INSERT INTO rooms (type_type_id, status) VALUES (?, ?)',
    //         [room_type_id, room_status || null]
    //     );

    //     // Directly use the insertResult.insertId without querying again
    //     const roomId = insertResult.insertId;

    //     // Prepare response
    //     return res.status(201).json({
    //         error: false,
    //         message: 'เพิ่มห้องสำเร็จ',
    //         room: {
    //             id: roomId,
    //             room_type: roomType.name_type,
    //             pet_type: petType,
    //             price_per_day: roomType.price_per_day,
    //             image: roomType.image,
    //             status: room_status
    //         }
    //     });

    // } catch (error) {
    //     console.error('Error in /addroom:', error);
    //     return res.status(500).json({
    //         error: true,
    //         message: 'เกิดข้อผิดพลาดในการเพิ่มห้อง',
    //         details: error.message
    //     });
    // }
});


app.post('/softDeleteRoom', function (req, res) {
    const { room_id } = req.body; // กำหนดรับแค่ room_id เท่านั้น, `deleted_at` ใช้ `NOW()` ใน SQL

    // ตรวจสอบว่า `room_id` ถูกส่งมาหรือไม่
    if (!room_id) {
        console.error("Missing room_id:", { room_id });
        return res.status(400).send({ message: "Missing required room_id" });
    }

    const query = `UPDATE rooms SET deleted_at = NOW() WHERE room_id = ?`;

    dbConn.query(query, [room_id], function (error, results) {
        if (error) {
            console.error("Database error:", error);
            return res.status(500).send({ error: true, message: "Database update failed", details: error });
        }
        if (results.affectedRows === 0) {
            return res.status(404).send({ message: "Room ID not found" });
        }
        return res.send({ message: "Soft delete successful for room" });
    });
});




app.get('/getRoomTypes', function (req, res) {
    dbConn.query('SELECT type_id, name_type,price_per_day,pet_type,image FROM room_type WHERE deleted_at IS NULL', function (error, results) {
        if (error) {
            return res.status(500).send({ error: true, message: "Database query failed", details: error });
        }
        return res.json(results);
    });
});




const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir);
}


const multer = require('multer');
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
// Set up static folder
app.use(express.static("./public"));

// Set up body parser
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Set up multer for image upload
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/'); // specify upload folder
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname)); // use a unique filename
    }
});

const upload = multer({ storage: storage });

app.post('/addRoomType', upload.single('image'), function (req, res) {
    const fileImage = req.file;
    // If no file uploaded, return error
    if (!fileImage) {
        return res.status(400).send({
            error: true,
            message: "No file uploaded"
        });
    }

    const imagePath = fileImage.path; // Get the file path of the uploaded image

    const roomType = {
        name_type: req.body.name_type,
        price_per_day: req.body.price_per_day,
        pet_type: req.body.pet_type,
        image: imagePath // Save the path of the uploaded image
    };

    console.log("Received Image Path:", imagePath);
    console.log("Received Data:", req.body);
    console.log("Received File:", req.file);

    // Validate roomType fields
    if (!roomType.name_type || !roomType.price_per_day || !roomType.pet_type) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุชื่อประเภทห้องพัก ราคาต่อวัน และประเภทสัตว์เลี้ยง"
        });
    } 

    dbConn.query('SELECT * FROM room_type WHERE name_type = ? AND deleted_at IS NULL', [roomType.name_type], function (error, results) {
        if (error) {
            return res.status(500).send({
                error: true,
                message: "Database query failed",
                details: error
            });
        }

        if (results.length > 0) {
            return res.status(400).send({
                error: true,
                message: "มีประเภทห้องพักนี้อยู่แล้ว"
            });
        }

        dbConn.query('INSERT INTO room_type (name_type, price_per_day, pet_type, image) VALUES (?, ?, ?, ?)', [roomType.name_type, roomType.price_per_day, roomType.pet_type, imagePath], function (error, results) {
            if (error) {
                return res.status(500).send({
                    error: true,    
                    message: "เกิดข้อผิดพลาดในการเพิ่มประเภทห้องพัก",
                    details: error
                });
            }

            dbConn.query('SELECT * FROM room_type WHERE type_id = ?', [results.insertId], function (error, newRoomType) {
                if (error) {
                    return res.status(500).send({
                        error: true,
                        message: "เพิ่มข้อมูลสำเร็จแต่ไม่สามารถดึงข้อมูลได้",
                        details: error
                    });
                }

                return res.status(201).send({
                    error: false,
                    message: "เพิ่มประเภทห้องพักสำเร็จ",
                    roomType: newRoomType[0]
                });
            });
        });
    });
});







app.get('/updateroomtype/:room_type_id', function (req, res) {
    const room_type_id = req.params.room_type_id;  // Get room_type_id from URL parameters

    // Query the database to fetch the room type by room_type_id
    dbConn.query(
        'SELECT * FROM room_type WHERE type_id = ? AND deleted_at IS NULL',
        [room_type_id],
        function (error, roomResults) {
            // Check if there was an error with the query
            if (error) {
                console.error("Error fetching room type:", error);
                return res.status(500).json({
                    error: true,
                    message: 'เกิดข้อผิดพลาดในการดึงข้อมูลห้องพัก',  // "Error fetching room data"
                    details: error.message
                });
            }

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
        }
    );
});



app.use(bodyParser.json()); // ใช้สำหรับ解析 JSON body



app.put('/updateRoomType/:room_type_id', upload.single('image'), function (req, res) {
    const room_type_id = req.params.room_type_id;

    // Check if the image exists in the request
    const fileImage = req.file;
    let imagePath = req.body.image; // keep the existing image if no new one is uploaded

    // If a new image is uploaded, update the image path
    if (fileImage) {
        imagePath = fileImage.path; // Save the new image path
    }

    const roomType = {
        name_type: req.body.name_type,
        price_per_day: req.body.price_per_day,
        pet_type: req.body.pet_type,
        image: imagePath // Path to the image (new or old)
    };

    // Check if required fields are provided
    if (!roomType.name_type || !roomType.price_per_day || !roomType.pet_type) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุชื่อประเภทห้องพัก ราคาต่อวัน และประเภทสัตว์เลี้ยง"
        });
    }

    // Perform the database update
    dbConn.query(
        'UPDATE room_type SET name_type = ?, price_per_day = ?, pet_type = ?, image = ? WHERE type_id = ?',
        [roomType.name_type, roomType.price_per_day, roomType.pet_type, imagePath, room_type_id],
        function (error, results) {
            if (error) {
                console.error("Error during update:", error);
                return res.status(500).send({
                    error: true,
                    message: "เกิดข้อผิดพลาดในการอัปเดตประเภทห้องพัก",
                    details: error.message
                });
            }

            if (results.affectedRows === 0) {
                return res.status(404).send({
                    error: true,
                    message: "ไม่พบประเภทห้องที่ต้องการอัปเดต"
                });
            }

            res.status(200).send({
                error: false,
                message: "อัปเดตประเภทห้องพักสำเร็จ"
            });
        }
    );
});


// Soft delete a room type using NOW() for the deleted_at timestamp
app.post('/softDeleteRoomType', function (req, res) {
    const { room_type_id } = req.body; // 

    // Check if room_type_id is provided
    if (!room_type_id) {
        console.error("Missing room_type_id:", { room_type_id });
        return res.status(400).send({ message: "Missing required room_type_id" });
    }

    const query = `UPDATE room_type SET deleted_at = NOW() WHERE type_id = ?`;

    dbConn.query(query, [room_type_id], function (error, results) {
        if (error) {
            console.error("Database error:", error);
            return res.status(500).send({ error: true, message: "Database update failed", details: error });
        }
        if (results.affectedRows === 0) {
            return res.status(404).send({ message: "Room type ID not found" });
        }
        return res.send({ message: "Soft delete successful for room type" });
    });
});



app.get('/updateroom/:room_id', async (req, res) => {
    const { room_id } = req.params;

    // Query the database to fetch the room by room_id
    dbConn.query(
        'SELECT * FROM rooms WHERE room_id = ? AND deleted_at IS NULL',
        [room_id],
        function (error, roomResults) {
            if (error) {
                console.error("Database error:", error);
                return res.status(500).json({
                    error: true,
                    message: 'เกิดข้อผิดพลาดในการดึงข้อมูลห้องพัก',
                    details: error.message
                });
            }

            if (roomResults.length === 0) {
                return res.status(404).json({
                    error: true,
                    message: 'ไม่พบห้องที่ระบุ'
                });
            }

            const room = roomResults[0];
            console.log("Fetched room:", room); // Log room details for debugging
            return res.json(room); // Return room data (including room_type_id)
        }
    );
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

    // Check if room exists and fetch its details
    dbConn.query('SELECT * FROM rooms WHERE room_id = ? AND deleted_at IS NULL', [room_id], function (error, roomResults) {
        if (error) {
            console.error("Database error:", error);
            return res.status(500).json({ error: true, message: error.message });
        }

        if (roomResults.length === 0) {
            return res.status(400).json({ error: true, message: 'ไม่พบห้องที่ระบุ' });
        }

        // Check if room type exists and fetch its details
        dbConn.query('SELECT name_type, price_per_day, image, pet_type FROM room_type WHERE type_id = ? AND deleted_at IS NULL', [room_type_id], function (error, roomTypeResults) {
            if (error) {
                console.error("Database error:", error);
                return res.status(500).json({ error: true, message: error.message });
            }

            if (roomTypeResults.length === 0) {
                return res.status(400).json({ error: true, message: 'ไม่พบประเภทห้องพักที่ระบุ' });
            }

            const roomType = roomTypeResults[0];

            // Fetch pet type name
            dbConn.query('SELECT pet_name_type FROM pet_type WHERE pet_type_id = ? AND deleted_at IS NULL', [roomType.pet_type], function (error, petTypeResults) {
                if (error) {
                    console.error("Database error:", error);
                    return res.status(500).json({ error: true, message: error.message });
                }

                const petType = petTypeResults.length > 0 ? petTypeResults[0].pet_name_type : null;

                // Update the room
                dbConn.query('UPDATE rooms SET type_type_id = ?, status = ? WHERE room_id = ?', [room_type_id, room_status || null, room_id], function (error, updateResult) {
                    if (error) {
                        console.error("Database error:", error);
                        return res.status(500).json({ error: true, message: error.message });
                    }

                    // Fetch the updated room details
                    dbConn.query('SELECT room_id FROM rooms WHERE room_id = ?', [room_id], function (error, updatedRoomResults) {
                        if (error) {
                            console.error("Database error:", error);
                            return res.status(500).json({ error: true, message: error.message });
                        }

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
                    });
                });
            });
        });
    });
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
    
    dbConn.query(query, function (error, results) {
        if (error) throw error;
        return res.send(results);
    });
});
app.get('/availableRooms', function (req, res) {
    const checkIn = req.query.check_in;
    const checkOut = req.query.check_out;
    const petTypeId = req.query.pet_type_id;

    if (!checkIn || !checkOut) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุวันเช็คอินและเช็คเอาท์"
        });
    }

    let query = `
        SELECT 
            r.type_type_id,
            rt.type_id AS room_type_id, 
            rt.name_type, 
            rt.price_per_day, 
            rt.image, 
            pt.pet_type_id,
            pt.pet_name_type AS pet_type,
            COUNT(r.room_id) AS available
        FROM 
            rooms r
        JOIN 
            room_type rt ON r.type_type_id = rt.type_id
        JOIN 
            pet_type pt ON rt.pet_type = pt.pet_type_id
        WHERE 
            r.status = 1  -- สถานะห้องว่าง
            AND r.deleted_at IS NULL
            AND rt.deleted_at IS NULL
            AND r.room_id NOT IN (
                SELECT 
                    b.room_id 
                FROM 
                    bookings b 
                WHERE 
                    b.deleted_at IS NULL
                    AND b.booking_status NOT IN (2, 3)  -- ไม่เช็คเอาท์หรือยกเลิก
                    AND (
                        (b.check_in <= ? AND b.check_out >= ?)  -- เช็คอินก่อนหรือวันเดียวกันกับที่ต้องการเช็คเอาท์
                        OR (b.check_in >= ? AND b.check_in < ?)  -- เช็คอินในช่วงที่ต้องการจอง
                    )
            )
    `;

    // พารามิเตอร์สำหรับ query
    let params = [checkOut, checkIn, checkIn, checkOut];

    // เพิ่มเงื่อนไขกรองตามประเภทสัตว์เลี้ยง (ถ้ามี)
    if (petTypeId) {
        query += " AND pt.pet_type_id = ?";
        params.push(petTypeId);
    }

    query += " GROUP BY rt.type_id, rt.name_type, rt.price_per_day, rt.image, pt.pet_type_id, pt.pet_name_type";

    // ทำการค้นหาข้อมูล
    dbConn.query(query, params, function (error, results) {
        if (error) {
            console.error("Database Error:", error);
            return res.status(500).send({
                error: true,
                message: "เกิดข้อผิดพลาดในการค้นหาห้องว่าง",
                details: error
            });
        }

        // แปลงรูปแบบ URL ของรูปภาพให้เป็น absolute URL
        const baseUrl = `${req.protocol}://${req.get('host')}`;
        results = results.map(room => {
            // ถ้ามีรูปภาพและไม่ใช่ URL เต็มรูปแบบ ให้เพิ่ม baseUrl
            if (room.image && !room.image.startsWith('http')) {
                room.image = `${baseUrl}${room.image.startsWith('/') ? '' : '/'}${room.image}`;
            }
            return room;
        });

        return res.json({
            error: false,
            message: "ค้นหาห้องว่างสำเร็จ",
            check_in: checkIn,
            check_out: checkOut,
            available_rooms: results
        });
    });
});

app.get("/availableRooms/:type_type_id", function (req, res) {
    const roomTypeId = req.params.type_type_id;
    const checkIn = req.query.check_in;
    const checkOut = req.query.check_out;

    console.log("Check-in:", checkIn, "Check-out:", checkOut,"Room Type ID:", roomTypeId);

    if (!checkIn || !checkOut) {
        return res.status(400).send({
            error: true,
            message: "กรุณาระบุวันเช็คอินและเช็คเอาท์"
        });
    }

    let query = `

        SELECT
            r.room_id,
            rt.name_type,
            rt.price_per_day,
            rt.image,
            pt.pet_type_id,
            pt.pet_name_type AS pet_type,
            COUNT(r.room_id) AS available_rooms
        FROM

            rooms r
        JOIN
            room_type rt ON r.type_type_id = rt.type_id
        JOIN
            pet_type pt ON rt.pet_type = pt.pet_type_id
        WHERE
            r.status = 1
            AND r.deleted_at IS NULL
            AND rt.deleted_at IS NULL
            AND r.type_type_id = ?
            AND r.room_id NOT IN (
                SELECT
                    b.room_id
                FROM
                    bookings b
                WHERE
                    b.deleted_at IS NULL
                    AND b.booking_status NOT IN (2, 3)
                    AND (
                        (b.check_in <= ? AND b.check_out >= ?)
                        OR (b.check_in >= ? AND b.check_in < ?)
                    )
            )
        GROUP BY
            r.room_id, rt.name_type, rt.price_per_day, rt.image, pt.pet_type_id, pt.pet_name_type
    `;
    let params = [roomTypeId, checkOut, checkIn, checkIn, checkOut];

    dbConn.query(query, params, function (error, results) {
        if (error) {
            console.error("Database Error:", error);
            return res.status(500).send({
                error: true,
                message: "เกิดข้อผิดพลาดในการค้นหาห้องว่าง",
                details: error
            });
        }
        
        const baseUrl = `${req.protocol}://${req.get('host')}`;
        results = results.map(room => {
            if (room.image && !room.image.startsWith('http')) {
                room.image = `${baseUrl}${room.image.startsWith('/') ? '' : '/'}${room.image}`;
            }
            return room;
        });

    
        return res.json({
            error: false,
            message: "ค้นหาห้องว่างสำเร็จ",
            check_in: checkIn,
            check_out: checkOut,
            available_rooms: results
        });
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
            const newTotalPay = 0;

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

  app.get("/get-booking/:user_id", function (req, res) {
    const userId = req.params.user_id;
    const query = `
        SELECT b.*
        FROM bookings b
        JOIN pets p ON b.pet_id = p.pet_id
        WHERE p.user_id = ? AND b.deleted_at IS NULL
    `;

    dbConn.query(query, [userId], function (error, results) {
      if (error) {
        console.error(error);
        return res.status(500).send("Database error");
      }
      return res.send(results);
    });
  });

  app.put("/update-booking-status/:booking_id", function (req, res) {
    const bookingId = req.params.booking_id;

    const query = `UPDATE bookings 
                    SET total_pay = (SELECT pay + adjust FROM bookings WHERE booking_id = ?) 
                    WHERE booking_id = ? AND deleted_at IS NULL;`;

    dbConn.query(query, [bookingId,bookingId], function (error, results) {
      if (error) {
        console.error("Database error:", error);
        return res.status(500).send({ error: true, message: "Database error", details: error });
      }

      if (results.affectedRows === 0) {
        return res.status(404).send({ error: true, message: "Booking not found or already deleted" });
      }

      return res.send({ message: "Booking status updated successfully" });
    });
  });


  app.get("/get-payment-method/:method_id", function (req, res) {   
    const methodId = req.params.method_id;
    const query = `SELECT * FROM payment_methods WHERE method_id = ?`;
    dbConn.query(query, [methodId], function (error, results) {
      if (error) throw error;
      if (results.length === 0) {
        return res.status(404).send({ error: true, message: "Payment method not found" });
      }
      return res.send(results[0]);
    });
  });

  app.get("/get-pet/:pet_id", function (req, res) {
    const petId = req.params.pet_id;
    const query = `SELECT * FROM pets WHERE pet_id = ?`;
    dbConn.query(query, [petId], function (error, results) {
      if (error) throw error;
      if (results.length === 0) {
        return res.status(404).send({ error: true, message: "Pet not found" });
      }
      return res.send(results[0]);
    });
  }
);

app.get("/get-room/:room_id", function (req, res) {
    const roomId = req.params.room_id;
    const query = `SELECT * FROM rooms WHERE room_id = ?`;
    dbConn.query(query, [roomId], function (error, results) {
      if (error) throw error;
      if (results.length === 0) {
        return res.status(404).send({ error: true, message: "Room not found" });
      }
      return res.send(results[0]);
    });
  }
);

app.get("/get-room-type/:type_id", function (req, res) {
    const typeId = req.params.type_id;
    const query = `SELECT * FROM room_type WHERE type_id = ?`;
    dbConn.query(query, [typeId], function (error, results) {
      if (error) throw error;
      if (results.length === 0) {
        return res.status(404).send({ error: true, message: "Room type not found" });
      }
      return res.send(results[0]);
    });
  }
);

app.get("/mypet-by-pet-id/:user_id/:pet_type_id", function (req, res) {
    let user_id = req.params.user_id;
    let pet_type_id = req.params.pet_type_id;
    const query = `
        SELECT pets.pet_id, pets.user_id, pets.pet_name, pets.pet_age, pets.pet_breed,
        pets.pet_weight, pets.pet_gender, pets.additional_info, pet_type.pet_name_type, pets.deleted_at
        FROM pets
        INNER JOIN pet_type ON pets.pet_type_id = pet_type.pet_type_id
        WHERE pets.deleted_at IS NULL AND  pets.user_id = ? AND pet_type.pet_type_id = ?`; //WHERE pets.User_id = ?
    dbConn.query(query, [user_id, pet_type_id], function (error, results) {
      if (error) {
        return res
          .status(500)
          .send({
            error: true,
            message: "Database query failed",
            details: error,
          });
      }
      return res.send(results);
    });
  }
);
  
app.post("/insert-booking", function (req, res) {
  let booking = req.body;

  if (!booking) {
    return res
      .status(400)
      .send({ error: true, message: "Please provide booking data" });
  }

  // 📌 แปลงวันที่จาก "DD/MM/YYYY" -> "YYYY-MM-DD"
  function formatDate(dateStr) {
    const [day, month, year] = dateStr.split("/");
    return `${year}-${month}-${day}`; // เปลี่ยนเป็นรูปแบบที่ MySQL รองรับ
  }

  // แปลงค่าก่อนบันทึกลงฐานข้อมูล
  booking.check_in = formatDate(booking.check_in);
  booking.check_out = formatDate(booking.check_out);

  dbConn.query(
    "INSERT INTO bookings SET ?",
    booking,
    function (error, results) {
      if (error) {
        console.error("SQL Error: ", error);
        return res.status(500).send({ error: true, message: "Database error" });
      }
      return res.send({
        message: "Booking inserted successfully",
        booking_id: results.insertId,
      });
    }
  );
});


app.listen(3000, function () {
  console.log("Node app is running on port 3000");
});

module.exports = app;
