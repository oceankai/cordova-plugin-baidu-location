
#import <Cordova/CDV.h>

#import <BaiduMapAPI_Base/BMKBaseComponent.h>
#import <BaiduMapAPI_Location/BMKLocationComponent.h>
#import <BaiduMapAPI_Search/BMKSearchComponent.h>

@interface BaiduLocation : CDVPlugin<BMKLocationServiceDelegate, BMKGeoCodeSearchDelegate> {
    BMKLocationService* _locService;
    BMKGeoCodeSearch* _geoCodeSerch;
    CDVInvokedUrlCommand* _execCommand;
    NSMutableDictionary* _data;
}

- (void)location:(CDVInvokedUrlCommand *)command;
- (void)didUpdateBMKUserLocation:(BMKUserLocation *)userLocation;

@end