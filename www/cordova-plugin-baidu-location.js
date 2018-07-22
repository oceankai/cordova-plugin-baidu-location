var exec = require('cordova/exec');

var BaiduLocation = {
    location: function (arg0, success, error) {
        exec(success, error, 'BaiduLocation', 'location', [arg0]);
    },
};

module.exports = BaiduLocation;
