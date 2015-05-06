package com.redhat.lightblue.hook.publish;

import java.util.List;

import com.redhat.lightblue.hooks.CRUDHook;
import com.redhat.lightblue.hooks.HookDoc;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.HookConfiguration;

public class PublishHook implements CRUDHook {

    public static final String HOOK_NAME = "publishHook";

    public String getName() {
        return HOOK_NAME;
    }

    public void processHook(EntityMetadata arg0, HookConfiguration arg1, List<HookDoc> arg2) {
        // TODO Auto-generated method stub

    }

}
