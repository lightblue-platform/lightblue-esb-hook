package com.redhat.lightblue.hook.publish;

import static com.redhat.lightblue.test.Assert.assertNoDataErrors;
import static com.redhat.lightblue.test.Assert.assertNoErrors;
import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;

import java.util.Set;
import java.util.HashSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.skyscreamer.jsonassert.JSONAssert;

import com.redhat.lightblue.Response;
import com.redhat.lightblue.crud.FindRequest;
import com.redhat.lightblue.crud.InsertionRequest;
import com.redhat.lightblue.crud.UpdateRequest;
import com.redhat.lightblue.mongo.test.AbstractMongoCRUDTestController;

public abstract class BasePublishHookTest extends AbstractMongoCRUDTestController {

    protected static final String ESB_EVENTS_VERSION = "0.0.1-SNAPSHOT";
    protected static final String COUNTRY_VERSION = "0.1.0-SNAPSHOT";

    @BeforeClass
    public static void preparePublishHookDatasources() {
        System.setProperty("mongo.datasource", "mongodata");
    }

    public BasePublishHookTest() throws Exception {
        super();
    }

    @Override
    public Set<String> getHooksToRemove() {
        return new HashSet<>();
    }
    
    @Before
    public void before() throws UnknownHostException {
        cleanupMongoCollections("country", "esbEvents");
    }

    protected void insertCountry() throws Exception {
        com.mongodb.DBCursor cursor=mongoServer.getConnection().getDB("testdb").getCollection("metadata").find();
        while(cursor.hasNext())
            System.out.println(cursor.next());
        Response insertResponse = getLightblueFactory().getMediator().insert(
                createRequest_FromJsonString(InsertionRequest.class, "{\"entity\":\"country\",\"entityVersion\":\"" + COUNTRY_VERSION + "\",\"data\":["
                        + "{\"_id\":\"12312312312\",\"name\":\"United States\",\"iso2Code\":\"123\",\"iso3Code\":\"456\","
                        + "\"optionalField\":[{\"myRandomField\":\"taylor\",\"mySpecificField\":\"swift\"}]}]}"));
        assertNoErrors(insertResponse);
        assertNoDataErrors(insertResponse);
        assertEquals(1, insertResponse.getModifiedCount());
    }

    protected void verifyEvent(int expectedEvents, String query, String expectedIdentityFields, String expectedFields, String operation) throws Exception {

        Response findResponse = getLightblueFactory().getMediator().find(
                createRequest_FromJsonString(FindRequest.class, "{\"entity\":\"esbEvents\",\"entityVersion\":\"" + ESB_EVENTS_VERSION + "\"," + "\"query\":"
                        + query + "," + "\"projection\": [{\"field\":\"*\",\"include\":true,\"recursive\":true}]}"));
        assertNoErrors(findResponse);
        assertNoDataErrors(findResponse);
        assertEquals(expectedEvents, findResponse.getMatchCount());
        // dates and uids had to be left out to prevent test from always
        // failing.
        if (expectedEvents > 0) {
            JSONAssert.assertEquals("[{\"identity\":" + expectedIdentityFields
                    + ",\"esbRootEntityName\":\"Country\",\"esbEventEntityName\":\"State\",\"endSystem\":\"nameOfTargetSystem\",\"createdBy\":\"publishHook\",\"version\":\"0.1.0-SNAPSHOT\""
                    + ",\"status\":\"UNPROCESSED\",\"lastUpdatedBy\":\"publishHook\",\"notes\":null,\"operation\":\"" + operation + "\","
                    + "\"entityName\":\"country\",\"objectType\":\"esbEvents\"" + expectedFields + "}]", findResponse.getEntityData().toString(), false);
        }
    }

    protected void updateCountry(int expectedUpdates, String updates) throws Exception {
        Response updateResponse = getLightblueFactory().getMediator().update(
                createRequest_FromJsonString(UpdateRequest.class, "{ \"entity\":\"country\",\"entityVersion\":\"" + COUNTRY_VERSION + "\","
                        + "\"projection\": [ { \"field\": \"*\", \"include\": \"true\", \"recursive\": true } ],"
                        + " \"query\": { \"field\": \"_id\", \"op\": \"=\", \"rvalue\": \"12312312312\" }," + " \"update\":" + updates + "}"));
        assertNoErrors(updateResponse);
        assertNoDataErrors(updateResponse);
        assertEquals(expectedUpdates, updateResponse.getModifiedCount());

    }

}
