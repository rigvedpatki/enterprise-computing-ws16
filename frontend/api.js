var http = require('http');
var request = require('request');
var fs = require('fs');

var ENDPOINT_URL = 'reimbursement-backend.elasticbeanstalk.com';
var BASIC_AUTH_USER = 'api-user';
var BASIC_AUTH_PASSWORD = 'O9VOG;|g$ia_Jc;EQ<&5';
var BASE_URL = 'http://' + BASIC_AUTH_USER + ':' + BASIC_AUTH_PASSWORD + '@' + ENDPOINT_URL;

exports.getRequest = function (requestId, callback) {
    request(BASE_URL + '/requests/' + requestId, function (err, response, body) {
        if (!body) {
            callback('not found')
        } else {
            callback(null, JSON.parse(body))
        }
    });
};

exports.getRequests = function (callback) {
    request(BASE_URL + '/requests/', function (err, response, body) {
        if (!body) {
            callback('not requests')
        } else {
            callback(null, JSON.parse(body))
        }
    });
};

exports.createRequest = function (requestValues, file, callback) {
    requestValues.fileName = file.originalname;
    requestValues.document = fs.createReadStream(file.path);

    request.post({
        url: BASE_URL + '/requests',
        formData: requestValues
    }, function optionalCallback(err, httpResponse, body) {
        if (err) {
            callback(err)
        } else {
            callback(null)
        }
    });
};

exports.updateRequest = function (requestId, requestValues, callback) {
    request.put({
        url: BASE_URL + '/requests/' + requestId,
        formData: requestValues
    }, function optionalCallback(err, httpResponse, body) {
        if (err) {
            callback(err)
        } else {
            callback(null)
        }
    });
};

exports.setStatus = function (requestId, newStatus, callback) {
    request.put({
        url: BASE_URL + '/requests/' + requestId + '/status',
        qs: {status: newStatus }
    }, function optionalCallback(err, httpResponse, body) {
        if (err) {
            callback(err)
        } else {
            callback(null)
        }
    });
};

exports.getDocument = function (requestId, callback) {
    request(BASE_URL + '/requests/' + requestId + '/document', function (err, response, body) {
        if (!body) {
            callback('not found')
        } else {
            callback(null, body)
        }
    });
};