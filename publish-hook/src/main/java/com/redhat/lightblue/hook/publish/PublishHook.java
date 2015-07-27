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
    public static final String ERR_MISSING_ID = HOOK_NAME + ":MissingID";

    private LightblueFactory lightblueFactory;
    private static transient JsonNodeFactory factory = JsonNodeFactory.withExactBigDecimals(true);

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

            for (IdentityConfiguration configuration : publishHookConfiguration.getIdentityConfigurations()) {

                try {
                    Projection integratedFieldsProjection = Projection
                            .add(configuration.getIdentityProjection(), configuration.getIntegratedFieldsProjection());
                    Projector identityProjector = Projector.getInstance(configuration.getIdentityProjection(), entityMetadata);
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
                            Set<Event> extractedEvents = EventExctractionUtil.compareAndExtractEvents(integrationProjectedPreDoc, integrationProjectedPostDoc,
                                    identityProjectedPostDoc);
                            for (Event event : extractedEvents) {

                                event.setEntityName(publishHookConfiguration.getEntityName());
                                event.setVersion(doc.getEntityMetadata().getVersion().getValue());
                                event.setEsbRootEntityName(publishHookConfiguration.getEsbRootEntityName());
                                event.setEsbEventEntityName(publishHookConfiguration.getEsbEventEntityName());
                                event.setEndSystem(publishHookConfiguration.getEndSystem());
                                event.setPriorityValue(Integer.parseInt(publishHookConfiguration.getDefaultPriority()));
                                event.setCreatedBy(HOOK_NAME);
                                event.setCreationDate(doc.getWhen());
                                event.addHeaders(publishHookConfiguration.getHeaders());
                                event.setLastUpdatedBy(HOOK_NAME);
                                event.setLastUpdateDate(doc.getWhen());
                                event.setStatus(Event.STATE_UNPROCESSED);
                                event.setEventSource(doc.getWho());
                                if (configuration.getRootIdentityFields() != null && configuration.getRootIdentityFields().size() > 0) {
                                    event.addRootIdentities(getRootIdentities(event.getIdentity(), configuration.getRootIdentityFields()));
                                }
                                try {
                                    insert(ENTITY_NAME, event);
                                } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                                        | InstantiationException | IOException e) {
                                    LOGGER.error("Unexpected error", e);
                                }

                            }
                        }
                    }
                } catch (IllegalArgumentException | JSONException e) {
                    LOGGER.error(e.toString());
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
