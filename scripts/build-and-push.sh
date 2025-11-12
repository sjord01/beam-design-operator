#!/usr/bin/env bash
set -euo pipefail

# Usage: ./build-and-push.sh <docker-registry-prefix>
# example: ./build-and-push.sh sjord01
REGISTRY_PREFIX=${1:-sjord01}
TAG=${2:-"0.1.0"}

echo "Building worker..."
pushd worker >/dev/null
mvn -DskipTests package
docker build -t ${REGISTRY_PREFIX}/beam-worker:${TAG} -f Dockerfile .
docker push ${REGISTRY_PREFIX}/beam-worker:${TAG}
popd >/dev/null

echo "Building operator..."
pushd operator >/dev/null
./mvnw -DskipTests package
docker build -t ${REGISTRY_PREFIX}/beam-operator:${TAG} -f Dockerfile .
docker push ${REGISTRY_PREFIX}/beam-operator:${TAG}
popd >/dev/null

echo "Done. Images pushed with tag ${TAG} to ${REGISTRY_PREFIX}/*"