package com.meli.project.meliproject.model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Execution {

    private ObjectId id;

    @BsonProperty(value = "country")
    private String country;

    @BsonProperty(value = "distance")
    private Double distance;

    @BsonProperty(value = "invocations")
    private Integer invocations;

    @BsonProperty(value = "total")
    private Double total;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getInvocations() {
        return invocations;
    }

    public void setInvocations(Integer invocations) {
        this.invocations = invocations;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Request{" +
                "country='" + country + '\'' +
                ", distance=" + distance +
                ", invocations=" + invocations +
                ", total=" + total +
                '}';
    }
}
