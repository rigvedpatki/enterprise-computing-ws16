var http = require('http');

var ENDPOINT_URL = 'http://reimbursement-backend.elasticbeanstalk.com/';
var BASIC_AUTH_USER = 'api-user';
var BASIC_AUTH_PASSQORD = 'O9VOG;|g$ia_Jc;EQ<&5';

//test array with all the reimbursements
var reimbursements = [
    {id:'1', name:'Hans', where:'Berlin', why:'Party', when:'2014-05-15', amount:'1337', document:'expenses.pdf', status: 'accepted'},
    {id:'2', name:'Thomas', where:'Berlin', why:'Party', when:'2015-02-22', amount:'13984', document:'expenses.xlsx', status: 'unknown'},
    {id:'3', name:'Klaus', where:'Moskau', why:'Accident', when:'2015-02-15', amount:'1324', document:'expenses.xlsx', status: 'unknown'},
    {id:'4', name:'Hannes', where:'Hamburg', why:'Travel', when:'2014-05-15', amount:'1634', document:'expenses.xlsx', status: 'declined'},
    {id:'5', name:'Hans', where:'Berlin', why:'Party', when:'2012-05-16', amount:'16067', document:'expenses.docx', status: 'accepted'},
    {id:'6', name:'Claudia', where:'Bremen', why:'Travel', when:'2014-03-15', amount:'420', document:'expenses.docx', status: 'declined'},
    {id:'7', name:'Hans', where:'Berlin', why:'Party', when:'2013-12-15', amount:'4267', document:'expenses.xlsx', status: 'unknown'}
];

exports.getRequest = function (requestId, callback) {
    //TODO
    callback(null, reimbursements[0]);
};

exports.getRequests = function (callback) {
    //TODO
    callback(null, reimbursements);
};

exports.createRequest = function (requestValues, filePath, callback) {
    //TODO
    callback('not implemented yet');
};

exports.updateRequest = function (requestId, requestValues, callback) {
    //TODO
    callback('not implemented yet');
};

exports.setStatus = function (requestId, newStatus, callback) {
    //TODO
    callback('not implemented yet');
};

exports.dummyFunction = function (requestId, callback) {
    callback(null, 'Hello Employee! Let\'s edit the request ' + requestId);
};