package com.redhat.lightblue.hook.publish;

import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.metadata.parser.HookConfigurationParser;
import com.redhat.lightblue.metadata.parser.MetadataParser;

public class PublishHookConfigurationParser<T> implements HookConfigurationParser<T> {

    @Override
    public String getName() {
        return PublishHook.HOOK_NAME;
    }

    @Override
    public CRUDHook getCRUDHook() {
        return new PublishHook();
    }

    @Override
    public void convert(MetadataParser<T> parser, T emptyNode, HookConfiguration object) {
        // TODO Auto-generated method stub

    }

    @Override
    public HookConfiguration parse(String name, MetadataParser<T> parser, T node) {
        return new PublishHookConfiguration();
    }

}
