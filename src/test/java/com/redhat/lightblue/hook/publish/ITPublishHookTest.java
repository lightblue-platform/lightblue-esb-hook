package com.redhat.lightblue.hook.publish;

import static com.redhat.lightblue.test.Assert.assertNoDataErrors;
import static com.redhat.lightblue.test.Assert.assertNoErrors;
import static com.redhat.lightblue.util.JsonUtils.json;
import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.Response;
import com.redhat.lightblue.crud.FindRequest;
import com.redhat.lightblue.crud.InsertionRequest;
import com.redhat.lightblue.mongo.test.AbstractMongoCRUDTestController;

public class ITPublishHookTest extends AbstractMongoCRUDTestController {

    private static final String ESB_EVENTS_VERSION = "0.0.1-SNAPSHOT";
    private static final String COUNTRY_VERSION = "0.1.0-SNAPSHOT";

    @BeforeClass
    public static void preparePublishHookDatasources() {
        System.setProperty("mongo.datasource", "mongodata");
    }

    public ITPublishHookTest() throws Exception {
        super();
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{
                json(loadResource("/metadata/esbEvents.json", true)),
                json(loadResource("/metadata/country_with_publishhook.json", true))
        };
    }

    @Before
    public void before() throws UnknownHostException {
        cleanupMongoCollections("country", "esbEvents");
    }

    /**
     * If no trigger is provided, then all actions of the CRUD type will create events.
     */
    @Test
    public void testPublish() throws Exception {
        Response insertResponse = getLightblueFactory().getMediator().insert(createRequest_FromJsonString(
                InsertionRequest.class,
                "{\"entity\":\"country\",\"entityVersion\":\"" + COUNTRY_VERSION + "\",\"data\":["
                        + "{\"name\":\"United States\",\"iso2Code\":\"123\",\"iso3Code\":\"456\"}"
                        + "]}"));
        assertNoErrors(insertResponse);
        assertNoDataErrors(insertResponse);
        assertEquals(1, insertResponse.getModifiedCount());

        Response findResponse = getLightblueFactory().getMediator().find(createRequest_FromJsonString(
                FindRequest.class,
                "{\"entity\":\"esbEvents\",\"entityVersion\":\"" + ESB_EVENTS_VERSION + "\","
                        + "\"query\":{\"field\":\"objectType\",\"op\":\"$eq\",\"rvalue\":\"esbEvents\"},"
                        + "\"projection\": [{\"field\":\"*\",\"include\":true,\"recursive\":true}]}"));
        assertNoErrors(findResponse);
        assertNoDataErrors(findResponse);
        assertEquals(1, findResponse.getMatchCount());

        //dates and uids had to be left out to prevent test from always failing.
        JSONAssert.assertEquals("[{\"identity\":[{\"field\":\"_id\"},{\"value\":\"123\",\"field\":\"iso2Code\"},{\"value\":\"456\",\"field\":\"iso3Code\"}],\"rootEntityName\":\"Country\",\"endSystem\":\"WEB\",\"createdBy\":\"publishHook\",\"version\":\"0.1.0-SNAPSHOT\",\"status\":\"unprocessed\",\"lastUpdatedBy\":\"publishHook\",\"notes\":null,\"operation\":\"INSERT\",\"entityName\":\"Country\",\"objectType\":\"esbEvents\",\"identity#\":3}]",
                findResponse.getEntityData().toString(),
                false);
    }
}
