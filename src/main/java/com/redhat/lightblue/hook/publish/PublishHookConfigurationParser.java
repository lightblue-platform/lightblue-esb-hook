package com.redhat.lightblue.hook.publish;

import java.util.ArrayList;
import java.util.List;

import com.redhat.lightblue.hook.publish.model.Header;
import com.redhat.lightblue.hook.publish.model.IntegrationConfiguration;
import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.metadata.parser.HookConfigurationParser;
import com.redhat.lightblue.metadata.parser.MetadataParser;

public class PublishHookConfigurationParser<T> implements HookConfigurationParser<T> {
    public static final String PROPERTY_ENTITY_NAME = "entityName";
    public static final String PROPERTY_ROOT_ENTITY_NAME = "rootEntityName";
    public static final String PROPERTY_END_SYSTEM = "endSystem";
    public static final String PROPERTY_DEFAULT_PRIORITY = "defaultPriority";
    public static final String PROPERTY_HEADERS = "headers";
    public static final String PROPERTY_HEADER_NAME = "name";
    public static final String PROPERTY_HEADER_VALUE = "value";
    public static final String PROPERTY_ON_ADD = "onAdd";
    public static final String PROPERTY_ON_UPDATE = "onUpdate";
    public static final String PROPERTY_INTEGRATED_FIELDS = "integratedFields";
    public static final String PROPERTY_IDENTITY_FIELDS = "identityfields";
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
            PublishHookConfiguration c = (PublishHookConfiguration) object;
            p.putValue(emptyNode, PROPERTY_ENTITY_NAME, c.getEntityName());
            p.putValue(emptyNode, PROPERTY_ROOT_ENTITY_NAME, c.getRootEntityName());
            p.putValue(emptyNode, PROPERTY_END_SYSTEM, c.getEndSystem());
            p.putValue(emptyNode, PROPERTY_DEFAULT_PRIORITY, c.getDefaultPriority());
            Object headersArray = p.newArrayField(emptyNode, PROPERTY_HEADERS);
            for (Header h : c.getHeaders()) {
                T headerObject = p.newNode();
                p.putString(headerObject, PROPERTY_HEADER_NAME, h.getName());
                p.putString(headerObject, PROPERTY_HEADER_VALUE, h.getValue());
                p.addObjectToArray(headersArray, headerObject);
            }

            T onAddConfigurationObject = p.newNode();
            p.putObject(emptyNode, PROPERTY_ON_ADD, onAddConfigurationObject);
            Object onAddIdentityFieldsArray = p.newArrayField(onAddConfigurationObject, PROPERTY_IDENTITY_FIELDS);
            for (String identityField : c.getOnAdd().getIdentityFields()) {
                p.addStringToArray(onAddIdentityFieldsArray, identityField);
            }
            if (c.getOnAdd().getRootIdentityFields() != null) {
                Object onAddRootIdentityFieldsArray = p.newArrayField(onAddConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
                for (String rootIdentityField : c.getOnAdd().getRootIdentityFields()) {
                    p.addStringToArray(onAddRootIdentityFieldsArray, rootIdentityField);
                }
            }

            Object onUpdateArray = p.newArrayField(emptyNode, PROPERTY_ON_UPDATE);
            for (IntegrationConfiguration onUpdateConfiguration : c.getOnUpdate()) {
                T onUpdateConfigurationObject = p.newNode();
                Object onUpdateIntegrationFieldsArray = p.newArrayField(onUpdateConfigurationObject, PROPERTY_INTEGRATED_FIELDS);
                for (String integrationField : onUpdateConfiguration.getIntegratedFields()) {
                    p.addStringToArray(onUpdateIntegrationFieldsArray, integrationField);
                }
                Object onUpdateIdentityFieldsArray = p.newArrayField(onUpdateConfigurationObject, PROPERTY_IDENTITY_FIELDS);
                for (String identityField : onUpdateConfiguration.getIdentityFields()) {
                    p.addStringToArray(onUpdateIdentityFieldsArray, identityField);
                }
                if (onUpdateConfiguration.getRootIdentityFields() != null) {
                    Object onUpdateRootIdentityFieldsArray = p.newArrayField(onUpdateConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
                    for (String rootIdentityField : onUpdateConfiguration.getRootIdentityFields()) {
                        p.addStringToArray(onUpdateRootIdentityFieldsArray, rootIdentityField);
                    }
                }
                p.addObjectToArray(onUpdateArray, onUpdateConfigurationObject);
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

        T onAddConfigurationObject = parser.getObjectProperty(node, PROPERTY_ON_ADD);
        List<String> onAddIdentityFields = parser.getStringList(onAddConfigurationObject, PROPERTY_IDENTITY_FIELDS);
        List<String> onAddRootIdentityFields = parser.getStringList(onAddConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
        IntegrationConfiguration onAddConfiguration = new IntegrationConfiguration(null, onAddIdentityFields, onAddRootIdentityFields);

        List<IntegrationConfiguration> onUpdateConfigurations = new ArrayList<>();
        for (T configuration : parser.getObjectList(node, PROPERTY_ON_UPDATE)) {
            List<String> integratedFields = parser.getStringList(configuration, PROPERTY_INTEGRATED_FIELDS);
            List<String> identityFields = parser.getStringList(configuration, PROPERTY_IDENTITY_FIELDS);
            List<String> rootIdentityFields = parser.getStringList(configuration, PROPERTY_ROOT_IDENTITY_FIELDS);
            IntegrationConfiguration conf = new IntegrationConfiguration(integratedFields, identityFields, rootIdentityFields);
            onUpdateConfigurations.add(conf);
        }
        return new PublishHookConfiguration(entityName, rootEntityName, endSystem, defaultPriority, headers, onAddConfiguration, onUpdateConfigurations);
    }
}
