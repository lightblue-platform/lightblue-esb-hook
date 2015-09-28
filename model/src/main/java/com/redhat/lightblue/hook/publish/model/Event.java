package com.redhat.lightblue.hook.publish.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.lightblue.metadata.types.DateType;

/**
 * POJO representation of metadata/esbEvents.json.
 */
public class Event {

    @JsonProperty("_id")
    private String id;
    private String entityName;
    private final List<Identity> identity = new ArrayList<>();
    private String esbRootEntityName;
    private String esbEventEntityName;
    private final List<Identity> rootIdentity = new ArrayList<>();
    private String endSystem;
    private Status status;
    private Operation operation;
    private String eventSource;
    @JsonFormat(pattern = DateType.DATE_FORMAT_STR)
    private Date creationDate;
    private String createdBy;
    @JsonFormat(pattern = DateType.DATE_FORMAT_STR)
    private Date lastUpdateDate;
    private String lastUpdatedBy;
    private String version;
    private final List<Header> headers = new ArrayList<>();
    private String notes;

    public static enum Status {
        UNPROCESSED
    }

    public static enum Operation {
        INSERT, UPDATE, RESYNC
    }

    /**
     * @return unique id for this event.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return name of the lightblue entity that generated this event.
     */
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return version of the lightblue entity that generated this event.
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return list of fields that uniquely identify the entity entry that
     * generated this event.
     */
    public List<Identity> getIdentity() {
        return identity;
    }

    public void addIdentities(Collection<Identity> identities) {
        identity.addAll(identities);
    }

    public void addIdentities(Identity... identities) {
        for (Identity id : identities) {
            identity.add(id);
        }
    }

    /**
     * @return the name of the esb entity that represents the lightblue entity
     * that generated this event.
     */
    public String getEsbRootEntityName() {
        return esbRootEntityName;
    }

    public void setEsbRootEntityName(String entityName) {
        esbRootEntityName = entityName;
    }

    /**
     * An event is not always for the rootEntity, sometimes it is on a sub-entity.
     * This field indicates the name of the sub-entity.
     * @return name of sub-entity that the event is generated on. Null will be returned
     * if the event is truly for the rootEntity proper.
     */
    public String getEsbEventEntityName() {
        return esbEventEntityName;
    }

    public void setEsbEventEntityName(String esbEventEntityName) {
        this.esbEventEntityName = esbEventEntityName;
    }

    /**
     * @return list of fields that uniquely identify the esb entity entry.
     */
    public List<Identity> getRootIdentity() {
        return rootIdentity;
    }

    public void addRootIdentities(Collection<Identity> identities) {
        rootIdentity.addAll(identities);
    }

    public void addRootIdentities(Identity... identities) {
        for (Identity id : identities) {
            rootIdentity.add(id);
        }
    }

    /**
     * @return name of the esb end system that is responsible for processing
     * this event.
     */
    public String getEndSystem() {
        return endSystem;
    }

    public void setEndSystem(String endSystem) {
        this.endSystem = endSystem;
    }

    /**
     * @return a marker indicating the workflow state of this event. For example, status
     * may indicate that an event has still 'unprocessed'.
     */
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return a marker indicating the operation that caused the event to generate.
     * In the case of the lightblue-esb-hook, it would be the crud operation performed on the
     * entity that generated the event.
     * If the event is created from an external application, then this field could be
     * relevant to that application.
     */
    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    /**
     * @return name of user or application that performed the action
     * that originated this event.
     */
    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    /**
     * @return date this event was created on.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return name of user or application that created this event.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return date this event was last updated on.
     */
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    /**
     * @return name of user or application that last updated this event.
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return list of optional headers for the jms message.
     */
    public List<Header> getHeaders() {
        return headers;
    }

    public void addHeaders(Collection<Header> headers) {
        this.headers.addAll(headers);
    }

    public void addHeaders(Header... headers) {
        for (Header header : headers) {
            this.headers.add(header);
        }
    }

    /**
     * @return any operator notes about this event.
     */
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
