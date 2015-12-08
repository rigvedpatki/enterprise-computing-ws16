var http = require('http');

var ENDPOINT_URL = 'http://reimbursement-backend.elasticbeanstalk.com/';
var BASIC_AUTH_USER = 'api-user';
var BASIC_AUTH_PASSQORD = 'O9VOG;|g$ia_Jc;EQ<&5';

exports.getRequest = function (requestId, callback) {
    //TODO
    callback('not implemented yet');
};

exports.getRequests = function (callback) {
    //TODO
    callback('not implemented yet');
};

exports.updateRequest = function (requestId, requestValues, callback) {
    //TODO
    callback('not implemented yet');
};

exports.createRequest = function (requestValues, document, callback) {
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