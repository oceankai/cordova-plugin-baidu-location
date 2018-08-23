var exec = require('cordova/exec');

var BaiduLocation = {
    location: function (success, error, option) {
        exec(success, error, 'BaiduLocation', 'location', [option]);
    },
};

module.exports = BaiduLocation
