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

    public static final String PROPERTY_ROOT_ENTITY_NAME = "esbRootEntityName";
    public static final String PROPERTY_EVENT_ENTITY_NAME = "esbEventEntityName";
    public static final String PROPERTY_END_SYSTEM = "endSystem";
    public static final String PROPERTY_DEFAULT_PRIORITY = "defaultPriority";
    public static final String PROPERTY_HEADERS = "headers";
    public static final String PROPERTY_HEADER_NAME = "name";
    public static final String PROPERTY_HEADER_VALUE = "value";
    public static final String PROPERTY_IDENTITY_CONFIGURATIONS = "identityConfigurations";
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

            Object identityConfigurationArray = p.newArrayField(emptyNode, PROPERTY_IDENTITY_CONFIGURATIONS);
            for (IdentityConfiguration identityConfiguration : config.getIdentityConfigurations()) {
                T identityConfigurationObject = p.newNode();
                p.putValue(identityConfigurationObject, PROPERTY_ROOT_ENTITY_NAME, identityConfiguration.getEsbRootEntityName());
                if (identityConfiguration.getEsbEventEntityName() != null) {
                    p.putValue(identityConfigurationObject, PROPERTY_EVENT_ENTITY_NAME, identityConfiguration.getEsbEventEntityName());
                }
                p.putValue(identityConfigurationObject, PROPERTY_END_SYSTEM, identityConfiguration.getEndSystem());
                p.putValue(identityConfigurationObject, PROPERTY_DEFAULT_PRIORITY, identityConfiguration.getDefaultPriority().toString());
                p.putProjection(identityConfigurationObject, PROPERTY_INTEGRATED_FIELDS_PROJECTION, identityConfiguration.getIntegratedFieldsProjection());
                p.putProjection(identityConfigurationObject, PROPERTY_IDENTITY_PROJECTION, identityConfiguration.getIdentityProjection());
                if (identityConfiguration.getRootIdentityFields() != null) {
                    Object rootIdentityFieldsArray = p.newArrayField(identityConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
                    for (String rootIdentityField : identityConfiguration.getRootIdentityFields()) {
                        p.addStringToArray(rootIdentityFieldsArray, rootIdentityField);
                    }
                }
                Object headersArray = p.newArrayField(identityConfigurationObject, PROPERTY_HEADERS);
                for (Header h : identityConfiguration.getHeaders()) {
                    T headerObject = p.newNode();
                    p.putString(headerObject, PROPERTY_HEADER_NAME, h.getName());
                    p.putString(headerObject, PROPERTY_HEADER_VALUE, h.getValue());
                    p.addObjectToArray(headersArray, headerObject);
                }
                p.addObjectToArray(identityConfigurationArray, identityConfigurationObject);
            }
        }
    }

    @Override
    public HookConfiguration parse(String name, MetadataParser<T> parser, T node) {
        List<Header> headers = new ArrayList<>();
        List<IdentityConfiguration> identityConfigurations = new ArrayList<>();
        for (T configuration : parser.getObjectList(node, PROPERTY_IDENTITY_CONFIGURATIONS)) {
            String esbRootEntityName = parser.getRequiredStringProperty(configuration, PROPERTY_ROOT_ENTITY_NAME);
            String esbEventEntityName = parser.getStringProperty(configuration, PROPERTY_EVENT_ENTITY_NAME);
            String endSystem = parser.getRequiredStringProperty(configuration, PROPERTY_END_SYSTEM);
            Integer defaultPriority = Integer.parseInt(parser.getRequiredStringProperty(configuration, PROPERTY_DEFAULT_PRIORITY));
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

            IdentityConfiguration conf = new IdentityConfiguration(
                    esbRootEntityName,
                    esbEventEntityName,
                    endSystem,
                    defaultPriority,
                    integratedFieldsProjection,
                    identityFieldsProjection,
                    rootIdentityFields,
                    headers);
            identityConfigurations.add(conf);
        }

        return new PublishHookConfiguration(identityConfigurations);
    }
}
