package com.redhat.lightblue.hook.publish;

import java.util.List;

import com.redhat.lightblue.hook.publish.model.Header;
import com.redhat.lightblue.hook.publish.model.IntegrationConfiguration;
import com.redhat.lightblue.metadata.HookConfiguration;

/**
 *
 * @author vrjain
 */
public class PublishHookConfiguration implements HookConfiguration {

    private static final long serialVersionUID = -2297815875083279355L;
    private final String entityName;
    private final String rootEntityName;
    private final String endSystem;
    private final String defaultPriority;
    private final List<Header> headers;
    private final IntegrationConfiguration onAdd;
    private final List<IntegrationConfiguration> onUpdate;

    public PublishHookConfiguration(String entityName, String rootEntityName, String endSystem, String defaultPriority, List<Header> headers,
            IntegrationConfiguration onAdd, List<IntegrationConfiguration> onUpdate) {
        this.entityName = entityName;
        this.rootEntityName = rootEntityName;
        this.endSystem = endSystem;
        this.defaultPriority = defaultPriority;
        this.headers = headers;
        this.onAdd = onAdd;
        this.onUpdate = onUpdate;
    }

    public String getEntityName() {
        return entityName;
    }
    public String getRootEntityName() {
        return rootEntityName;
    }
    public String getEndSystem() {
        return endSystem;
    }
    public String getDefaultPriority() {
        return defaultPriority;
    }
    public List<Header> getHeaders() {
        return headers;
    }
    public IntegrationConfiguration getOnAdd() {
        return onAdd;
    }
    public List<IntegrationConfiguration> getOnUpdate() {
        return onUpdate;
    }

}
