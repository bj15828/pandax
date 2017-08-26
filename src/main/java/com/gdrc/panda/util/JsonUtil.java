package com.gdrc.panda.util;

import com.google.gson.Gson;

public class JsonUtil {

  
  public static Object json2Object(String json,Class cls){
    
    Gson g = new Gson();
    return  g.fromJson(json, cls);
    
  }
  
  public static String object2Json(Object o ){
    
    Gson g = new Gson();
    return  g.toJson(o);
    
  }
  
}
