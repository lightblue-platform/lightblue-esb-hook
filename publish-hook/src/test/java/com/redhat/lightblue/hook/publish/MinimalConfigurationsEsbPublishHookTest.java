package com.redhat.lightblue.hook.publish;

import static com.redhat.lightblue.util.JsonUtils.json;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadResource;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class MinimalConfigurationsEsbPublishHookTest extends BaseEsbPublishHookTest {

    private static final String expectedIdentityKeys = "[{\"field\":\"_id\"},{\"value\":\"123\",\"field\":\"iso2Code\"},{\"value\":\"456\",\"field\":\"iso3Code\"}]";
    private static final String expectedFields = ",\"rootIdentity#\":0,\"headers#\":0";

    public MinimalConfigurationsEsbPublishHookTest() throws Exception {
        super();
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{
                json(loadResource("./metadata/esbEvents.json")),
                json(loadResource("./metadata/countryWithMinimalConfigurations.json"))
        };
    }

    @Test
    public void testPublishonInsert() throws Exception {

        insertCountry();
        verifyEvent(1, "{\"field\":\"objectType\",\"op\":\"$eq\",\"rvalue\":\"esbEvents\"}", expectedIdentityKeys, expectedFields, "INSERT");
    }

    /**
     * If no trigger is provided, then all actions of the CRUD type will create
     * events.
     */
    @Test
    public void testPublishOnNameUpdate() throws Exception {

        insertCountry();
        updateCountry(1, "{ \"$set\": { \"name\": \"England\" } }");
        verifyEvent(
                1,
                "{ \"$and\" :[{\"field\":\"objectType\",\"op\":\"$eq\",\"rvalue\":\"esbEvents\"}, {\"field\":\"operation\",\"op\":\"$eq\",\"rvalue\":\"UPDATE\"}] }",
                expectedIdentityKeys, expectedFields, "UPDATE");
    }
}
