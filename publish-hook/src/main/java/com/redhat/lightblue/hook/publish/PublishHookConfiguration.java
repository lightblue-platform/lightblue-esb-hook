package com.redhat.lightblue.hook.publish;

import java.util.List;

import com.redhat.lightblue.hook.publish.model.Header;
import com.redhat.lightblue.metadata.HookConfiguration;

/**
 *
 * @author vrjain
 */
public class PublishHookConfiguration implements HookConfiguration {

    private static final long serialVersionUID = -2297815875083279355L;
    private final String entityName;
    private final String esbRootEntityName;
    private final String esbEventEntityName;
    private final String endSystem;
    private final String defaultPriority;
    private final List<Header> headers;
    private final List<IdentityConfiguration> identityConfigurations;

    public PublishHookConfiguration(String entityName, String esbRootEntityName, String esbEventEntityName, String endSystem, String defaultPriority, List<Header> headers,
            List<IdentityConfiguration> identityConfigurations) {
        this.entityName = entityName;
        this.esbRootEntityName = esbRootEntityName;
        this.esbEventEntityName = esbEventEntityName;
        this.endSystem = endSystem;
        this.defaultPriority = defaultPriority;
        this.headers = headers;
        this.identityConfigurations = identityConfigurations;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEsbRootEntityName() {
        return esbRootEntityName;
    }

    public String getEsbEventEntityName() {
        return esbEventEntityName;
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

    public List<IdentityConfiguration> getIdentityConfigurations() {
        return identityConfigurations;
    }

}
