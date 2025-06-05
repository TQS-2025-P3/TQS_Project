import http from 'k6/http';
import { check, fail, sleep } from 'k6';

export let options = {
  vus: 1, // number of virtual users
  duration: '10s', // how long the test runs
};

const BASE_URL = 'http://spring_app:8080'; // Docker internal host name

export default function () {
  const userData = {
    name: `User_${__VU}_${__ITER}`,
    email: `user_${__VU}_${__ITER}_${Date.now()}@example.com`,
    password: 'password123',
    balance: 100.0,
    cars: [],
  };

  const res = http.post(`${BASE_URL}/api/users`, JSON.stringify(userData), {
    headers: { 'Content-Type': 'application/json' },
  });

  const success = check(res, {
    'User created (200)': (r) => r.status === 200,
  });

  if (!success) {
    console.error(`❌ Request failed with status ${res.status}`);
    console.error(`Response body:\n${res.body}`);
    fail('User creation failed');
  }

  sleep(1);
}
