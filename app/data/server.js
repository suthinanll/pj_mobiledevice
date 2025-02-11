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

app.listen(3000, function () {
  console.log("Node app is running on port 3000");
});

module.exports = app;
