package com.redhat.lightblue.hook.publish;

import java.util.ArrayList;
import java.util.List;

import com.redhat.lightblue.hook.publish.model.Header;
import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.metadata.parser.HookConfigurationParser;
import com.redhat.lightblue.metadata.parser.MetadataParser;
import com.redhat.lightblue.query.Projection;

public class PublishHookConfigurationParser<T> implements HookConfigurationParser<T> {

    public static final String PROPERTY_EVENT_CONFIGURATIONS = "eventConfigurations";
    public static final String PROPERTY_ROOT_ENTITY_NAME = "esbRootEntityName";
    public static final String PROPERTY_EVENT_ENTITY_NAME = "esbEventEntityName";
    public static final String PROPERTY_END_SYSTEM = "endSystem";
    public static final String PROPERTY_HEADERS = "headers";
    public static final String PROPERTY_HEADER_NAME = "name";
    public static final String PROPERTY_HEADER_VALUE = "value";
    public static final String PROPERTY_INTEGRATED_FIELDS_PROJECTION = "integratedFieldsProjection";
    public static final String PROPERTY_IDENTITY_PROJECTION = "identityProjection";
    public static final String PROPERTY_ROOT_IDENTITY_FIELDS = "rootIdentityfields";

    @Override
    public String getName() {
        return PublishHook.HOOK_NAME;
    }

    @Override
    public CRUDHook getCRUDHook() {
        return new PublishHook();
    }

    @Override
    public void convert(MetadataParser<T> p, T emptyNode, HookConfiguration object) {
        if (object instanceof PublishHookConfiguration) {
            PublishHookConfiguration config = (PublishHookConfiguration) object;

            Object eventConfigurationArray = p.newArrayField(emptyNode, PROPERTY_EVENT_CONFIGURATIONS);
            for (EventConfiguration eventConfiguration : config.getEventConfigurations()) {
                T eventConfigurationObject = p.newNode();
                p.putValue(eventConfigurationObject, PROPERTY_ROOT_ENTITY_NAME, eventConfiguration.getEsbRootEntityName());
                if (eventConfiguration.getEsbEventEntityName() != null) {
                    p.putValue(eventConfigurationObject, PROPERTY_EVENT_ENTITY_NAME, eventConfiguration.getEsbEventEntityName());
                }
                p.putValue(eventConfigurationObject, PROPERTY_END_SYSTEM, eventConfiguration.getEndSystem());
                p.putProjection(eventConfigurationObject, PROPERTY_INTEGRATED_FIELDS_PROJECTION, eventConfiguration.getIntegratedFieldsProjection());
                p.putProjection(eventConfigurationObject, PROPERTY_IDENTITY_PROJECTION, eventConfiguration.getIdentityProjection());
                if (eventConfiguration.getRootIdentityFields() != null) {
                    Object rootIdentityFieldsArray = p.newArrayField(eventConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
                    for (String rootIdentityField : eventConfiguration.getRootIdentityFields()) {
                        p.addStringToArray(rootIdentityFieldsArray, rootIdentityField);
                    }
                }
                Object headersArray = p.newArrayField(eventConfigurationObject, PROPERTY_HEADERS);
                for (Header h : eventConfiguration.getHeaders()) {
                    T headerObject = p.newNode();
                    p.putString(headerObject, PROPERTY_HEADER_NAME, h.getName());
                    p.putString(headerObject, PROPERTY_HEADER_VALUE, h.getValue());
                    p.addObjectToArray(headersArray, headerObject);
                }
                p.addObjectToArray(eventConfigurationArray, eventConfigurationObject);
            }
        }
    }

    @Override
    public HookConfiguration parse(String name, MetadataParser<T> parser, T node) {
        List<Header> headers = new ArrayList<>();
        List<EventConfiguration> eventConfigurations = new ArrayList<>();
        for (T configuration : parser.getObjectList(node, PROPERTY_EVENT_CONFIGURATIONS)) {
            String esbRootEntityName = parser.getRequiredStringProperty(configuration, PROPERTY_ROOT_ENTITY_NAME);
            String esbEventEntityName = parser.getRequiredStringProperty(configuration, PROPERTY_EVENT_ENTITY_NAME);
            String endSystem = parser.getRequiredStringProperty(configuration, PROPERTY_END_SYSTEM);
            Projection integratedFieldsProjection = parser.getProjection(configuration, PROPERTY_INTEGRATED_FIELDS_PROJECTION);
            Projection identityFieldsProjection = parser.getProjection(configuration, PROPERTY_IDENTITY_PROJECTION);
            List<String> rootIdentityFields = parser.getStringList(configuration, PROPERTY_ROOT_IDENTITY_FIELDS);
            List<T> headerConfigurations = parser.getObjectList(configuration, PROPERTY_HEADERS);
            if (headerConfigurations != null) {
                for (T headerConfiguration : headerConfigurations) {
                    Header header = new Header();
                    header.setName(parser.getRequiredStringProperty(headerConfiguration, PROPERTY_HEADER_NAME));
                    header.setValue(parser.getRequiredStringProperty(headerConfiguration, PROPERTY_HEADER_VALUE));
                    headers.add(header);
                }
            }

            EventConfiguration conf = new EventConfiguration(
                    esbRootEntityName,
                    esbEventEntityName,
                    endSystem,
                    integratedFieldsProjection,
                    identityFieldsProjection,
                    rootIdentityFields,
                    headers);
            eventConfigurations.add(conf);
        }

        return new PublishHookConfiguration(eventConfigurations);
    }
}
