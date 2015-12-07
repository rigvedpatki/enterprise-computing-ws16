/*eslint-env node*/

var express = require('express');
var bodyParser = require('body-parser');
var cfenv = require('cfenv');
var app = express();
var api = require('./api');

app.use(express.static(__dirname + '/public'));
app.use(bodyParser.json());
app.set('views', './views');
app.set('view engine', 'jade');

var appEnv = cfenv.getAppEnv();

app.listen(appEnv.port, '0.0.0.0', function () {
    console.log("server starting on " + appEnv.url);
});

// EMPLOYEE main screen (add a request and list all requests)
app.get('/employee', function (req, res) {
    res.render('employee', {title: 'Hey', message: 'Hello Employee!'});
});

// EMPLOYEE edit request screen (edit the values of one particular request)
app.get('/employee/:id', function (req, res) {
    var requestId = req.params['id'];
    api.dummyFunction(requestId, function (err, response) {
        res.render('employee-edit', {title: 'Hey', message: response});
    });
});

// MANAGER screen (list of all pending requests)
app.get('/manager', function (req, res) {
    res.render('manager', {title: 'Hey', message: 'Hello Manager!'});
});

// creates a new request (used by the form in the employee screen)
app.post('/employee/requests', function (req, res) {
    //TODO
    // api.createRequest(..
});

// should be used to overwrite the request's values (by employee)
app.put('/employee/requests/:id', function (req, res) {
    //TODO
    // api.createRequest(..
});

// should be used to overwrite the request's status (by manager)
app.put('/manager/requests/:id/status', function (req, res) {
    //TODO
    // api.setStatus(..
});
