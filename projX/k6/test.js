import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
export let errorRate = new Rate('errors');

// Test configuration
export let options = {
  stages: [
    { duration: '10s', target: 10 }, // Ramp up to 10 users
   // { duration: '1m', target: 10 }, // Stay at 10 users
   // { duration: '30s', target: 20 }, // Ramp up to 20 users
   // { duration: '1m', target: 20 }, // Stay at 20 users
   // { duration: '30s', target: 0 },  // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
    http_req_failed: ['rate<0.05'],   // Error rate must be below 5%
    errors: ['rate<0.1'],             // Custom error rate below 10%
  },
};

const BASE_URL = 'http://localhost:8080';

// Test data
let testUsers = [];
let testStaff = [];
let testCars = [];
let testStations = [];
let testBookings = [];

export function setup() {
  // Create test users
  console.log('Setting up test data...');
  
  // Create staff first (needed for stations)
  const staffData = {
    name: 'Test Staff',
    email: `staff_${Date.now()}@example.com`,
    password: 'password123'
  };
  
  let staffResponse = http.post(`${BASE_URL}/api/staffs`, JSON.stringify(staffData), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  if (staffResponse.status === 200) {
    testStaff.push(JSON.parse(staffResponse.body));
  }
  
  return { testStaff };
}

export default function(data) {
  // Test scenarios with different weights
  let scenario = Math.random();
  
  if (scenario < 0.3) {
    testUserOperations();
  } else if (scenario < 0.6) {
    testCarOperations();
  } else if (scenario < 0.8) {
    testStationOperations();
  } else {
    testBookingOperations();
  }
  
  sleep(1);
}

function testUserOperations() {
  // Create user
  const userData = {
    name: `User_${__VU}_${__ITER}`,
    email: `user_${__VU}_${__ITER}_${Date.now()}@example.com`,
    password: 'password123',
    balance: 100.0,
    cars: []
  };
  
  let createResponse = http.post(`${BASE_URL}/api/users`, JSON.stringify(userData), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  let success = check(createResponse, {
    'User created successfully': (r) => r.status === 200,
    'Response time < 500ms': (r) => r.timings.duration < 500,
  });
  
  errorRate.add(!success);
  
  if (createResponse.status === 200) {
    const user = JSON.parse(createResponse.body);
    
    // Get user by ID
    let getUserResponse = http.get(`${BASE_URL}/api/users/${user.id}`);
    check(getUserResponse, {
      'Get user successful': (r) => r.status === 200,
      'User data matches': (r) => JSON.parse(r.body).email === userData.email,
    });
    
    // Get user balance
    let balanceResponse = http.get(`${BASE_URL}/api/users/${user.id}/balance`);
    check(balanceResponse, {
      'Get balance successful': (r) => r.status === 200,
      'Balance is correct': (r) => parseFloat(r.body) === 100.0,
    });
    
    // Add funds
    let addFundsResponse = http.patch(`${BASE_URL}/api/users/${user.id}/addFunds?amount=50.0`);
    check(addFundsResponse, {
      'Add funds successful': (r) => r.status === 200,
    });
    
    // Update user
    const updatedUserData = {
      name: `Updated_${userData.name}`,
      email: userData.email,
      password: userData.password,
      balance: 150.0,
      cars: []
    };
    
    let updateResponse = http.put(`${BASE_URL}/api/users/${user.id}`, JSON.stringify(updatedUserData), {
      headers: { 'Content-Type': 'application/json' },
    });
    
    check(updateResponse, {
      'Update user successful': (r) => r.status === 200,
    });
    
    // Get all users
    let getAllUsersResponse = http.get(`${BASE_URL}/api/users`);
    check(getAllUsersResponse, {
      'Get all users successful': (r) => r.status === 200,
      'Response is array': (r) => Array.isArray(JSON.parse(r.body)),
    });
    
    // Clean up - delete user
    let deleteResponse = http.del(`${BASE_URL}/api/users/${user.id}`);
    check(deleteResponse, {
      'Delete user successful': (r) => r.status === 200,
    });
  }
}

function testCarOperations() {
  // First create a user to own the car
  const userData = {
    name: `CarOwner_${__VU}_${__ITER}`,
    email: `carowner_${__VU}_${__ITER}_${Date.now()}@example.com`,
    password: 'password123',
    balance: 100.0,
    cars: []
  };
  
  let userResponse = http.post(`${BASE_URL}/api/users`, JSON.stringify(userData), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  if (userResponse.status === 200) {
    const user = JSON.parse(userResponse.body);
    
    // Create car
    const carData = {
      brand: 'Tesla',
      model: 'Model 3',
      plate: `TEST${__VU}${__ITER}`,
      batteryCapacity: 75.0,
      userId: user.id
    };
    
    let createCarResponse = http.post(`${BASE_URL}/api/cars`, JSON.stringify(carData), {
      headers: { 'Content-Type': 'application/json' },
    });
    
    let success = check(createCarResponse, {
      'Car created successfully': (r) => r.status === 200,
      'Response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    errorRate.add(!success);
    
    if (createCarResponse.status === 200) {
      const car = JSON.parse(createCarResponse.body);
      
      // Get car by ID
      let getCarResponse = http.get(`${BASE_URL}/api/cars/${car.id}`);
      check(getCarResponse, {
        'Get car successful': (r) => r.status === 200,
        'Car data matches': (r) => JSON.parse(r.body).plate === carData.plate,
      });
      
      // Get cars by user
      let getUserCarsResponse = http.get(`${BASE_URL}/api/cars/user/${user.id}`);
      check(getUserCarsResponse, {
        'Get user cars successful': (r) => r.status === 200,
        'Cars array returned': (r) => Array.isArray(JSON.parse(r.body)),
      });
      
      // Update car
      const updatedCarData = {
        brand: 'Tesla',
        model: 'Model S',
        plate: carData.plate,
        batteryCapacity: 100.0,
        userId: user.id
      };
      
      let updateCarResponse = http.put(`${BASE_URL}/api/cars/${car.id}`, JSON.stringify(updatedCarData), {
        headers: { 'Content-Type': 'application/json' },
      });
      
      check(updateCarResponse, {
        'Update car successful': (r) => r.status === 200,
      });
      
      // Get all cars
      let getAllCarsResponse = http.get(`${BASE_URL}/api/cars`);
      check(getAllCarsResponse, {
        'Get all cars successful': (r) => r.status === 200,
      });
      
      // Clean up
      http.del(`${BASE_URL}/api/cars/${car.id}`);
    }
    
    // Clean up user
    http.del(`${BASE_URL}/api/users/${user.id}`);
  }
}

function testStationOperations() {
  // First create staff
  const staffData = {
    name: `Staff_${__VU}_${__ITER}`,
    email: `staff_${__VU}_${__ITER}_${Date.now()}@example.com`,
    password: 'password123'
  };
  
  let staffResponse = http.post(`${BASE_URL}/api/staffs`, JSON.stringify(staffData), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  if (staffResponse.status === 200) {
    const staff = JSON.parse(staffResponse.body);
    
    // Create station
    const stationData = {
      name: `Station_${__VU}_${__ITER}`,
      latitude: 40.7128 + Math.random() * 0.1,
      longitude: -74.0060 + Math.random() * 0.1,
      slots: 10,
      pricePerKwh: 0.25,
      staffId: staff.id
    };
    
    let createStationResponse = http.post(`${BASE_URL}/api/stations`, JSON.stringify(stationData), {
      headers: { 'Content-Type': 'application/json' },
    });
    
    let success = check(createStationResponse, {
      'Station created successfully': (r) => r.status === 200,
      'Response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    errorRate.add(!success);
    
    if (createStationResponse.status === 200) {
      const station = JSON.parse(createStationResponse.body);
      
      // Get station by ID
      let getStationResponse = http.get(`${BASE_URL}/api/stations/${station.id}`);
      check(getStationResponse, {
        'Get station successful': (r) => r.status === 200,
        'Station data matches': (r) => JSON.parse(r.body).name === stationData.name,
      });
      
      // Update station
      const updatedStationData = {
        name: `Updated_${stationData.name}`,
        latitude: stationData.latitude,
        longitude: stationData.longitude,
        slots: 15,
        pricePerKwh: 0.30,
        staffId: staff.id
      };
      
      let updateStationResponse = http.put(`${BASE_URL}/api/stations/${station.id}`, JSON.stringify(updatedStationData), {
        headers: { 'Content-Type': 'application/json' },
      });
      
      check(updateStationResponse, {
        'Update station successful': (r) => r.status === 200,
      });
      
      // Get all stations
      let getAllStationsResponse = http.get(`${BASE_URL}/api/stations`);
      check(getAllStationsResponse, {
        'Get all stations successful': (r) => r.status === 200,
      });
      
      // Clean up
      http.del(`${BASE_URL}/api/stations/${station.id}`);
    }
    
    // Get staff by ID
    let getStaffResponse = http.get(`${BASE_URL}/api/staffs/${staff.id}`);
    check(getStaffResponse, {
      'Get staff successful': (r) => r.status === 200,
    });
    
    // Get all staff
    let getAllStaffResponse = http.get(`${BASE_URL}/api/staffs`);
    check(getAllStaffResponse, {
      'Get all staff successful': (r) => r.status === 200,
    });
  }
}

function testBookingOperations() {
  // Create user, car, staff, and station for booking
  const userData = {
    name: `BookingUser_${__VU}_${__ITER}`,
    email: `bookinguser_${__VU}_${__ITER}_${Date.now()}@example.com`,
    password: 'password123',
    balance: 200.0,
    cars: []
  };
  
  let userResponse = http.post(`${BASE_URL}/api/users`, JSON.stringify(userData), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  if (userResponse.status === 200) {
    const user = JSON.parse(userResponse.body);
    
    // Create car
    const carData = {
      brand: 'BMW',
      model: 'i3',
      plate: `BOOK${__VU}${__ITER}`,
      batteryCapacity: 42.0,
      userId: user.id
    };
    
    let carResponse = http.post(`${BASE_URL}/api/cars`, JSON.stringify(carData), {
      headers: { 'Content-Type': 'application/json' },
    });
    
    if (carResponse.status === 200) {
      const car = JSON.parse(carResponse.body);
      
      // Create staff
      const staffData = {
        name: `BookingStaff_${__VU}_${__ITER}`,
        email: `bookingstaff_${__VU}_${__ITER}_${Date.now()}@example.com`,
        password: 'password123'
      };
      
      let staffResponse = http.post(`${BASE_URL}/api/staffs`, JSON.stringify(staffData), {
        headers: { 'Content-Type': 'application/json' },
      });
      
      if (staffResponse.status === 200) {
        const staff = JSON.parse(staffResponse.body);
        
        // Create station
        const stationData = {
          name: `BookingStation_${__VU}_${__ITER}`,
          latitude: 41.0 + Math.random() * 0.1,
          longitude: -73.0 + Math.random() * 0.1,
          slots: 5,
          pricePerKwh: 0.20,
          staffId: staff.id
        };
        
        let stationResponse = http.post(`${BASE_URL}/api/stations`, JSON.stringify(stationData), {
          headers: { 'Content-Type': 'application/json' },
        });
        
        if (stationResponse.status === 200) {
          const station = JSON.parse(stationResponse.body);
          
          // Create booking
          const bookingData = {
            userId: user.id,
            carId: car.id,
            stationId: station.id,
            duration: 60
          };
          
          let createBookingResponse = http.post(`${BASE_URL}/api/bookings`, JSON.stringify(bookingData), {
            headers: { 'Content-Type': 'application/json' },
          });
          
          let success = check(createBookingResponse, {
            'Booking created successfully': (r) => r.status === 200,
            'Response time < 1000ms': (r) => r.timings.duration < 1000,
          });
          
          errorRate.add(!success);
          
          if (createBookingResponse.status === 200) {
            const booking = JSON.parse(createBookingResponse.body);
            
            // Get bookings by user
            let getUserBookingsResponse = http.get(`${BASE_URL}/api/bookings/user/${user.id}`);
            check(getUserBookingsResponse, {
              'Get user bookings successful': (r) => r.status === 200,
              'Bookings array returned': (r) => Array.isArray(JSON.parse(r.body)),
            });
            
            // Update booking status
            let updateStatusResponse = http.patch(`${BASE_URL}/api/bookings/${booking.id}/status?status=COMPLETED`);
            check(updateStatusResponse, {
              'Update booking status successful': (r) => r.status === 200,
            });
          }
          
          // Clean up
          http.del(`${BASE_URL}/api/stations/${station.id}`);
        }
      }
      
      // Clean up
      http.del(`${BASE_URL}/api/cars/${car.id}`);
    }
    
    // Clean up
    http.del(`${BASE_URL}/api/users/${user.id}`);
  }
}

export function teardown(data) {
  console.log('Cleaning up test data...');
  // Additional cleanup if needed
}