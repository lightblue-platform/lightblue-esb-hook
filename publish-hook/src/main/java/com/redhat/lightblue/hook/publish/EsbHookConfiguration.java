package com.redhat.lightblue.hook.publish;

import java.util.List;

import com.redhat.lightblue.metadata.HookConfiguration;

/**
 *
 * @author vrjain
 */
public class EsbHookConfiguration implements HookConfiguration {

    private static final long serialVersionUID = -2297815875083279355L;

    private final List<EventConfiguration> eventConfigurations;

    public EsbHookConfiguration(List<EventConfiguration> eventConfigurations) {
        this.eventConfigurations = eventConfigurations;
    }

    public List<EventConfiguration> getEventConfigurations() {
        return eventConfigurations;
    }

}
