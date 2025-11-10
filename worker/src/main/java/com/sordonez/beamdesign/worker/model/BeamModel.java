package com.sordonez.beamdesign.worker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "beam_model")
public class BeamModel {

    @Id
    @Column(length = 36)
    private String id;

    @Column(columnDefinition = "text")
    private String specJson;

    private Instant createdAt;

    public BeamModel() {}

    public BeamModel(String id, String specJson) {
        this.id = id;
        this.specJson = specJson;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSpecJson() { return specJson; }
    public void setSpecJson(String specJson) { this.specJson = specJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}