package com.redhat.lightblue.hook.publish;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.lightblue.DataError;
import com.redhat.lightblue.Response;
import com.redhat.lightblue.config.LightblueFactory;
import com.redhat.lightblue.config.LightblueFactoryAware;
import com.redhat.lightblue.crud.InsertionRequest;
import com.redhat.lightblue.hook.publish.model.Event;
import com.redhat.lightblue.hook.publish.model.Identity;
import com.redhat.lightblue.hook.publish.model.IntegrationConfiguration;
import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.hooks.HookDoc;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.util.Error;
import com.redhat.lightblue.util.JsonDoc;
import com.redhat.lightblue.util.Path;

public class PublishHook implements CRUDHook, LightblueFactoryAware {

    private final Logger LOGGER = LoggerFactory.getLogger(PublishHook.class);

    public static final String HOOK_NAME = "publishHook";
    public static final String ENTITY_NAME = "esbEvents";
    public static final String ERR_MISSING_ID = HOOK_NAME + ":MissingID";
    public static final String INSERT_OPERATION = "INSERT";
    public static final String DELETE_OPERATION = "DELETE";
    public static final String UPDATE_OPERATION = "UPDATE";

    private LightblueFactory lightblueFactory;

    @Override
    public String getName() {
        return HOOK_NAME;
    }

    @Override
    public void setLightblueFactory(LightblueFactory factory) {
        this.lightblueFactory = factory;
    }

    @Override
    public void processHook(EntityMetadata entityMetadata, HookConfiguration hookConfiguration, List<HookDoc> docs) {
        if (!(hookConfiguration instanceof PublishHookConfiguration)) {
            throw new IllegalArgumentException("Only instances of PublishHookConfiguration are supported.");
        }

        PublishHookConfiguration publishHookConfiguration = (PublishHookConfiguration) hookConfiguration;

        for (HookDoc doc : docs) {

            if (doc.getPreDoc() == null) {
                insertEvent(publishHookConfiguration, doc, INSERT_OPERATION, publishHookConfiguration.getOnAdd().getIdentityFields(), publishHookConfiguration
                        .getOnAdd().getRootIdentityFields());
                break;
            } else if (doc.getPostDoc() == null) {
                // there is no point of firing an event
                break;
            }

            // for each configuration, if any of the integrated fields have
            // changed, fire an event for the first field that changed
            for (IntegrationConfiguration configuration : publishHookConfiguration.getOnUpdate()) {
                for (String integratedField : configuration.getIntegratedFields()) {
                    Path fieldPath = new Path(integratedField);

                    try {
                        JsonNode preDocField = doc.getPreDoc().get(fieldPath);
                        JsonNode postDocField = doc.getPostDoc().get(fieldPath);

                        if (preDocField != null || postDocField != null) {
                            if (preDocField == null) {
                                insertEvent(publishHookConfiguration, doc, INSERT_OPERATION, configuration.getIdentityFields(),
                                        configuration.getRootIdentityFields());
                                break;
                            } else if (postDocField == null) {
                                insertEvent(publishHookConfiguration, doc, DELETE_OPERATION, configuration.getIdentityFields(),
                                        configuration.getRootIdentityFields());
                                break;
                            } else if (JSONCompare.compareJSON(preDocField.toString(), postDocField.toString(), JSONCompareMode.LENIENT).passed()) {
                                insertEvent(publishHookConfiguration, doc, UPDATE_OPERATION, configuration.getIdentityFields(),
                                        configuration.getRootIdentityFields());
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        LOGGER.error(e.toString());
                    }
                }
            }

        }
    }

    private void insertEvent(PublishHookConfiguration publishHookConfiguration, HookDoc doc, String operation, List<String> identityFields,
            List<String> rootIdentityFields) {

        Event event = new Event();
        event.setEntityName(publishHookConfiguration.getEntityName());
        event.setRootEntityName(publishHookConfiguration.getRootEntityName());
        event.setEndSystem(publishHookConfiguration.getEndSystem());
        event.setVersion(doc.getEntityMetadata().getVersion().getValue());
        event.setPriorityValue(Integer.parseInt(publishHookConfiguration.getDefaultPriority()));
        event.setCreatedBy(HOOK_NAME);
        event.setCreationDate(new Date());
        event.addHeaders(publishHookConfiguration.getHeaders());
        event.setLastUpdatedBy(HOOK_NAME);
        event.setLastUpdateDate(new Date());
        event.setOperation(operation);
        event.setStatus("UNPROCESSED");
        event.setEventSource(doc.getWho());
        event.addIdentities(getIdentityFields(identityFields, doc.getPostDoc()));
        if (rootIdentityFields != null) {
            event.addRootIdentities(getIdentityFields(rootIdentityFields, doc.getPostDoc()));
        }
        try {
            insert(ENTITY_NAME, event);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | IOException e) {
            // TODO Better Handle Exception
            LOGGER.error("Unexpected error", e);
        }
    }

    private List<Identity> getIdentityFields(List<String> fieldNames, JsonDoc doc) {
        List<Identity> result = new ArrayList<>();
        for (String identityField : fieldNames) {
            Path fieldPath = new Path(identityField);
            JsonNode field = doc.get(fieldPath);
            if (field != null) {
                Identity identity = new Identity();
                identity.setField(identityField);
                identity.setValue(field.asText());
                result.add(identity);
            } else {
                throw Error.get(ERR_MISSING_ID, "path:" + identityField + " in " + doc);
            }
        }
        return result;
    }

    private void insert(String entityName, Object entity) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, IOException,
            NoSuchMethodException, InstantiationException {
        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        jsonNode.put("entity", entityName);
        ArrayNode data = jsonNode.putArray("data");
        data.add(new ObjectMapper().valueToTree(entity));

        InsertionRequest ireq = InsertionRequest.fromJson(jsonNode);
        Response r = lightblueFactory.getMediator().insert(ireq);
        if (!r.getErrors().isEmpty()) {
            // TODO Better Handle Exception
            for (Error e : r.getErrors()) {
                LOGGER.error(e.toString());
            }
        } else if (!r.getDataErrors().isEmpty()) {
            // TODO Better Handle Exception
            for (DataError e : r.getDataErrors()) {
                LOGGER.error(e.toString());
            }
        }
    }

}
