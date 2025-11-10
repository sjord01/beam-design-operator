package com.sordonez.beamdesign.worker.repository;

import com.sordonez.beamdesign.worker.model.BeamResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeamResultRepository extends JpaRepository<BeamResult, String> {
    List<BeamResult> findByModelId(String modelId);
}