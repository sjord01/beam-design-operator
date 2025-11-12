Beam Design Operator - Quick README

Repository: github.com/sjord01/beam-design-operator

What this is
A small web service + lightweight UI for creating, running and storing beam design/analysis jobs.
Enter beam geometry, supports, loads and material/target requirements; the app computes shear, bending moment,
and deflection (simple Euler–Bernoulli discretized analysis), persists models and results to Postgres,
and shows numeric summaries in the UI. An operator module demonstrates a CRD → Job workflow for asynchronous runs.

Quick features
- Save beam models (POST /api/models)
- Compute results (POST /api/compute or /compute)
- List models and results (GET /api/models, GET /api/models/{id}/results)
- Simple browser UI served from the worker module (static files)
- Optional operator module for Kubernetes Job orchestration

Prerequisites
- Java 17
- Docker (recommended for running Postgres)
- jq (optional, for pretty CLI JSON)
- No global Maven required — the repo includes mvnw

Quick start (dev, run from repo root)
# copy UI into worker static folder (run when you change UI)
mkdir -p worker/src/main/resources/static
cp -r ui/* worker/src/main/resources/static/

# start a local Postgres container
docker rm -f pg-local || true
docker run --rm -d --name pg-local -e POSTGRES_PASSWORD=pass -e POSTGRES_DB=beam -p 5432:5432 postgres:15

# export DB env vars in your shell
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/beam'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='pass'

# run the worker (serves UI + API)
./mvnw -f worker/pom.xml spring-boot:run -Dspring-boot.run.main-class=com.sordonez.beamdesign.worker.WorkerApplication

Verify
- Open: http://localhost:8080/
- Check listener: lsof -iTCP:8080 -sTCP:LISTEN

Minimal API examples (replace variables as needed)
# Save model -> returns modelId
curl -s -X POST 'http://localhost:8080/api/models' -H "Content-Type: application/json" -d '{"length":6.0,"materialId":"concrete-c30","targetSafetyFactor":1.5,"loads":[{"position":3.0,"value":20.0,"type":"point"}],"supports":["pinned","pinned"]}' | jq .

# Compute directly (returns resultId + result.summary)
curl -s -X POST 'http://localhost:8080/api/compute' -H "Content-Type: application/json" -d '{"length":6.0,"materialId":"concrete-c30","targetSafetyFactor":1.5,"loads":[{"position":3.0,"value":20.0}],"supports":["pinned","pinned"]}' | jq .

# Compute by saved model
curl -s -X POST 'http://localhost:8080/api/compute' -H "Content-Type: application/json" -d '{"modelId":"<modelId>"}' | jq .

Where to look in the code
- worker/src/main/java/.../controller/ApiController.java — HTTP API endpoints
- worker/src/main/java/.../service/ComputeService.java — numerical compute + persistence
- worker/src/main/resources/static/index.html and app.js — frontend UI and client logic
- operator/ — Java Operator SDK reconciler and CRD example (k8s flow)

Contact / repo
https://github.com/sjord01/beam-design-operator