package com.redhat.lightblue.hook.publish;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

import com.redhat.lightblue.hook.publish.model.Event;
import com.redhat.lightblue.hook.publish.model.Event.Operation;
import com.redhat.lightblue.hook.publish.model.Identity;

public class EventExctractionUtil {

    /*
     * Accepts JSONObject / JSONArray and returns the Events for the required
     * permutations.
     *
     * NOTE: this doesn't check if the objects are actually different, call this
     * only if an event is to be created for sure, just to find out what events
     * are to be created.
     */
    public static Set<Event> compareAndExtractEvents(String pre, String post, String ids) throws JSONException, IllegalArgumentException {
        Object preObject = null, postObject = null, idsObject = null;
        if (pre != null) {
            preObject = JSONParser.parseJSON(pre);
        }
        if (post != null && ids != null) {
            postObject = JSONParser.parseJSON(post);
            idsObject = JSONParser.parseJSON(ids);
        } else {
            throw new IllegalArgumentException("post state and projected ids cannot be null:: " + getErrorSignature(pre, post, ids));
        }

        if (!(preObject == null || preObject.getClass().equals(postObject.getClass())) || !postObject.getClass().equals(idsObject.getClass())) {
            throw new IllegalArgumentException("pre state (optional), post state and projected ids need to be valid JSON of the same type:: "
                    + getErrorSignature(pre, post, ids));
        }
        // JSONParser only returns JSONArray/ JSONObject/ JSONString
        if (!postObject.getClass().equals(JSONArray.class) && !postObject.getClass().equals(JSONObject.class)) {
            throw new IllegalArgumentException("Identities can only extracted from JSONArrays or JSONObjects:: " + getErrorSignature(pre, post, ids));
        }

        return compareAndGetEvents(preObject, postObject, idsObject);
    }

    /*
     * For child objects and child arrays, cross multiply the number of simple
     * fields. This is because we need one event per child array element that
     * changed.
     */
    public static Set<Event> compareAndGetEvents(JSONObject pre, JSONObject post, JSONObject ids) throws JSONException {
        Set<Event> result = new HashSet<>();
        Iterator<String> keysIterator = ids.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (post.get(key) instanceof JSONObject || post.get(key) instanceof JSONArray) {
                Object preObject = (pre == null) ? null : pre.opt(key);
                Set<Event> events = compareAndGetEvents(preObject, post.get(key), ids.get(key));
                for (Event event : events) {
                    for (Identity identity : event.getIdentity()) {
                        identity.setField(key + "." + identity.getField());
                    }
                }
                result = crossMultiplySets(result, events);
            } else {
                Identity i = new Identity();
                i.setField(key);
                i.setValue(post.get(key).toString());
                Operation operation = (pre == null) ? Operation.INSERT : Operation.UPDATE;
                if (result.size() == 0) {
                    Event event = new Event();
                    event.setOperation(operation);
                    result.add(event);
                }
                for (Event event : result) {
                    event.getIdentity().add(i);
                }
            }
        }
        return result;
    }

    /*
     * we cannot assume order of objects in the array are going to remain the
     * same, however, we can assume the objects in the array are unique in they
     * identifying field values.
     *
     * identity object is the subset of the integrated state of the entity.
     * using that property we create a map for both pre and post arrays, with
     * the identifying sub state as the key.
     *
     * then for all array elements in the identifying array we lookup if that
     * element was added or updated.
     */
    public static Set<Event> compareAndGetEvents(JSONArray pre, JSONArray post, JSONArray ids) throws JSONException {
        Set<Event> result = new HashSet<>();
        Map<JSONWrapper, JSONWrapper> preMap = getJSONComparisionMap(ids, pre);
        Map<JSONWrapper, JSONWrapper> postMap = getJSONComparisionMap(ids, post);
        for (JSONWrapper key : postMap.keySet()) {
            if (!preMap.containsKey(key) || !preMap.get(key).equals(postMap.get(key))) {
                Object preJsonObject = (preMap.get(key) == null) ? null : preMap.get(key).getValue();
                Set<Event> arrayChildResults = compareAndGetEvents(preJsonObject, postMap.get(key).getValue(), key.getValue());
                for (Event arrayChildResult : arrayChildResults) {
                    if (!preMap.containsKey(key)) {
                        arrayChildResult.setOperation(Operation.INSERT);
                    }
                    result.add(arrayChildResult);
                }
            }
        }
        return result;
    }

    private static String getErrorSignature(String pre, String post, String ids) {
        return "pre:" + pre + " post:" + post + " ids:" + ids;
    }

    private static Set<Event> compareAndGetEvents(Object pre, Object post, Object ids) throws JSONException {
        if ((pre == null || pre instanceof JSONObject) && post instanceof JSONObject && ids instanceof JSONObject) {
            return compareAndGetEvents((JSONObject) pre, (JSONObject) post, (JSONObject) ids);
        } else if ((pre == null || pre instanceof JSONArray) && post instanceof JSONArray && ids instanceof JSONArray) {
            return compareAndGetEvents((JSONArray) pre, (JSONArray) post, (JSONArray) ids);
        } else {
            return null;
        }
    }

    private static Map<JSONWrapper, JSONWrapper> getJSONComparisionMap(JSONArray ids, JSONArray docs) throws JSONException {
        Map<JSONWrapper, JSONWrapper> result = new HashMap<>();
        JSONWrapper[] idsArray = new JSONWrapper[ids.length()];
        for (int i = 0; i < ids.length(); i++) {
            idsArray[i] = new JSONWrapper(ids.get(i));
        }
        if (docs != null) {
            int[] visited = new int[docs.length()];
            for (int i = 0; i < ids.length(); i++) {
                for (int j = 0; j < docs.length(); j++) {
                    if (visited[j] == 0) {
                        JSONWrapper doc = new JSONWrapper(docs.get(j));
                        if (idsArray[i].equals(doc)) {
                            result.put(idsArray[i], doc);
                            visited[j] = 1;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static Set<Event> crossMultiplySets(Set<Event> setOne, Set<Event> setTwo) {
        if (setOne.size() == 0) {
            return setTwo;
        }
        if (setTwo.size() == 0) {
            return setOne;
        }
        Set<Event> resultSets = new HashSet<>();
        for (Event eventTwo : setTwo) {
            for (Event eventOne : setOne) {
                Event resultEvent = new Event();
                resultEvent.addIdentities(eventOne.getIdentity());
                resultEvent.addIdentities(eventTwo.getIdentity());
                // insert wins because a nested object / array was inserted
                resultEvent.setOperation((eventOne.getOperation() == Operation.INSERT || eventTwo.getOperation() == Operation.INSERT)
                        ? Operation.INSERT : Operation.UPDATE);
                resultSets.add(resultEvent);
            }
        }
        return resultSets;
    }
}
