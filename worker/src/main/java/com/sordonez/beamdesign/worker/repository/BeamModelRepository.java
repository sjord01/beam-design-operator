package com.sordonez.beamdesign.worker.repository;

import com.sordonez.beamdesign.worker.model.BeamModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeamModelRepository extends JpaRepository<BeamModel, String> {
}