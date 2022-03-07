import http from 'k6/http';
import exec from 'k6/execution';

const url = __ENV.SERVICE_URL || 'http://localhost:8080';

export default function() {
  const data = {
    content: `Test ${exec.vu.idInTest}-${exec.vu.iterationInScenario}`
  };
  http.post(url, JSON.stringify(data), {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}
