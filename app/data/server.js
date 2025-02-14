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

app.get("/allEmp", function (req, res) {
  dbConn.query("SELECT * FROM employee", function (error, results, fields) {
    if (error) throw error;
    return res.send(results);
  });
});

app.post("/emp", function (req, res) {
  var std = req.body;

  if (!std) {
    return res
      .status(400)
      .send({ error: true, message: "Please provide student" });
  }

  dbConn.query(
    "INSERT INTO employee SET ? ",
    std,
    function (error, results, fields) {
      if (error) throw error;
      return res.send(results);
    }
  );
});

app.put("/update_emp/:emp_id", function (req, res) {
  var emp = req.body;
  var id = req.params.emp_id;
  if (!emp) {
    return res
      .status(400)
      .send({ error: std, message: "Please provide student" });
  }
  dbConn.query(
    "UPDATE employee SET ? WHERE emp_id = ?",
    [emp, id],
    function (error, results, fields) {
      if (error) throw error;
      return res.send(results);
    }
  );
})

app.delete("/delete_emp/:emp_id", function (req, res) {
  var id = req.params.emp_id;
  dbConn.query("DELETE FROM employee WHERE emp_id = ?", [id], function (
    error,
    results,
    fields
  ) {
    if (error) throw error;
    return res.send(results);
  });
});

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




app.listen(3000, function () {
  console.log("Node app is running on port 3000");
});

module.exports = app;
