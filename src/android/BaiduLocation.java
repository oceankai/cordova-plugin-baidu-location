package com.happy2discover.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

public class BaiduLocation extends CordovaPlugin {
    public static CallbackContext mCallbackContext = null;

    public LocationClient mLocationClient = null;
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

        int locType = location.getLocType();                    //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        switch (locType) {
            case BDLocation.TypeCriteriaException:
            case BDLocation.TypeNetWorkException:
            case BDLocation.TypeOffLineLocationNetworkFail:
            case BDLocation.TypeServerError:
            case BDLocation.TypeServerDecryptError:
            case BDLocation.TypeServerCheckKeyError:
                mCallbackContext.error("location error", locType);
                break;
            default:
                double latitude = location.getLatitude();                    //获取纬度信息
                double longitude = location.getLongitude();                  //获取经度信息
                float radius = location.getRadius();                         //获取定位精度，默认值为0.0f
                String coorType = location.getCoorType();                     //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
                String addrStr = location.getAddrStr();                       //获取详细地址信息
                String country = location.getCountry();                       //获取国家
                String province = location.getProvince();                     //获取省份
                String city = location.getCity();                             //获取城市
                String district = location.getDistrict();                     //获取区县
                String street = location.getStreet();                         //获取街道信息
                String locationDescribe = location.getLocationDescribe();     //获取位置描述信息
                List<Poi> poiList = location.getPoiList();                    //获取周边POI信息

                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("latitude", latitude);
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("radius", radius);
                    jsonObject.put("coorType", coorType);
                    jsonObject.put("addrStr", addrStr);
                    jsonObject.put("country", country);
                    jsonObject.put("province", province);
                    jsonObject.put("city", city);
                    jsonObject.put("district", district);
                    jsonObject.put("street", street);
                    jsonObject.put("locationDescribe", locationDescribe);

                    JSONArray jsonArray = new JSONArray();
                    for (Poi poi: poiList) {
                        JSONObject poiObject = new JSONObject();
                        poiObject.put("id", poi.getId());
                        poiObject.put("name", poi.getName());
                        poiObject.put("rank", poi.getRank());

                        jsonArray.put(poiObject);
                    }
                    jsonObject.put("poiList", jsonArray);

                    mCallbackContext.success(jsonObject);
                } catch (JSONException e) {
                    mCallbackContext.error(e.getMessage());
                } finally {
                }
                break;
        }

        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        }
    };

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;

        if (action.equals("location")) {
            if (null == mLocationClient) {
                //声明LocationClient类
                mLocationClient = new LocationClient(this.webView.getContext());
            }

            JSONObject optionObject = null;
            if (1 != args.length() && !args.isNull(0)) {
              optionObject = args.getJSONObject(0);
            }
            LocationClientOption option = this.generateLocationClientOption(optionObject);

            //配置定位SDK参数
            mLocationClient.setLocOption(option);

            //注册监听函数
            mLocationClient.registerLocationListener(myListener);

            mLocationClient.start();

            return true;
        }

        return false;
    }

    private LocationClientOption generateLocationClientOption(JSONObject optionObject) {
        LocationClientOption option = new LocationClientOption();

        if (null != optionObject && optionObject.has("locationMode")) {
            try {
                String locationMode = optionObject.getString("locationMode");
                if (locationMode.equals("batterySaving")) {
                    option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
                }
                else if (locationMode.equals("deviceSensors")) {
                    option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
                }
                else {
                  option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                }
            } catch (JSONException e) {

            } finally {
            }
        }
        else {
            //LocationMode.Hight_Accuracy：高精度；
            //LocationMode.Battery_Saving：低功耗；
            //LocationMode.Device_Sensors：仅使用设备；
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        }

        if (null != optionObject && optionObject.has("coorType")) {
            try {
                String coorType = optionObject.getString("coorType");

                if (coorType.equals("gcj02") || coorType.equals("bd09") || coorType.equals("bd09ll")) {
                    option.setCoorType(coorType);
                }
                else {
                    option.setCoorType("bd09ll");
                }
            } catch (JSONException e) {

            } finally {
            }
        }
        else {
            //可选，设置返回经纬度坐标类型，默认gcj02
            //gcj02：国测局坐标；
            //bd09ll：百度经纬度坐标；
            //bd09：百度墨卡托坐标；
            //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
            option.setCoorType("bd09ll");
        }

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(1000);

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(true);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);

        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setWifiCacheTimeOut(5*60*1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        //可选，是否需要位置描述信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的位置信息，此处必须为true
        option.setIsNeedLocationDescribe(true);

        //可选，是否需要周边POI信息，默认为不需要，即参数为false
        //如果开发者需要获得周边POI信息，此处必须为true
        option.setIsNeedLocationPoiList(true);

        return option;
    }
}
