import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';
// This is a test script that simulates a booking flow where the user books a car from a station. 

// Global variables to store test data
let testData = {};

export const options = {
  setupTimeout: '30s',
  scenarios: {
    // Ramp up gradually to avoid overwhelming the system
    booking_flow: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 50 },   // Ramp up to 50 users
        { duration: '1m', target: 100 },   // Scale to 100 users
        { duration: '30s', target: 0 },    // Ramp down
      ],
    },
    
  },
  
  thresholds: {
    http_req_duration: ['p(95)<3000'], // 95% of requests under 3s
    http_req_failed: ['rate<0.05'],    // Error rate under 5%
    'http_req_duration{endpoint:booking}': ['p(90)<1500'], // Booking specific
  },
};

// Setup function runs once before all scenarios
export function setup() {
  console.log('Loading test data...');
  
  // Load users data
  const usersResponse = http.get(`${BASE_URL}/api/users`);
  if (usersResponse.status !== 200) {
    console.error('Failed to load users data');
    return { users: [], stations: [] };
  }
  
  const users = usersResponse.json().filter(u => u.balance > 10);
  console.log(`Loaded ${users.length} eligible users`);
  
  // Load stations data
  const stationsResponse = http.get(`${BASE_URL}/api/stations`);
  if (stationsResponse.status !== 200) {
    console.error('Failed to load stations data');
    return { users, stations: [] };
  }
  
  const stations = stationsResponse.json();
  console.log(`Loaded ${stations.length} stations`);
  
  return { users, stations };
}

// Main booking scenario
export default function (data) {
  // Early exit if no test data available
  if (!data || data.users.length === 0 || data.stations.length === 0) {
    console.error('No test data available');
    return;
  }

  const user = data.users[Math.floor(Math.random() * data.users.length)];
  
  // Get user's cars with better error handling
  const carsRes = http.get(`${BASE_URL}/api/cars/user/${user.id}`, {
    tags: { endpoint: 'cars' },
    timeout: '10s'
  });
  
  if (!check(carsRes, {
    'cars request successful': (r) => r.status === 200,
    'cars response time OK': (r) => r.timings.duration < 5000,
  })) {
    console.warn(`Failed to get cars for user ${user.id}`);
    return;
  }

  const cars = carsRes.json();
  if (cars.length === 0) {
    sleep(1); // Brief pause before next iteration
    return;
  }

  // Get user's current bookings
  const bookingsRes = http.get(`${BASE_URL}/api/bookings/user/${user.id}`, {
    tags: { endpoint: 'bookings' },
    timeout: '10s'
  });
  
  if (!check(bookingsRes, {
    'bookings request successful': (r) => r.status === 200,
  })) {
    console.warn(`Failed to get bookings for user ${user.id}`);
    return;
  }

  const bookings = bookingsRes.json();
  const activeCarIds = new Set(
    bookings
      .filter(b => b.status === 'RESERVED')
      .map(b => b.car.id)
  );

  const availableCars = cars.filter(car => !activeCarIds.has(car.id));
  if (availableCars.length === 0) {
    sleep(1);
    return;
  }

  // Find available stations
  const availableStations = data.stations.filter(st => st.availableSlots > 0);
  if (availableStations.length === 0) {
    sleep(2); // Wait a bit longer if no stations available
    return;
  }

  // Sort stations by availability (prefer stations with more slots)
  availableStations.sort((a, b) => b.availableSlots - a.availableSlots);
  const station = availableStations[Math.floor(Math.random() * Math.min(3, availableStations.length))];

  // Retry logic
  let bookingCreated = false;
  const maxAttempts = 2;
  let remainingCars = [...availableCars];
  
  for (let attempt = 0; attempt < maxAttempts && !bookingCreated && remainingCars.length > 0; attempt++) {
    // Pick a random available car
    const carIndex = Math.floor(Math.random() * remainingCars.length);
    const car = remainingCars[carIndex];
    
    const bookingPayload = {
      userId: user.id,
      carId: car.id,
      stationId: station.id,
      duration: Math.floor(Math.random() * 60) + 15, // 15-75 minutes, more realistic
    };

    const bookingRes = http.post(
      `${BASE_URL}/api/bookings`,
      JSON.stringify(bookingPayload),
      {
        headers: { 'Content-Type': 'application/json' },
        tags: { endpoint: 'booking' },
        timeout: '15s'
      }
    );

    if (check(bookingRes, {
      'booking creation successful': (r) => r.status === 200,
      'booking response time OK': (r) => r.timings.duration < 2000,
    })) {
      bookingCreated = true;
      const booking = bookingRes.json();
      
      // Simulate some time before completing the booking
      sleep(Math.random() * 2);
      
      // Complete the booking
      const statusRes = http.patch(
        `${BASE_URL}/api/bookings/${booking.id}/status?status=COMPLETED`,
        null,
        { 
          tags: { endpoint: 'booking_status' },
          timeout: '10s'
        }
      );
      
      check(statusRes, {
        'booking completion successful': (r) => r.status === 200,
      });
      
    } else {
      // Remove the car from available options and try another
      remainingCars.splice(carIndex, 1);
      
      if (bookingRes.status >= 400) {
        console.warn(`Booking failed for car ${car.id}: ${bookingRes.status}`);
      }
      
      // Brief pause between attempts
      sleep(0.5);
    }
  }

  //pause between actions
  sleep(Math.random() * 4 + 1); 
}
  sleep(5);