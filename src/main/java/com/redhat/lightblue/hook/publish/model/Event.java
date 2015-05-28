package com.redhat.lightblue.hook.publish.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.lightblue.metadata.types.DateType;

public class Event {

    private String entityName;
    private String versionText;
    private String createdBy;
    private Date creationDate;
    private Date lastUpdateDate;
    private String lastUpdatedBy;
    private String CRUDOperation;
    private final List<EventIdentity> identity = new ArrayList<>();
    private String status;
    private String notes;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getVersionText() {
        return versionText;
    }

    public void setVersionText(String versionText) {
        this.versionText = versionText;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @JsonFormat(pattern = DateType.DATE_FORMAT_STR)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    @JsonProperty("CRUDOperation")
    public String getCRUDOperation() {
        return CRUDOperation;
    }

    public void setCRUDOperation(String CRUDOperation) {
        this.CRUDOperation = CRUDOperation;
    }

    public List<EventIdentity> getIdentity() {
        return identity;
    }

    public void addIdentities(Collection<EventIdentity> eventIdentities) {
        identity.addAll(eventIdentities);
    }

    public void addIdentities(EventIdentity... eventIdentities) {
        for (EventIdentity id : eventIdentities) {
            identity.add(id);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
