package com.redhat.lightblue.hook.publish;

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

    public PublishHookConfiguration(String entityName, String rootEntityName, String endSystem, String defaultPriority) {
        this.entityName = entityName;
        this.rootEntityName = rootEntityName;
        this.endSystem = endSystem;
        this.defaultPriority = defaultPriority;
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

}
