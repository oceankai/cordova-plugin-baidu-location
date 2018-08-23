# cordova-plugin-baidu-location

# install
cordova plugin add https://github.com/oceankai/cordova-plugin-baidu-location.git --variable AK_ANDROID=your android key --variable IOS_KEY=your ios key

# useage

window.BaiduLocation.location(function (result) {<br> 
  console.log('result: ', result)<br> 
 }, function (error) {<br> 
  console.log('error: ', error)<br> 
});
