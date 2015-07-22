package com.redhat.lightblue.hook.publish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONParser;

public class JSONWrapper {
    private final Object value;
    private final JSONCompareMode mode;

    public JSONWrapper(Object value) {
        this.value = value;
        mode = JSONCompareMode.LENIENT;
    }

    public JSONWrapper(Object value, JSONCompareMode mode) throws IllegalArgumentException {

        if (value instanceof String) {
            try {
                JSONParser.parseJSON((String) value);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (!(value instanceof JSONArray) && !(value instanceof JSONObject) && !(value instanceof JSONString)) {
            throw new IllegalArgumentException("Acceptable values for JSONWrapper are JSONArray/JSONObject/JSONString/String, got(" + value.getClass() + ")::"
                    + value);
        }

        this.value = value;
        this.mode = mode;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if ((obj instanceof JSONWrapper) && JSONCompare.compareJSON(value.toString(), ((JSONWrapper) obj).value.toString(), mode).passed()) {
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
