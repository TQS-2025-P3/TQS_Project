import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export let errorRate = new Rate('errors');

export let options = {
  stages: [
    { duration: '30s', target: 10 }, 
    { duration: '1m', target: 10 },   
    { duration: '30s', target: 0 },  
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], 
    errors: ['rate<0.1'],             
  }
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
  let users = [];
  for (let i = 0; i < 5; i++) {
    let payload = JSON.stringify({
      name: `User ${i}`,
      email: `user${i}@loadtest.com`,
      password: 'password123'
    });

    let response = http.post(`${BASE_URL}/api/users`, payload, {
      headers: { 'Content-Type': 'application/json' },
    });

    if (response.status === 200) {
      users.push(response.json());
    }
  }
  return { users };
}

export default function(data) {
  let response = http.get(`${BASE_URL}/api/users`);
  check(response, {
    'GET /api/users - status 200': (r) => r.status === 200,
    'GET /api/users - response time < 200ms': (r) => r.timings.duration < 200,
  }) || errorRate.add(1);

  sleep(1);

  if (data.users && data.users.length > 0) {
    let userId = data.users[Math.floor(Math.random() * data.users.length)].id;
    response = http.get(`${BASE_URL}/api/users/${userId}`);
    check(response, {
      'GET /api/users/{id} - status 200': (r) => r.status === 200,
      'GET /api/users/{id} - has name': (r) => r.json('name') !== undefined,
    }) || errorRate.add(1);
  }

  sleep(1);

  if (data.users && data.users.length > 0) {
    let userId = data.users[Math.floor(Math.random() * data.users.length)].id;
    response = http.patch(`${BASE_URL}/api/users/${userId}/addFunds?amount=10.0`);
    check(response, {
      'PATCH /api/users/{id}/addFunds - status 200': (r) => r.status === 200,
    }) || errorRate.add(1);
  }

  sleep(2);
}

export function teardown(data) {
  console.log('Load test completed');
}