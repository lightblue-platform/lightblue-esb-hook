package com.redhat.lightblue.hook.publish.model;

import java.util.Set;

public class IdentitySet {
    public static final String INSERT_OPERATION = "INSERT";
    public static final String UPDATE_OPERATION = "UPDATE";

    private final Set<Identity> set;
    private String operation;

    public IdentitySet(Set<Identity> set, String operation) {
        this.set = set;
        this.operation = operation;
    }
    public Set<Identity> getSet() {
        return set;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    @Override
    public String toString() {
        return operation + ":" + set.toString();
    }

}
