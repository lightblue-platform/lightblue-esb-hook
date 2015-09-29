package com.redhat.lightblue.hook.publish;

import java.util.List;

import com.redhat.lightblue.hook.publish.model.Header;
import com.redhat.lightblue.query.Projection;

public class EventConfiguration {

    private final String esbRootEntityName;
    private final String esbEventEntityName;

    private final String endSystem;

    private final Projection integratedFieldsProjection;
    private final Projection identityProjection;
    private final List<String> rootIdentityFields;
    private final List<Header> headers;

    public EventConfiguration(
            String esbRootEntityName,
            String esbEventEntityName,
            String endSystem,
            Projection integratedFieldsProjection,
            Projection identityProjection,
            List<String> rootIdentityFields,
            List<Header> headers) {
        this.esbRootEntityName = esbRootEntityName;
        this.esbEventEntityName = esbEventEntityName;
        this.endSystem = endSystem;
        this.integratedFieldsProjection = integratedFieldsProjection;
        this.identityProjection = identityProjection;
        this.rootIdentityFields = rootIdentityFields;
        this.headers = headers;
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

    public Projection getIntegratedFieldsProjection() {
        return integratedFieldsProjection;
    }

    public Projection getIdentityProjection() {
        return identityProjection;
    }

    public List<String> getRootIdentityFields() {
        return rootIdentityFields;
    }

    public List<Header> getHeaders() {
        return headers;
    }

}
