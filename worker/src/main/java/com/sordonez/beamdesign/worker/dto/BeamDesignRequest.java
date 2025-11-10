package com.sordonez.beamdesign.worker.dto;

import java.util.List;
import java.util.Map;

public class BeamDesignRequest {
    private String modelId;
    private double length;
    private List<Map<String, Object>> loads;
    private List<String> supports;
    private String materialId;
    private double targetSafetyFactor;

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public List<Map<String, Object>> getLoads() { return loads; }
    public void setLoads(List<Map<String, Object>> loads) { this.loads = loads; }
    public List<String> getSupports() { return supports; }
    public void setSupports(List<String> supports) { this.supports = supports; }
    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }
    public double getTargetSafetyFactor() { return targetSafetyFactor; }
    public void setTargetSafetyFactor(double targetSafetyFactor) { this.targetSafetyFactor = targetSafetyFactor; }
}