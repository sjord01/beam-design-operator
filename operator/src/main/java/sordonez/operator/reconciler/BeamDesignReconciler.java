package sordonez.operator.reconciler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sordonez.operator.crd.BeamDesign;
import sordonez.operator.crd.BeamDesignStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal reconciler that:
 * - when a BeamDesign CR is created, writes a ConfigMap with the spec JSON,
 * - creates a Job that runs the worker image with BEAM_SPEC_CONFIGMAP env pointing to that ConfigMap,
 * - updates status.phase = Running and stores jobName,
 * - when Job succeeds, sets status.phase = Succeeded and stores a basic resultId.
 *
 * Note: adjust image name/tag and resource limits for your environment.
 */
@Controller
@Component
public class BeamDesignReconciler implements Reconciler<BeamDesign> {
    private final KubernetesClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(BeamDesignReconciler.class);

    public BeamDesignReconciler(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public UpdateControl<BeamDesign> reconcile(BeamDesign resource, Context context) throws Exception {
        String ns = resource.getMetadata().getNamespace() == null ? "default" : resource.getMetadata().getNamespace();
        String name = resource.getMetadata().getName();

        if (resource.getStatus() == null) resource.setStatus(new BeamDesignStatus());

        BeamDesignStatus status = resource.getStatus();

        // If no job started yet, create ConfigMap + Job
        if (status.getJobName() == null || status.getJobName().isEmpty()) {
            // create configmap with spec JSON
            String cmName = "beam-spec-" + name;
            String specJson = mapper.writeValueAsString(resource.getSpec());
            ConfigMap cm = new ConfigMap();
            cm.getMetadata().setName(cmName);
            cm.getMetadata().setNamespace(ns);
            Map<String,String> data = new HashMap<>();
            data.put("spec.json", specJson);
            cm.setData(data);

            // apply configmap (create or replace)
            client.configMaps().inNamespace(ns).createOrReplace(cm);

            // build a Job
            String jobName = "beamcompute-" + name + "-" + Instant.now().getEpochSecond();
            Job job = new JobBuilder()
                    .withNewMetadata()
                    .withName(jobName)
                    .withNamespace(ns)
                    .endMetadata()
                    .withNewSpec()
                    .withBackoffLimit(1)
                    .withNewTemplate()
                    .withNewSpec()
                    .withRestartPolicy("Never")
                    .addNewContainer()
                    .withName("beam-worker")
                    .withImage("sjord01/beam-worker:0.1.0") // adjust tag
                    .addNewEnv()
                    .withName("BEAM_SPEC_CONFIGMAP")
                    .withNewValueFrom()
                    .withNewConfigMapKeyRef()
                    .withName(cmName)
                    .withKey("spec.json")
                    .endConfigMapKeyRef()
                    .endValueFrom()
                    .endEnv()
                    .endContainer()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();

            client.batch().v1().jobs().inNamespace(ns).create(job);

            status.setPhase("Running");
            status.setJobName(jobName);
            status.setMessage("Job created: " + jobName);
            log.info("Created Job {} for BeamDesign {}/{}", jobName, ns, name);
            return UpdateControl.updateStatus(resource);
        }

        // If a job exists, check job status
        Job job = client.batch().v1().jobs().inNamespace(ns).withName(status.getJobName()).get();
        if (job == null) {
            // job disappeared — mark failed
            status.setPhase("Failed");
            status.setMessage("Job not found: " + status.getJobName());
            return UpdateControl.updateStatus(resource);
        }

        Integer succeeded = job.getStatus() != null ? job.getStatus().getSucceeded() : null;
        Integer failed = job.getStatus() != null ? job.getStatus().getFailed() : null;
        if (succeeded != null && succeeded > 0) {
            status.setPhase("Succeeded");
            // For demo: set resultId to jobName; in real flow operator should retrieve real result (DB or object store)
            status.setResultId(status.getJobName());
            status.setMessage("Job succeeded");
            log.info("Job {} succeeded for BeamDesign {}/{}", status.getJobName(), ns, name);
            return UpdateControl.updateStatus(resource);
        }

        if (failed != null && failed > 0) {
            status.setPhase("Failed");
            status.setMessage("Job failed");
            log.warn("Job {} failed for BeamDesign {}/{}", status.getJobName(), ns, name);
            return UpdateControl.updateStatus(resource);
        }

        // still running -> no status update necessary
        return UpdateControl.noUpdate();
    }
}