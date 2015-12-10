/*eslint-env node*/

var express = require('express');
var bodyParser = require('body-parser');
var cfenv = require('cfenv');
var app = express();
var auth = require('basic-auth');
var multer = require('multer');
var path = require('path');
var upload = multer({
    dest: 'uploads/'
});

var api = require('./api');

var MANAGER_USERNAME = 'manager';
var MANAGER_PASSWORD = 'O`M:fX3O"E[gxfT}S+/l';
var EMPLOYEE_USERNAME = 'employee';
var EMPLOYEE_PASSWORD = ')AybMslaS/p|o[si;xlv';

app.use(express.static(__dirname + '/public'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

var appEnv = cfenv.getAppEnv();

app.listen(appEnv.port, '0.0.0.0', function () {
    console.log("server starting on " + appEnv.url);
});

// EMPLOYEE main screen (add a request and list all requests)
app.get('/employee', function (req, res) {
    if (employeeAuth(req)) {
        api.getRequests(function (err, requests) {
            if (err) {
                res.statusCode = 500;
                res.send(err);
            } else {
                res.render('employee', {title: 'Hey', message: 'Hello Employee!', reimbursements: requests});
            }
        });
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// EMPLOYEE edit request screen (edit the values of one particular request)
app.get('/employee/:id', function (req, res) {
    if (employeeAuth(req)) {
        var requestId = req.params['id'];
        // TODO
        api.getRequest(requestId, function (err, request) {
            if (err) {
                res.statusCode = 500;
                res.send(err);
            } else {
                res.render('employee-edit', {title: 'Hey', reimbursement: request});
            }
        });
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// MANAGER screen (list of all pending requests)
app.get('/manager', function (req, res) {
    if (managerAuth(req)) {
        api.getRequests(function (err, requests) {
            if (err) {
                res.statusCode = 500;
                res.send(err);
            } else {
                res.render('manager', {title: 'Hey', message: 'Hello Manager!', reimbursements: requests});
            }
        });
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// creates a new request (used by the form in the employee screen)
app.post('/employee/requests', upload.single('document'), function (req, res) {
    if (employeeAuth(req)) {
        var file = req.file.path;
        var requestValues = {
            name: req.body['name'],
            where: req.body['where'],
            why: req.body['why'],
            when: req.body['when'],
            amount: req.body['amount']
        };
        api.createRequest(requestValues, file, function (err, id) {
            if (err) {
                res.statusCode = 500;
                res.send(err);
            } else {
                res.redirect('/employee');
            }
        });
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// should be used to overwrite the request's values (by employee)
app.post('/employee/requests/:id', function (req, res) {
    if (employeeAuth(req)) {
        var requestId = req.params['id'];
        var requestValues = {
            name: req.body['name'],
            where: req.body['where'],
            why: req.body['why'],
            when: req.body['when'],
            amount: req.body['amount']
        };
        api.updateRequest(requestId, requestValues, function(err){
            if (err) {
                res.statusCode = 500;
                res.send(err);
            } else {
                res.redirect('/employee');
            }
        });
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// should be used to overwrite the request's status (by manager)
app.post('/manager/requests/:id/status', function (req, res) {
    if (managerAuth(req)) {
        var requestId = req.params['id'];
        var requestStatus = req.body['status'];
        api.setStatus(requestId, requestStatus, function(err){
            if (err) {
                res.statusCode = 500;
                res.send(err);
            } else {
                res.redirect('/manager');
            }
        });
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// reads basic auth user and password from the request header
managerAuth = function (req) {
    var credentials = auth(req);
    if (!credentials || credentials.name !== MANAGER_USERNAME || credentials.pass !== MANAGER_PASSWORD) {
        return false;
    } else {
        return true;
    }
};

// reads basic auth user and password from the request header
employeeAuth = function (req) {
    var credentials = auth(req);
    if (!credentials || credentials.name !== EMPLOYEE_USERNAME || credentials.pass !== EMPLOYEE_PASSWORD) {
        return false;
    } else {
        return true;
    }
};