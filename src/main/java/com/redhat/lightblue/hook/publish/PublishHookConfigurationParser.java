package com.redhat.lightblue.hook.publish;

import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.metadata.parser.HookConfigurationParser;
import com.redhat.lightblue.metadata.parser.MetadataParser;

public class PublishHookConfigurationParser<T> implements HookConfigurationParser<T> {
    public static final String PROPERTY_ENTITY_NAME = "entityName";
    public static final String PROPERTY_ROOT_ENTITY_NAME = "rootEntityName";
    public static final String PROPERTY_END_SYSTEM = "endSystem";
    public static final String PROPERTY_DEFAULT_PRIORITY = "defaultPriority";

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
        }
    }

    @Override
    public HookConfiguration parse(String name, MetadataParser<T> parser, T node) {
        String entityName = parser.getRequiredStringProperty(node, PROPERTY_ENTITY_NAME);
        String rootEntityName = parser.getRequiredStringProperty(node, PROPERTY_ROOT_ENTITY_NAME);
        String endSystem = parser.getRequiredStringProperty(node, PROPERTY_END_SYSTEM);
        String defaultPriority = parser.getRequiredStringProperty(node, PROPERTY_DEFAULT_PRIORITY);

        return new PublishHookConfiguration(entityName, rootEntityName, endSystem, defaultPriority);
    }

}
