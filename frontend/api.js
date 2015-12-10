var http = require('http');
var request = require('request');
var fs = require('fs');

var ENDPOINT_URL = 'reimbursement-backend.elasticbeanstalk.com';
var BASIC_AUTH_USER = 'api-user';
var BASIC_AUTH_PASSWORD = 'O9VOG;|g$ia_Jc;EQ<&5';
var BASE_URL = 'http://' + BASIC_AUTH_USER + ':' + BASIC_AUTH_PASSWORD + '@' + ENDPOINT_URL;

//test array with all the reimbursements
var reimbursements = [
    {
        id: '1',
        name: 'Hans',
        where: 'Berlin',
        why: 'Party',
        when: '2014-05-15',
        amount: '1337',
        document: 'expenses.pdf',
        status: 'accepted'
    },
    {
        id: '2',
        name: 'Thomas',
        where: 'Berlin',
        why: 'Party',
        when: '2015-02-22',
        amount: '13984',
        document: 'expenses.xlsx',
        status: 'unknown'
    },
    {
        id: '3',
        name: 'Klaus',
        where: 'Moskau',
        why: 'Accident',
        when: '2015-02-15',
        amount: '1324',
        document: 'expenses.xlsx',
        status: 'unknown'
    },
    {
        id: '4',
        name: 'Hannes',
        where: 'Hamburg',
        why: 'Travel',
        when: '2014-05-15',
        amount: '1634',
        document: 'expenses.xlsx',
        status: 'declined'
    },
    {
        id: '5',
        name: 'Hans',
        where: 'Berlin',
        why: 'Party',
        when: '2012-05-16',
        amount: '16067',
        document: 'expenses.docx',
        status: 'accepted'
    },
    {
        id: '6',
        name: 'Claudia',
        where: 'Bremen',
        why: 'Travel',
        when: '2014-03-15',
        amount: '420',
        document: 'expenses.docx',
        status: 'declined'
    },
    {
        id: '7',
        name: 'Hans',
        where: 'Berlin',
        why: 'Party',
        when: '2013-12-15',
        amount: '4267',
        document: 'expenses.xlsx',
        status: 'unknown'
    }
];

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