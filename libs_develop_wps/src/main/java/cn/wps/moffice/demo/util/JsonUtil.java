package cn.wps.moffice.demo.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    public static String getString2Json(JSONObject object, String key, String defaultString) {
        if (TextUtils.isEmpty(defaultString)) {
            defaultString = "";
        }
        if (TextUtils.isEmpty(key)) {
            return defaultString;
        }
        if (object != null) {
            try {
                return object.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultString;
    }

    public static JSONArray getJsonArray2Json(JSONObject object, String key) {
        if (object != null) {
            try {
                return object.getJSONArray(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONArray();
    }

    public static JSONObject getJson2Json(JSONObject object, String key) {
        if (object != null) {
            try {
                return object.getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean getBoolean2Json(JSONObject object, String key, boolean defaultBoolean) {
        if (object != null) {
            try {
                return object.getBoolean(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultBoolean;
    }

    public static String getString2JsonArray(JSONArray jsonArray, int index, String defaultString) {
        if (TextUtils.isEmpty(defaultString)) {
            defaultString = "";
        }
        if (jsonArray != null) {
            try {
                return jsonArray.getString(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultString;
    }

    public static JSONObject getJson2JsonArray(JSONArray jsonArray, int index) {
        if (jsonArray != null) {
            try {
                return jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONArray string2JsonArray(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        try {
            JSONArray array = new JSONArray(data);
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void putString2Json(JSONObject jsonObject, String key, Object data) {
        try {
            jsonObject.put(key, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int getInt2Json(JSONObject object, String key, int defaultInt) {
        if (TextUtils.isEmpty(key)) {
            return defaultInt;
        }
        if (object != null) {
            try {
                return object.getInt(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultInt;
    }
}
