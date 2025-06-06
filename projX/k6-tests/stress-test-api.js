import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 50 },   
    { duration: '5m', target: 50 },   
    { duration: '2m', target: 100 },  
    { duration: '5m', target: 100 }, 
    { duration: '2m', target: 0 },    
  ],
  thresholds: {
    http_req_duration: ['p(99)<1500'], 
    http_req_failed: ['rate<0.05'],    
  },
};

const BASE_URL = 'http://localhost:8080';

export default function() {
  let payload = JSON.stringify({
    name: `StressUser${__VU}_${__ITER}`,
    email: `stress${__VU}_${__ITER}@test.com`,
    password: 'stresstest123'
  });

  let response = http.post(`${BASE_URL}/api/users`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(response, {
    'POST /api/users - status 200': (r) => r.status === 200,
    'POST /api/users - has id': (r) => r.json('id') !== undefined,
  });

  response = http.get(`${BASE_URL}/api/users`);
  check(response, {
    'GET /api/users - status 200': (r) => r.status === 200,
    'GET /api/users - is array': (r) => Array.isArray(r.json()),
  });
}