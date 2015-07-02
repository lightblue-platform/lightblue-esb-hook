package com.redhat.lightblue.hook.publish;

import static com.redhat.lightblue.hook.publish.model.IdentitySet.INSERT_OPERATION;
import static com.redhat.lightblue.hook.publish.model.IdentitySet.UPDATE_OPERATION;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

import com.redhat.lightblue.hook.publish.model.Identity;
import com.redhat.lightblue.hook.publish.model.IdentitySet;
import com.redhat.lightblue.hook.publish.model.JSONWrapper;

public class IdentityExctractionUtil {

    /*
     * Accepts JSONObject / JSONArray and returns the IdentitySet for the
     * required permutations.
     * 
     * each IdentitySet implies one event
     * 
     * NOTE: this doesn't check if the objects are actually different, call this
     * only if an event is to be created for sure, just to find out what events
     * are to be created.
     */
    public static Set<IdentitySet> compareAndGetIdentities(String pre, String post, String ids) throws JSONException, IllegalArgumentException {
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

        return compareAndGetIdentities(preObject, postObject, idsObject);
    }

    /*
     * For child objects and child arrays, cross multiply the number of simple
     * fields. This is because we need one event per child array element that
     * changed.
     */
    public static Set<IdentitySet> compareAndGetIdentities(JSONObject pre, JSONObject post, JSONObject ids) throws JSONException {
        Set<IdentitySet> result = new HashSet<>();
        Iterator<String> keysIterator = ids.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (post.get(key) instanceof JSONObject || post.get(key) instanceof JSONArray) {
                Object preObject = (pre == null) ? null : pre.opt(key);
                Set<IdentitySet> arraySets = getNewSetsWithPrependName(key, compareAndGetIdentities(preObject, post.get(key), ids.get(key)));
                result = crossMultiplySets(result, arraySets);
            } else {
                Identity i = new Identity();
                i.setField(key);
                i.setValue(post.get(key).toString());
                String operation = (pre == null) ? INSERT_OPERATION : UPDATE_OPERATION;
                if (result.size() == 0) {
                    result.add(new IdentitySet(new HashSet<Identity>(), operation));
                }
                for (IdentitySet set : result) {
                    set.getSet().add(i);
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
    public static Set<IdentitySet> compareAndGetIdentities(JSONArray pre, JSONArray post, JSONArray ids) throws JSONException {
        Set<IdentitySet> result = new HashSet<>();
        Map<JSONWrapper, JSONWrapper> preMap = getJSONComparisionMap(ids, pre);
        Map<JSONWrapper, JSONWrapper> postMap = getJSONComparisionMap(ids, post);
        for (JSONWrapper key : postMap.keySet()) {
            if (!preMap.containsKey(key) || !preMap.get(key).equals(postMap.get(key))) {
                Object preJsonObject = (preMap.get(key) == null) ? null : preMap.get(key).getValue();
                Set<IdentitySet> arrayChildResults = compareAndGetIdentities(preJsonObject, postMap.get(key).getValue(), key.getValue());
                for (IdentitySet arrayChildResult : arrayChildResults) {
                    if (!preMap.containsKey(key)) {
                        arrayChildResult.setOperation(INSERT_OPERATION);
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

    private static Set<IdentitySet> compareAndGetIdentities(Object pre, Object post, Object ids) throws JSONException {
        if ((pre == null || pre instanceof JSONObject) && post instanceof JSONObject && ids instanceof JSONObject) {
            return compareAndGetIdentities((JSONObject) pre, (JSONObject) post, (JSONObject) ids);
        } else if ((pre == null || pre instanceof JSONArray) && post instanceof JSONArray && ids instanceof JSONArray) {
            return compareAndGetIdentities((JSONArray) pre, (JSONArray) post, (JSONArray) ids);
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

    private static Set<IdentitySet> getNewSetsWithPrependName(String name, Set<IdentitySet> sets) {
        Set<IdentitySet> resultSets = new HashSet<>();
        for (IdentitySet set : sets) {
            Set<Identity> resultSet = new HashSet<>();
            for (Identity i : set.getSet()) {
                Identity resultIdentity = new Identity();
                resultIdentity.setField(name + "." + i.getField());
                resultIdentity.setValue(i.getValue());
                resultSet.add(resultIdentity);
            }
            resultSets.add(new IdentitySet(resultSet, set.getOperation()));
        }
        return resultSets;
    }

    private static Set<IdentitySet> crossMultiplySets(Set<IdentitySet> sets1, Set<IdentitySet> sets2) {
        if (sets1.size() == 0)
            return sets2;
        if (sets2.size() == 0)
            return sets1;
        Set<IdentitySet> resultSets = new HashSet<>();
        for (IdentitySet set2 : sets2) {
            for (IdentitySet set1 : sets1) {
                Set<Identity> resultSet = new HashSet<>();
                resultSet.addAll(set1.getSet());
                resultSet.addAll(set2.getSet());
                // insert wins because a nested object / array was inserted
                String operation = (set1.getOperation().contentEquals(INSERT_OPERATION) || set2.getOperation().contentEquals(INSERT_OPERATION))
                        ? INSERT_OPERATION
                        : UPDATE_OPERATION;
                resultSets.add(new IdentitySet(resultSet, operation));
            }
        }
        return resultSets;
    }
}
