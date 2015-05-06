package com.redhat.lightblue.hook.publish;

import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.metadata.HookConfiguration;
import com.redhat.lightblue.metadata.parser.HookConfigurationParser;
import com.redhat.lightblue.metadata.parser.MetadataParser;

public class PublishHookConfigurationParser<T> implements HookConfigurationParser<T> {

    public String getName() {
        return PublishHook.HOOK_NAME;
    }

    public CRUDHook getCRUDHook() {
        return new PublishHook();
    }

    public void convert(MetadataParser<T> parser, T emptyNode, HookConfiguration object) {
        // TODO Auto-generated method stub

    }

    public HookConfiguration parse(String name, MetadataParser<T> parser, T node) {
        // TODO Auto-generated method stub
        return null;
    }

}
