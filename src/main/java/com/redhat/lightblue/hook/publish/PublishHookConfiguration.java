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
    private final List<IntegrationConfiguration> identityConfigurations;

    public PublishHookConfiguration(String entityName, String rootEntityName, String endSystem, String defaultPriority, List<Header> headers, List<IntegrationConfiguration> idenityConfigurations) {
        this.entityName = entityName;
        this.rootEntityName = rootEntityName;
        this.endSystem = endSystem;
        this.defaultPriority = defaultPriority;
        this.headers = headers;
        this.identityConfigurations = idenityConfigurations;
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
    public List<IntegrationConfiguration> getIdentityConfigurations() {
        return identityConfigurations;
    }

}
