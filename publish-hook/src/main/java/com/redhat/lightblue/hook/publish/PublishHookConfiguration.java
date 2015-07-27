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

    private final List<IdentityConfiguration> identityConfigurations;

    public PublishHookConfiguration(List<IdentityConfiguration> identityConfigurations) {
        this.identityConfigurations = identityConfigurations;
    }

    public List<IdentityConfiguration> getIdentityConfigurations() {
        return identityConfigurations;
    }

}
