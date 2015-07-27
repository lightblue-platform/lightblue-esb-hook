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

    private final List<Header> headers;
    private final List<IdentityConfiguration> identityConfigurations;

    public PublishHookConfiguration(List<Header> headers,
            List<IdentityConfiguration> identityConfigurations) {
        this.headers = headers;
        this.identityConfigurations = identityConfigurations;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<IdentityConfiguration> getIdentityConfigurations() {
        return identityConfigurations;
    }

}
