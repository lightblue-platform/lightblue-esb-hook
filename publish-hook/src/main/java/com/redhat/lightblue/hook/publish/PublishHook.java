package com.redhat.lightblue.hook.publish;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.lightblue.DataError;
import com.redhat.lightblue.Response;
import com.redhat.lightblue.config.LightblueFactory;
import com.redhat.lightblue.config.LightblueFactoryAware;
import com.redhat.lightblue.crud.InsertionRequest;
import com.redhat.lightblue.eval.Projector;
import com.redhat.lightblue.hook.publish.model.Event;
import com.redhat.lightblue.hook.publish.model.Identity;
import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.hooks.HookDoc;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.query.Projection;
import com.redhat.lightblue.util.Error;

public class PublishHook implements CRUDHook, LightblueFactoryAware {

    private final Logger LOGGER = LoggerFactory.getLogger(PublishHook.class);

    public static final String HOOK_NAME = "publishHook";
    public static final String ENTITY_NAME = "esbEvents";

    private LightblueFactory lightblueFactory;
    private static transient JsonNodeFactory factory = JsonNodeFactory.withExactBigDecimals(true);

    @Override
    public String getName() {
        return HOOK_NAME;
    }

    @Override
    public void setLightblueFactory(LightblueFactory factory) {
        lightblueFactory = factory;
    }

    @Override
    public void processHook(EntityMetadata entityMetadata, HookConfiguration hookConfiguration, List<HookDoc> docs) {
        if (!(hookConfiguration instanceof PublishHookConfiguration)) {
            throw new IllegalArgumentException("Only instances of PublishHookConfiguration are supported.");
        }

        PublishHookConfiguration publishHookConfiguration = (PublishHookConfiguration) hookConfiguration;

        for (HookDoc doc : docs) {

            for (EventConfiguration eventConfiguration : publishHookConfiguration.getEventConfigurations()) {

                try {
                    Projection integratedFieldsProjection = Projection
                            .add(eventConfiguration.getIdentityProjection(), eventConfiguration.getIntegratedFieldsProjection());
                    Projector identityProjector = Projector.getInstance(eventConfiguration.getIdentityProjection(), entityMetadata);
                    Projector integratedFieldsProjector = Projector.getInstance(integratedFieldsProjection, entityMetadata);

                    String integrationProjectedPreDoc = null, integrationProjectedPostDoc, identityProjectedPostDoc;
                    if (doc.getPreDoc() != null) {
                        integrationProjectedPreDoc = integratedFieldsProjector.project(doc.getPreDoc(), factory).toString();
                    }
                    // no point in creating events if post doc is not available
                    if (doc.getPostDoc() != null) {
                        integrationProjectedPostDoc = integratedFieldsProjector.project(doc.getPostDoc(), factory).toString();
                        identityProjectedPostDoc = identityProjector.project(doc.getPostDoc(), factory).toString();

                        if (doc.getPreDoc() == null
                                || JSONCompare.compareJSON(integrationProjectedPreDoc, integrationProjectedPostDoc, JSONCompareMode.LENIENT).failed()) {
                            LOGGER.debug("integrationProjectedPreDoc: {}", integrationProjectedPreDoc);
                            LOGGER.debug("integrationProjectedPostDoc: {}", integrationProjectedPostDoc);
                            LOGGER.debug("identityProjectedPostDoc: {}", identityProjectedPostDoc);
                            Set<Event> extractedEvents = EventExctractionUtil.compareAndExtractEvents(
                                    integrationProjectedPreDoc, 
                                    integrationProjectedPostDoc,
                                    identityProjectedPostDoc);
                            for (Event event : extractedEvents) {
                                event.setEntityName(doc.getEntityMetadata().getName());
                                event.setVersion(doc.getEntityMetadata().getVersion().getValue());

                                event.setEsbRootEntityName(eventConfiguration.getEsbRootEntityName());
                                event.setEsbEventEntityName(eventConfiguration.getEsbEventEntityName());
                                event.setEndSystem(eventConfiguration.getEndSystem());
                                event.setCreatedBy(HOOK_NAME);
                                event.setCreationDate(doc.getWhen());
                                event.addHeaders(eventConfiguration.getHeaders());
                                event.setLastUpdatedBy(HOOK_NAME);
                                event.setLastUpdateDate(doc.getWhen());
                                event.setStatus(Event.Status.UNPROCESSED);
                                event.setEventSource(doc.getWho());
                                if (eventConfiguration.getRootIdentityFields() != null && eventConfiguration.getRootIdentityFields().size() > 0) {
                                    event.addRootIdentities(getRootIdentities(event.getIdentity(), eventConfiguration.getRootIdentityFields()));
                                }

                                insert(ENTITY_NAME, event);
                            }
                        }
                    }
                } catch (IllegalArgumentException | JSONException e) {
                    LOGGER.error("Unexpected exception while preparing events for insertion.", e);
                }
            }
        }
    }

    private List<Identity> getRootIdentities(List<Identity> identities, List<String> rootIdentityFields) {
        List<Identity> rootIdentities = new ArrayList<>();
        if (rootIdentityFields != null && rootIdentityFields.size() > 0) {
            Map<String, Identity> map = new HashMap<>();
            for (Identity identity : identities) {
                map.put(identity.getField(), identity);
            }
            for (String rootIdentity : rootIdentityFields) {
                rootIdentities.add(map.get(rootIdentity));
            }
        }
        return rootIdentities;
    }

    private void insert(String entityName, Object entity) {
        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        jsonNode.put("entity", entityName);
        ArrayNode data = jsonNode.putArray("data");
        data.add(new ObjectMapper().valueToTree(entity));

        InsertionRequest ireq = InsertionRequest.fromJson(jsonNode);
        Response r;
        try {
            r = lightblueFactory.getMediator().insert(ireq);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | IOException e) {
            throw new RuntimeException("Lightblue is not configured properly.", e);
        }
        if (!r.getErrors().isEmpty()) {
            for (Error e : r.getErrors()) {
                LOGGER.error("Errors while attempting to insert esb events", e);
            }
        } else if (!r.getDataErrors().isEmpty()) {
            for (DataError e : r.getDataErrors()) {
                LOGGER.error("Data errors while attempting to insert esb events", e);
            }
        }
    }

}
