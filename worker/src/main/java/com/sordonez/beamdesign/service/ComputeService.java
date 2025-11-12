package com.sordonez.beamdesign.service;

import com.sordonez.beamdesign.worker.model.BeamResult;
import com.sordonez.beamdesign.worker.repository.BeamResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ComputeService {

    private final BeamResultRepository resultRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public ComputeService(BeamResultRepository resultRepo) {
        this.resultRepo = resultRepo;
    }

    /**
     * Performs a simple discretized beam analysis (simply supported, point loads via superposition),
     * persists the result to DB and returns a Map with the generated result and resultId.
     */
    public Map<String,Object> computeAndSave(Map<String,Object> spec) throws Exception {
        double length = ((Number)spec.getOrDefault("length", 1.0)).doubleValue();
        List<Map<String,Object>> loads = (List<Map<String,Object>>) spec.getOrDefault("loads", List.of());

        int N = 61;
        double dx = length / (N-1);
        double[] positions = new double[N];
        double[] shear = new double[N];
        double[] moment = new double[N];
        double[] deflection = new double[N];

        double totalReactionsLeft = 0.0;
        for (var ld : loads) {
            double pos = ((Number)ld.getOrDefault("position", 0)).doubleValue();
            double val = ((Number)ld.getOrDefault("value", 0)).doubleValue();
            double Ra = val * (length - pos) / length;
            totalReactionsLeft += Ra;
        }

        for (int i=0;i<N;i++) {
            double x = i*dx;
            positions[i]=x;
            double s = totalReactionsLeft;
            for (var ld : loads) {
                double pos = ((Number)ld.getOrDefault("position", 0)).doubleValue();
                double val = ((Number)ld.getOrDefault("value", 0)).doubleValue();
                if (x>=pos) s -= val;
            }
            shear[i]=s;

            double m = totalReactionsLeft * x;
            for (var ld : loads) {
                double pos = ((Number)ld.getOrDefault("position", 0)).doubleValue();
                double val = ((Number)ld.getOrDefault("value", 0)).doubleValue();
                if (x>=pos) m -= val * (x - pos);
            }
            moment[i]=m;
        }

        // deflection via double integration of M/EI (EI arbitrary scale)
        double EI = 2e7;
        double[] curvature = new double[N];
        for (int i=0;i<N;i++) curvature[i] = moment[i] / EI;
        double[] slope = new double[N];
        deflection[0]=0;
        slope[0]=0;
        for (int i=1;i<N;i++){
            slope[i] = slope[i-1] + 0.5*(curvature[i-1]+curvature[i]) * dx;
            deflection[i] = deflection[i-1] + 0.5*(slope[i-1]+slope[i]) * dx;
        }

        double maxMoment = Arrays.stream(moment).map(Math::abs).max().orElse(0.0);
        double maxDefl = Arrays.stream(deflection).map(Math::abs).max().orElse(0.0);

        Map<String,Object> result = Map.of(
                "positions", positions,
                "shear", shear,
                "moment", moment,
                "deflection", deflection,
                "summary", Map.of("maxMoment", maxMoment, "maxDeflection", maxDefl),
                "status", "ok"
        );

        String modelId = (String) spec.getOrDefault("modelId", null);
        String id = UUID.randomUUID().toString();
        String json = mapper.writeValueAsString(result);
        BeamResult br = new BeamResult(id, modelId, json, maxMoment, maxDefl);
        resultRepo.save(br);

        return Map.of("resultId", id, "result", result);
    }
}