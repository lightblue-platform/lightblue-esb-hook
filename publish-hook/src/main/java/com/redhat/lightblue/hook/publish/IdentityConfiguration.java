package com.redhat.lightblue.hook.publish;

import java.util.ArrayList;
import java.util.List;

import com.redhat.lightblue.query.Projection;

public class IdentityConfiguration {

    private Projection integratedFieldsProjection;
    private Projection identityProjection;
    private List<String> rootIdentityFields = new ArrayList<>();

    public IdentityConfiguration(Projection integratedFieldsProjection, Projection identityProjection, List<String> rootIdentityFields) {
        this.integratedFieldsProjection = integratedFieldsProjection;
        this.identityProjection = identityProjection;
        this.rootIdentityFields = rootIdentityFields;
    }

    public Projection getIntegratedFieldsProjection() {
        return integratedFieldsProjection;
    }
    public void setIntegratedFieldsProjection(Projection integratedFieldsProjection) {
        this.integratedFieldsProjection = integratedFieldsProjection;
    }
    public Projection getIdentityProjection() {
        return identityProjection;
    }
    public void setIdentityProjection(Projection identityProjection) {
        this.identityProjection = identityProjection;
    }
    public List<String> getRootIdentityFields() {
        return rootIdentityFields;
    }
    public void setRootIdentityFields(List<String> rootIdentityFields) {
        this.rootIdentityFields = rootIdentityFields;
    }

}
