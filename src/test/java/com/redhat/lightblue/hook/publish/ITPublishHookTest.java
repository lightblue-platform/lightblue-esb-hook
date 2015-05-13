package com.redhat.lightblue.hook.publish;

import static com.redhat.lightblue.util.JsonUtils.json;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.mongo.test.AbstractMongoCRUDTestController;

public class ITPublishHookTest extends AbstractMongoCRUDTestController {

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
                json(loadResource("/metadata/publish.json", true))
        };
    }

    @Test
    public void test() {
        assertTrue(true);
        //TODO Something meaningful.
    }

}
