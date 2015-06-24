package com.redhat.lightblue.hook.publish.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.redhat.lightblue.metadata.types.DateType;

public class Event {

    private String entityName;
    private final List<Identity> identity = new ArrayList<>();
    private String rootEntityName;
    private final List<Identity> rootIdentity = new ArrayList<>();
    private String endSystem;
    private String status;
    private String operation;
    private String eventSource;
    private Integer priorityValue;
    private Date creationDate;
    private String createdBy;
    private Date lastUpdateDate;
    private String lastUpdatedBy;
    private String version;
    private final List<Header> headers = new ArrayList<>();
    private String notes;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<Identity> getIdentity() {
        return identity;
    }

    public void addIdentities(Collection<Identity> identities) {
        this.identity.addAll(identities);
    }

    public void addIdentities(Identity... identities) {
        for (Identity id : identities) {
            this.identity.add(id);
        }
    }

    public String getRootEntityName() {
        return rootEntityName;
    }

    public void setRootEntityName(String entityName) {
        this.rootEntityName = entityName;
    }

    public List<Identity> getRootIdentity() {
        return rootIdentity;
    }

    public void addRootIdentities(Collection<Identity> identities) {
        this.rootIdentity.addAll(identities);
    }

    public void addRootIdentities(Identity... identities) {
        for (Identity id : identities) {
            this.rootIdentity.add(id);
        }
    }

    public String getEndSystem() {
        return endSystem;
    }

    public void setEndSystem(String endSystem) {
        this.endSystem = endSystem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public Integer getPriorityValue() {
        return priorityValue;
    }

    public void setPriorityValue(Integer priorityValue) {
        this.priorityValue = priorityValue;
    }

    @JsonFormat(pattern = DateType.DATE_FORMAT_STR)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonFormat(pattern = DateType.DATE_FORMAT_STR)
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void addHeaders(Collection<Header> headers) {
        headers.addAll(headers);
    }

    public void addHeaders(Header... headers) {
        for (Header header : headers) {
            this.headers.add(header);
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
