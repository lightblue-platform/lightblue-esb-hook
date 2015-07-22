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
    public static final String PROPERTY_ENTITY_NAME = "entityName";
    public static final String PROPERTY_ROOT_ENTITY_NAME = "rootEntityName";
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
            p.putValue(emptyNode, PROPERTY_ENTITY_NAME, config.getEntityName());
            p.putValue(emptyNode, PROPERTY_ROOT_ENTITY_NAME, config.getRootEntityName());
            p.putValue(emptyNode, PROPERTY_END_SYSTEM, config.getEndSystem());
            p.putValue(emptyNode, PROPERTY_DEFAULT_PRIORITY, config.getDefaultPriority());
            Object headersArray = p.newArrayField(emptyNode, PROPERTY_HEADERS);
            for (Header h : config.getHeaders()) {
                T headerObject = p.newNode();
                p.putString(headerObject, PROPERTY_HEADER_NAME, h.getName());
                p.putString(headerObject, PROPERTY_HEADER_VALUE, h.getValue());
                p.addObjectToArray(headersArray, headerObject);
            }

            Object identityConfigurationArray = p.newArrayField(emptyNode, PROPERTY_IDENTITY_CONFIGURATIONS);
            for (IdentityConfiguration identityConfiguration : config.getIdentityConfigurations()) {
                T identityConfigurationObject = p.newNode();
                p.putProjection(identityConfigurationObject, PROPERTY_INTEGRATED_FIELDS_PROJECTION, identityConfiguration.getIntegratedFieldsProjection());
                p.putProjection(identityConfigurationObject, PROPERTY_IDENTITY_PROJECTION, identityConfiguration.getIdentityProjection());
                if (identityConfiguration.getRootIdentityFields() != null) {
                    Object rootIdentityFieldsArray = p.newArrayField(identityConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
                    for (String rootIdentityField : identityConfiguration.getRootIdentityFields()) {
                        p.addStringToArray(rootIdentityFieldsArray, rootIdentityField);
                    }
                }
                p.addObjectToArray(identityConfigurationArray, identityConfigurationObject);
            }
        }
    }

    @Override
    public HookConfiguration parse(String name, MetadataParser<T> parser, T node) {
        String entityName = parser.getRequiredStringProperty(node, PROPERTY_ENTITY_NAME);
        String rootEntityName = parser.getRequiredStringProperty(node, PROPERTY_ROOT_ENTITY_NAME);
        String endSystem = parser.getRequiredStringProperty(node, PROPERTY_END_SYSTEM);
        String defaultPriority = parser.getRequiredStringProperty(node, PROPERTY_DEFAULT_PRIORITY);
        List<Header> headers = new ArrayList<>();
        List<T> headerConfigurations = parser.getObjectList(node, PROPERTY_HEADERS);
        if (headerConfigurations != null) {
            for (T headerConfiguration : headerConfigurations) {
                Header header = new Header();
                header.setName(parser.getRequiredStringProperty(headerConfiguration, PROPERTY_HEADER_NAME));
                header.setValue(parser.getRequiredStringProperty(headerConfiguration, PROPERTY_HEADER_VALUE));
                headers.add(header);
            }
        }

        List<IdentityConfiguration> identityConfigurations = new ArrayList<>();
        for (T configuration : parser.getObjectList(node, PROPERTY_IDENTITY_CONFIGURATIONS)) {
            Projection integratedFieldsProjection = parser.getProjection(configuration, PROPERTY_INTEGRATED_FIELDS_PROJECTION);
            Projection identityFieldsProjection = parser.getProjection(configuration, PROPERTY_IDENTITY_PROJECTION);
            List<String> rootIdentityFields = parser.getStringList(configuration, PROPERTY_ROOT_IDENTITY_FIELDS);
            IdentityConfiguration conf = new IdentityConfiguration(integratedFieldsProjection, identityFieldsProjection, rootIdentityFields);
            identityConfigurations.add(conf);
        }
        return new PublishHookConfiguration(entityName, rootEntityName, endSystem, defaultPriority, headers, identityConfigurations);
    }
}
