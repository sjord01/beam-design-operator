const API_BASE = "http://localhost:8080/api";
const COMPUTE_BASE = "http://localhost:8080/compute";

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('designForm');
    const submitResult = document.getElementById('submitResult');
    const modelList = document.getElementById('modelList');
    const resultArea = document.getElementById('resultArea');
    const saveBtn = document.getElementById('saveModel');
    const computeBtn = document.getElementById('computeNow');

    async function listModels() {
        try {
            const res = await fetch(API_BASE + "/models");
            const data = await res.json();
            modelList.innerHTML = '';
            if (data.length === 0) modelList.innerText = 'No models found';
            else {
                data.forEach(m => {
                    const d = document.createElement('div');
                    d.textContent = `${m.id} — ${new Date(m.createdAt).toLocaleString()}`;
                    d.onclick = () => loadResults(m.id);
                    modelList.appendChild(d);
                });
            }
        } catch(err) {
            modelList.innerText = 'Error listing models: ' + err;
        }
    }

    async function loadResults(modelId) {
        resultArea.innerHTML = 'Loading results...';
        try {
            const res = await fetch(API_BASE + `/models/${modelId}/results`);
            const data = await res.json();
            if (data.length===0) resultArea.innerText = 'No results for model ' + modelId;
            else {
                resultArea.innerHTML = '';
                data.forEach(r => {
                    const pre = document.createElement('pre');
                    pre.textContent = `resultId: ${r.id}\nmaxMoment: ${r.maxMoment}\nmaxDeflection: ${r.maxDeflection}\ncreatedAt: ${new Date(r.createdAt).toLocaleString()}\n\nresultJson:\n${r.resultJson}`;
                    resultArea.appendChild(pre);
                });
            }
        } catch(err) {
            resultArea.innerText = 'Error loading results: ' + err;
        }
    }

    saveBtn.addEventListener('click', async () => {
        const fd = new FormData(form);
        const spec = {
            length: parseFloat(fd.get('length')),
            materialId: fd.get('materialId'),
            targetSafetyFactor: parseFloat(fd.get('targetSafetyFactor')),
            loads: [{ position: parseFloat(fd.get('loadPos')), value: parseFloat(fd.get('loadVal')), type: "point" }],
            supports: fd.get('supports').split(',')
        };
        submitResult.innerText = 'Saving model...';
        try {
            const res = await fetch(API_BASE + "/models", {
                method: "POST",
                headers: {'Content-Type':'application/json'},
                body: JSON.stringify(spec)
            });
            const data = await res.json();
            submitResult.innerText = 'Saved modelId: ' + data.modelId;
            listModels();
        } catch(err) {
            submitResult.innerText = 'Error saving model: ' + err;
        }
    });

    computeBtn.addEventListener('click', async () => {
        const fd = new FormData(form);
        const payload = {
            length: parseFloat(fd.get('length')),
            materialId: fd.get('materialId'),
            targetSafetyFactor: parseFloat(fd.get('targetSafetyFactor')),
            loads: [{ position: parseFloat(fd.get('loadPos')), value: parseFloat(fd.get('loadVal')), type: "point" }],
            supports: fd.get('supports').split(',')
        };
        submitResult.innerText = 'Computing...';
        try {
            const res = await fetch(COMPUTE_BASE, {
                method: "POST",
                headers: {'Content-Type':'application/json'},
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            submitResult.innerText = 'Compute finished. resultId: ' + (data.resultId || 'none');
            if (data.result && data.result.summary) {
                resultArea.innerHTML = `<h3>Summary</h3><pre>${JSON.stringify(data.result.summary, null, 2)}</pre>`;
            } else {
                resultArea.innerText = 'No result returned';
            }
            listModels();
        } catch(err) {
            submitResult.innerText = 'Compute error: ' + err;
            resultArea.innerText = '';
        }
    });

    listModels();
});