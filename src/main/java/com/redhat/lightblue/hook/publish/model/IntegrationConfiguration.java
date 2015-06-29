package com.redhat.lightblue.hook.publish.model;

import java.util.ArrayList;
import java.util.List;

public class IntegrationConfiguration {

    private List<String> integratedFields = new ArrayList<>();
    private List<String> identityFields = new ArrayList<>();
    private List<String> rootIdentityFields = new ArrayList<>();

    public IntegrationConfiguration(List<String> integratedFields, List<String> identityFields, List<String> rootIdentityFields) {
        this.integratedFields = integratedFields;
        this.identityFields = identityFields;
        this.rootIdentityFields = rootIdentityFields;
    }

    public List<String> getIntegratedFields() {
        return integratedFields;
    }
    public void setIntegratedFields(List<String> integratedFields) {
        this.integratedFields = integratedFields;
    }
    public List<String> getIdentityFields() {
        return identityFields;
    }
    public void setIdentityFields(List<String> identityFields) {
        this.identityFields = identityFields;
    }
    public List<String> getRootIdentityFields() {
        return rootIdentityFields;
    }
    public void setRootIdentityFields(List<String> rootIdentityFields) {
        this.rootIdentityFields = rootIdentityFields;
    }

}
