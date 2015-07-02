package com.redhat.lightblue.hook.publish.model;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JSONWrapper {
    private final Object value;
    private final JSONCompareMode mode;

    public JSONWrapper(Object value) {
        this.value = value;
        mode = JSONCompareMode.LENIENT;
    }

    public JSONWrapper(Object value, JSONCompareMode mode) {
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
