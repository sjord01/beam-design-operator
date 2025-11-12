package sordonez.operator.crd;

import java.util.List;
import java.util.Map;

public class BeamDesignSpec {
    private Double length;
    private List<Map<String,Object>> loads;
    private List<String> supports;
    private String materialId;
    private Map<String,Object> sweep;

    // getters / setters
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public List<Map<String,Object>> getLoads() { return loads; }
    public void setLoads(List<Map<String,Object>> loads) { this.loads = loads; }
    public List<String> getSupports() { return supports; }
    public void setSupports(List<String> supports) { this.supports = supports; }
    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }
    public Map<String,Object> getSweep() { return sweep; }
    public void setSweep(Map<String,Object> sweep) { this.sweep = sweep; }
}