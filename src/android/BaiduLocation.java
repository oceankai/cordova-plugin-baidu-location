package com.happy2discover.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaiduLocation extends CordovaPlugin {

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      if (action.equals("location")) {
          String message = args.getString(0);
          this.location(message, callbackContext);
          return true;
      }
      return false;
  }
  
  private void location(String message, CallbackContext callbackContext) {
      if (message != null && message.length() > 0) {
          callbackContext.success(message);
      } else {
          callbackContext.error("Expected one non-empty string argument.");
      }
  }
  }