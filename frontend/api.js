var http = require('http');

exports.getRequest = function (requestId, callback) {
    //TODO
    callback('not implemented yet');
};

exports.getRequests = function (callback) {
    //TODO
    callback('not implemented yet');
};

exports.createRequest = function (requestValues, document, callback) {
    //TODO
    callback('not implemented yet');
};

exports.dummyFunction = function (requestId, callback) {
    callback(null, 'Hello Employee! Let\'s edit the request ' + requestId);
};