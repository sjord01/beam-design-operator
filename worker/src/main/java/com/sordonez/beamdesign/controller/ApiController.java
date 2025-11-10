package com.sordonez.beamdesign.worker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sordonez.beamdesign.worker.model.BeamModel;
import com.sordonez.beamdesign.worker.model.BeamResult;
import com.sordonez.beamdesign.worker.repository.BeamModelRepository;
import com.sordonez.beamdesign.worker.repository.BeamResultRepository;
import com.sordonez.beamdesign.worker.service.ComputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final BeamModelRepository modelRepo;
    private final BeamResultRepository resultRepo;
    private final ComputeService computeService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiController(BeamModelRepository modelRepo,
                         BeamResultRepository resultRepo,
                         ComputeService computeService) {
        this.modelRepo = modelRepo;
        this.resultRepo = resultRepo;
        this.computeService = computeService;
    }

    // Register a model (stores full spec JSON). Returns modelId.
    @PostMapping("/models")
    public ResponseEntity<?> createModel(@RequestBody Map<String,Object> spec) throws Exception {
        String id = UUID.randomUUID().toString();
        String json = mapper.writeValueAsString(spec);
        BeamModel m = new BeamModel(id, json);
        modelRepo.save(m);
        return ResponseEntity.ok(Map.of("status","ok","modelId", id));
    }

    // List models
    @GetMapping("/models")
    public List<BeamModel> listModels() {
        return modelRepo.findAll();
    }

    // Get model
    @GetMapping("/models/{id}")
    public ResponseEntity<?> getModel(@PathVariable String id) {
        return modelRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // List results for a model
    @GetMapping("/models/{id}/results")
    public List<BeamResult> listResultsForModel(@PathVariable String id) {
        return resultRepo.findByModelId(id);
    }

    // Compute endpoint: can accept full spec or a modelId in body
    @PostMapping("/compute")
    public ResponseEntity<?> compute(@RequestBody Map<String,Object> payload) throws Exception {
        // If modelId present but no other fields, fetch model and merge payload
        if (payload.containsKey("modelId") && (!payload.containsKey("length") || payload.get("length")==null)) {
            String modelId = String.valueOf(payload.get("modelId"));
            Optional<BeamModel> maybe = modelRepo.findById(modelId);
            if (maybe.isPresent()) {
                String specJson = maybe.get().getSpecJson();
                Map<String,Object> spec = mapper.readValue(specJson, Map.class);
                // allow overrides in payload
                spec.putAll(payload);
                Map<String,Object> res = computeService.computeAndSave(spec);
                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error","modelId not found"));
            }
        } else {
            Map<String,Object> res = computeService.computeAndSave(payload);
            return ResponseEntity.ok(res);
        }
    }
}