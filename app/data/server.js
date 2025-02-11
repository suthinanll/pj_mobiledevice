const { error } = require('console');
var express = require('express');
var app = express();
var bodyParser = require('body-parser')
var mysql = require('mysql');
require('dotenv').config();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

app.get('/', function(req,res){
    return res.send({error:true, message:'Test Emp Web API'})
});

var dbConn = mysql.createConnection({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME
});

dbConn.connect();

app.get('/', (req, res) => {
    return res.send({ error: false, message: 'Test API' });
});

const tables = ['user_type', 'room_type', 'rooms', 'pet_type', 'pets', 'payment_methods'];
// เพิ่มข้อมูลใส่ user room room_type rooms pet_type pets payment
tables.forEach(table => {
    app.post(`/insert/${table}`, (req, res) => {
        const data = req.body;
        if (!data) {
            return res.status(400).send({ error: true, message: `Please provide data for ${table}` });
        }
        dbConn.query(`INSERT INTO ${table} SET ?`, data, (error, results) => {
            if (error) return res.status(500).send(error);
            return res.send({ message: `${table} inserted successfully`, data: results });
        });
    });
});

app.post('/create/acccout', (req, res) => {
    const data = req.body;
    if(!data){
        return res.status(400).send({ error: true, message: 'Please provide data for Create User'
        });
    }
    dbConn.query("INSERT INTO user SET ?", data, (error, results) => {
        if(error) return res.status(500).send(error);
        return res.send({ message: 'Create user successfully', data: results });
        }
    );
});

//แสดงข้อมูล user
app.get('/getuser/:user_id', (req, res) => {
    let id = req.params.user_id
    dbConn.query('SELECT * FROM users WHERE user_id = ? AND delete_at IS NULL', id , (error,results,fields) => {
            if(error) throw error;
            return res.send(results);
            });
    }
)

// เอาของสัตว์เลี้ยงของ user มาโชว์ตาม user_id
app.get('/getpets/:user_id', (req, res) => {
    let id = req.params.user_id;

    let query = `
        SELECT pets.name AS petname, pets.sex AS sex, pets.breed AS breed , pets.weight AS weight, pets.age AS age, pets.description AS descrip, pets.birth_date AS birth , pet_type.name AS type
        FROM users
        JOIN pets ON pets.user_id = users.user_id
        JOIN pet_type ON pet_type.id_type = pets.type_pet
        WHERE users.user_id = ?
        AND users.deleted_at IS NULL
        AND pets.deleted_at IS NULL
        AND pet_type.deleted_at IS NULL
    `;

    dbConn.query(query, id, (error, results) => {
        if (error) {
            console.error("Database Error:", error);
            return res.status(500).send({ error: true, message: "Internal Server Error" });
        }
        console.log("send data get pets from user successfuly")
        return res.json(results);
    });
});


app.get('/getroom/:user_id', (req, res) => {
    let id = req.params.user_id
    let query = `SELECT


    `
    dbConn.query('SELECT name, sex, type_pet FROM pets WHERE user_id = ?', id , (error,results,fields) => {
            if(error) throw error;
            return res.send(results);
        });
    }
);

app.get('/allMember', (req, res) =>{
       dbConn.query('SELECT user_id, name, tell_number, email, user_type FROM users WHERE deleted_at IS NULL', function(error,results, fields){
               if(error) throw error;
               console.log("send data successfuly")
               return res.send(results);
       });
    });

// เอาทุกข้อมูล อ้างอิงจาก user_id
app.get('/AllDataUser/:user_id', (req, res) => {
    let id = req.params.user_id;

    let query = `
       SELECT
           u.user_id, u.name AS user_name, u.tell_number, u.email, ut.type_name AS user_type,
           p.id_pet, p.name AS pet_name, p.sex, p.breed, p.weight, p.age, p.description, p.birth_date,
           pt.name AS pet_type,
           b.booking_id, b.check_in, b.check_out, b.additional_info, b.pay, b.adjust, b.total_pay,
           pm.method_name AS payment_method,
           r.room_id, rt.name_type AS room_type, rt.price_per_day, r.status AS room_status
       FROM users u
       LEFT JOIN user_type ut ON u.user_type = ut.type_id
       LEFT JOIN pets p ON u.user_id = p.user_id AND p.deleted_at IS NULL
       LEFT JOIN pet_type pt ON p.type_pet = pt.id_type AND pt.deleted_at IS NULL
       LEFT JOIN bookings b ON p.id_pet = b.pet_id AND b.deleted_at IS NULL
       LEFT JOIN payment_methods pm ON b.payment_method = pm.method_id
       LEFT JOIN rooms r ON b.room_id = r.room_id AND r.deleted_at IS NULL
       LEFT JOIN room_type rt ON r.type_type_id = rt.type_id AND rt.deleted_at IS NULL
       WHERE u.user_id = ?
       AND u.deleted_at IS NULL;

    `;

    dbConn.query(query, id, (error, results) => {
        if (error) {
            console.error("Database Error:", error);
            return res.status(500).send({ error: true, message: "Internal Server Error" });
        }
        console.log("send AllData from user successfuly")
        return res.json(results);
    });
});

// เอาทุกข้อมูล อ้างอิงจาก booking_id
app.get('/AllDataBooking/:booking_id', (req, res) => {
    let id = req.params.booking_id;

    let query = `
       SELECT
           b.booking_id, b.check_in, b.check_out, b.additional_info,
           b.pay, b.adjust, b.total_pay,
           pm.method_name AS payment_method,
           p.id_pet, p.name AS pet_name, p.sex, p.breed, p.weight, p.age,
           p.description, p.birth_date,
           pt.name AS pet_type,
           u.user_id, u.name AS user_name, u.tell_number, u.email, ut.type_name AS user_type,
           r.room_id, rt.name_type AS room_type, rt.price_per_day, r.status AS room_status
       FROM bookings b
       LEFT JOIN payment_methods pm ON b.payment_method = pm.method_id
       LEFT JOIN pets p ON b.pet_id = p.id_pet AND p.deleted_at IS NULL
       LEFT JOIN pet_type pt ON p.type_pet = pt.id_type AND pt.deleted_at IS NULL
       LEFT JOIN users u ON p.user_id = u.user_id AND u.deleted_at IS NULL
       LEFT JOIN user_type ut ON u.user_type = ut.type_id
       LEFT JOIN rooms r ON b.room_id = r.room_id AND r.deleted_at IS NULL
       LEFT JOIN room_type rt ON r.type_type_id = rt.type_id AND rt.deleted_at IS NULL
       WHERE b.deleted_at IS NULL;
    `;

    dbConn.query(query, id, (error, results) => {
        if (error) {
            console.error("Database Error:", error);
            return res.status(500).send({ error: true, message: "Internal Server Error" });
        }
        console.log("send AllData from user successfuly")
        return res.json(results);
    });
});



app.listen(3000, function(){
    console.log('Node app is running on port 3000');
});

module.exports = app;