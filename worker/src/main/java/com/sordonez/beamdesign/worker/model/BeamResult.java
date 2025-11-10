package com.sordonez.beamdesign.worker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "beam_result")
public class BeamResult {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 36)
    private String modelId;

    @Column(columnDefinition = "text")
    private String resultJson;

    private double maxMoment;
    private double maxDeflection;

    private Instant createdAt;

    public BeamResult() {}

    public BeamResult(String id, String modelId, String resultJson, double maxMoment, double maxDeflection) {
        this.id = id;
        this.modelId = modelId;
        this.resultJson = resultJson;
        this.maxMoment = maxMoment;
        this.maxDeflection = maxDeflection;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public String getResultJson() { return resultJson; }
    public void setResultJson(String resultJson) { this.resultJson = resultJson; }
    public double getMaxMoment() { return maxMoment; }
    public void setMaxMoment(double maxMoment) { this.maxMoment = maxMoment; }
    public double getMaxDeflection() { return maxDeflection; }
    public void setMaxDeflection(double maxDeflection) { this.maxDeflection = maxDeflection; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}