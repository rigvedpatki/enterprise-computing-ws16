/*eslint-env node*/

var express = require('express');
var bodyParser = require('body-parser');
var cfenv = require('cfenv');
var app = express();
var api = require('./api');
var auth = require('basic-auth');

var MANAGER_USERNAME = 'manager';
var MANAGER_PASSWORD = 'O`M:fX3O"E[gxfT}S+/l';
var EMPLOYEE_USERNAME = 'employee';
var EMPLOYEE_PASSWORD = ')AybMslaS/p|o[si;xlv';

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
    if (employeeAuth(req)) {
        //TODO
    	// test array
    	var reimbursements = [
      	                    {id: '1', name:'Hans', where:'Berlin', why:'Party', when:'11.05.2014', amount:'1337', document:'expenses.pdf', status: 'accepted'},
      	          			{id: '5', name:'Hans', where:'Berlin', why:'Party', when:'12.05.2014', amount:'160', document:'expenses.docx', status: 'accepted'},
      	        			{id: '7', name:'Hans', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'}
      	          			];
        res.render('employee', {title: 'Hey', message: 'Hello Employee!', reimbursements: reimbursements});
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
        //test array
        var reimbursements = [
        	                    {id:'1', name:'Hans', where:'Berlin', why:'Party', when:'11.05.2014', amount:'1337', document:'expenses.pdf', status: 'accepted'},
          	                    {id:'2', name:'Thomas', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'},
          	                    {id:'3', name:'Klaus', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'},
          	                    {id:'4', name:'Hannes', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'declined'},
          	          			{id:'5', name:'Hans', where:'Berlin', why:'Party', when:'12.05.2014', amount:'160', document:'expenses.docx', status: 'accepted'},
          	          		    {id:'6', name:'Claudia', where:'Bremen', why:'Party', when:'12.05.2014', amount:'160', document:'expenses.docx', status: 'declined'},
          	        			{id:'7', name:'Hans', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'}
          	          			];
        var reimbursement = [];
        for (var i = 0; i < reimbursements.length; i++) { 
            if(reimbursements[i]['id'] == requestId) {
            	reimbursement = reimbursements[i];
            }
        }
        api.dummyFunction(requestId, function (err, response) {
            res.render('employee-edit', {title: 'Hey', message: response, reimbursement: reimbursement});
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
    	// test array
    	var reimbursements = [
      	                    {id: '1', name:'Hans', where:'Berlin', why:'Party', when:'11.05.2014', amount:'1337', document:'expenses.pdf', status: 'accepted'},
      	                    {id: '2', name:'Thomas', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'},
      	                    {id: '3', name:'Klaus', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'},
      	                    {id: '4', name:'Hannes', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'declined'},
      	          			{id: '5', name:'Hans', where:'Berlin', why:'Party', when:'12.05.2014', amount:'160', document:'expenses.docx', status: 'accepted'},
      	          		    {id: '6', name:'Claudia', where:'Bremen', why:'Party', when:'12.05.2014', amount:'160', document:'expenses.docx', status: 'declined'},
      	        			{id: '7', name:'Hans', where:'Berlin', why:'Party', when:'13.05.2014', amount:'134', document:'expenses.xlsx', status: 'unknown'}
      	          			];
        res.render('manager', {title: 'Hey', message: 'Hello Manager!', reimbursements: reimbursements});
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

// creates a new request (used by the form in the employee screen)
app.post('/employee/requests', function (req, res) {
    if (employeeAuth(req)) {
        //TODO
        // api.createRequest(..
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
        //TODO
        // api.updateRequest(..
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
        //TODO
        // api.setStatus(..
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