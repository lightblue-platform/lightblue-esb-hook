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
    public static final String PROPERTY_INTEGRATION_CONFIGURATIONS = "integrationConfigurations";
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
            for(Header h : c.getHeaders()) {
                T headerObject = p.newNode();
                p.putString(headerObject, PROPERTY_HEADER_NAME, h.getName() );
                p.putString(headerObject, PROPERTY_HEADER_VALUE, h.getValue() );
                p.addObjectToArray(headersArray, headerObject);
            }
            Object integrationConfigurationArray = p.newArrayField(emptyNode, PROPERTY_INTEGRATION_CONFIGURATIONS);
            for(IntegrationConfiguration integrationConfiguration : c.getIdentityConfigurations()) {
                T integrationConfigurationObject = p.newNode();
                Object integrationFieldsArray = p.newArrayField(integrationConfigurationObject, PROPERTY_INTEGRATED_FIELDS);
                for(String integrationField : integrationConfiguration.getIntegratedFields()) {
                    p.addStringToArray(integrationFieldsArray, integrationField);
                }
                Object identityFieldsArray = p.newArrayField(integrationConfigurationObject, PROPERTY_IDENTITY_FIELDS);
                for(String identityField : integrationConfiguration.getIdentityFields()) {
                    p.addStringToArray(identityFieldsArray, identityField);
                }
                Object rootIdentityFieldsArray = p.newArrayField(integrationConfigurationObject, PROPERTY_ROOT_IDENTITY_FIELDS);
                for(String rootIdentityField : integrationConfiguration.getRootIdentityFields()) {
                    p.addStringToArray(rootIdentityFieldsArray, rootIdentityField);
                }
                p.addObjectToArray(integrationConfigurationArray, integrationConfigurationObject);
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
        for(T header : parser.getObjectList(node,PROPERTY_HEADERS)) {
            Header h = new Header();
            h.setName(parser.getRequiredStringProperty(header, PROPERTY_HEADER_NAME));
            h.setValue(parser.getRequiredStringProperty(header, PROPERTY_HEADER_VALUE));
            headers.add(h);
        }
        List<IntegrationConfiguration> identityConfigurations = new ArrayList<>();
        for(T configuration: parser.getObjectList(node,PROPERTY_INTEGRATION_CONFIGURATIONS)) {
            List<String> integratedFields = new ArrayList<>();
            for(String integratedField: parser.getStringList(configuration, PROPERTY_INTEGRATED_FIELDS)) {
                integratedFields.add(integratedField);
            }
            List<String> identityFields = new ArrayList<>();
            for(String identityField: parser.getStringList(configuration, PROPERTY_IDENTITY_FIELDS)) {
                identityFields.add(identityField);
            }
            List<String> rootIdentityFields = new ArrayList<>();
            for(String rootIdentityField: parser.getStringList(configuration, PROPERTY_ROOT_IDENTITY_FIELDS)) {
                rootIdentityFields.add(rootIdentityField);
            }
            IntegrationConfiguration conf = new IntegrationConfiguration(integratedFields, identityFields, rootIdentityFields);
            identityConfigurations.add(conf);
        }
        return new PublishHookConfiguration(entityName, rootEntityName, endSystem, defaultPriority, headers, identityConfigurations);
    }

}
