package com.redhat.lightblue.hook.publish;

import static com.redhat.lightblue.util.JsonUtils.json;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadResource;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class FieldIdentityEsbPublishHookTest extends BaseEsbPublishHookTest {

    private static final String expectedIdentityKeys = "[{\"field\":\"_id\"},{\"value\":\"swift\",\"field\":\"optionalField.mySpecificField\"}]";
    private static final String expectedFields = ",\"rootIdentity\":[{\"field\":\"_id\"}]"
            + ",\"headers\":[{\"name\":\"test\",\"value\":\"true\"},{\"name\":\"noop\",\"value\":\"false\"}]";

    public FieldIdentityEsbPublishHookTest() throws Exception {
        super();
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{
                json(loadResource("./metadata/esbEvents.json")),
                json(loadResource("./metadata/countryWithFieldsIdentityConfigured.json"))
        };
    }

    @Test
    public void testPublishonInsert() throws Exception {

        insertCountry();
        verifyEvent(1, "{\"field\":\"objectType\",\"op\":\"$eq\",\"rvalue\":\"esbEvents\"}", expectedIdentityKeys, expectedFields, "INSERT");
    }

    @Test
    public void testPublishOnNameUpdate() throws Exception {

        insertCountry();
        updateCountry(1, "{ \"$set\": { \"name\": \"England\" } }");
        verifyEvent(
                0,
                "{ \"$and\" :[{\"field\":\"objectType\",\"op\":\"$eq\",\"rvalue\":\"esbEvents\"}, {\"field\":\"operation\",\"op\":\"$eq\",\"rvalue\":\"UPDATE\"}] }",
                null, null, null);
    }
}
