package com.redhat.lightblue.hook.publish;

import java.util.ArrayList;
import java.util.List;

import com.redhat.lightblue.query.Projection;

public class IdentityConfiguration {

    private final String esbRootEntityName;
    private final String esbEventEntityName;

    private final String endSystem;
    private final Integer defaultPriority;

    private Projection integratedFieldsProjection;
    private Projection identityProjection;
    private List<String> rootIdentityFields = new ArrayList<>();

    public IdentityConfiguration(
            String esbRootEntityName,
            String esbEventEntityName,
            String endSystem,
            Integer defaultPriority,
            Projection integratedFieldsProjection,
            Projection identityProjection,
            List<String> rootIdentityFields) {
        this.esbRootEntityName = esbRootEntityName;
        this.esbEventEntityName = esbEventEntityName;
        this.endSystem = endSystem;
        this.defaultPriority = defaultPriority;
        this.integratedFieldsProjection = integratedFieldsProjection;
        this.identityProjection = identityProjection;
        this.rootIdentityFields = rootIdentityFields;
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

    public Integer getDefaultPriority() {
        return defaultPriority;
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
