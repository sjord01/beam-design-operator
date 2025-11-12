package sordonez.operator.crd;

import java.util.Map;

public class BeamDesignStatus {
    private String phase;
    private String jobName;
    private String resultId;
    private Map<String,Object> summary;
    private String message;

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    public Map<String,Object> getSummary() { return summary; }
    public void setSummary(Map<String,Object> summary) { this.summary = summary; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}