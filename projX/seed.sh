#!/bin/bash

echo "📦 A semear dados no backend..."

# --- Utilizador principal ---
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Pedro", "email": "pedro@example.com", "password": "1234"}'

curl -s -X PATCH "http://localhost:8080/api/users/1/addFunds?amount=25.00"

# --- Staff ---
curl -s -X POST http://localhost:8080/api/staffs \
  -H "Content-Type: application/json" \
  -d '{"name": "Maria Silva", "email": "maria.silva@example.com", "password": "segredo123"}'

# --- Carros do Pedro ---
curl -s -X POST http://localhost:8080/api/cars -H "Content-Type: application/json" -d '{"brand":"Tesla","model":"Model S","plate":"00-AA-11","batteryCapacity":100,"userId":1}'
curl -s -X POST http://localhost:8080/api/cars -H "Content-Type: application/json" -d '{"brand":"Nissan","model":"Leaf","plate":"11-BB-22","batteryCapacity":40,"userId":1}'

# --- Estações ---
stations=(
  "Estação Central Lisboa,38.7169,-9.1399,4"
  "Eco Park Porto,41.1496,-8.6109,3"
  "Carregamento Sul Faro,37.0194,-7.9304,4"
)

for station in "${stations[@]}"; do
  IFS=',' read -r name lat lng slots <<< "$station"
  price=$(awk -v min=0.10 -v max=0.50 'BEGIN{srand(); printf "%.2f", min+rand()*(max-min)}')
  curl -s -X POST http://localhost:8080/api/stations \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"$name\",
      \"latitude\": $lat,
      \"longitude\": $lng,
      \"slots\": $slots,
      \"pricePerKwh\": $price,
      \"staffId\": 1
    }"
done

# --- Outros Utilizadores + carros + fundos ---
for i in {2..4}; do
  curl -s -X POST http://localhost:8080/api/users \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"User$i\",\"email\":\"user$i@example.com\",\"password\":\"pw$i\"}"

  curl -s -X PATCH "http://localhost:8080/api/users/$i/addFunds?amount=50.00"

  curl -s -X POST http://localhost:8080/api/cars \
    -H "Content-Type: application/json" \
    -d "{
      \"brand\": \"Brand$i\",
      \"model\": \"Model$i\",
      \"plate\": \"0$i-AA-$i$i\",
      \"batteryCapacity\": $((30 + i * 10)),
      \"userId\": $i
    }"
done

curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "carId": 3,
    "stationId": 1,
    "duration": 60
  }'

curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "carId": 2,
    "stationId": 14,
    "duration": 60
  }'

curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 3,
    "carId": 6,
    "stationId": 10,
    "duration": 60
  }'


# Lista de estações
stations=(
  "Estação Central Lisboa,38.7169,-9.1399,4"
  "Eco Park Porto,41.1496,-8.6109,3"
  "Carregamento Sul Faro,37.0194,-7.9304,4"
  "Posto Braga,41.5454,-8.4265,3"
  "Évora Energy,38.5711,-7.9092,4"
  "Setúbal Charge,38.5244,-8.8882,2"
  "Viseu Power Hub,40.661,-7.9097,5"
  "Coimbra Estação,40.2033,-8.4103,3"
  "Cascais Spot,38.6979,-9.4215,3"
  "Aveiro Point,40.6405,-8.6538,2"
  "Leiria Norte,39.7436,-8.8071,4"
  "Guimarães Green,41.4445,-8.2962,2"
  "Beja Plug-in,38.0151,-7.8632,5"
  "Sintra Charging,38.8029,-9.3817,4"
  "Portimão Hub,37.1366,-8.5377,3"
  "Torres Vedras EV,39.0922,-9.2589,2"
  "Guarda Estação,40.5373,-7.2671,3"
  "Loulé Energy,37.137,-8.0237,4"
  "Funchal Island Station,32.6669,-16.9241,3"
  "Ponta Delgada Plug,37.7412,-25.6756,2"
)

# Inserir estações com preço aleatório entre 0.10 e 0.50 €/kWh
for station in "${stations[@]}"; do
  IFS=',' read -r name lat lng slots <<< "$station"
  price=$(awk -v min=0.10 -v max=0.50 'BEGIN{srand(); printf "%.2f", min+rand()*(max-min)}')
  curl -s -X POST http://localhost:8080/api/stations \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"$name\",
      \"latitude\": $lat,
      \"longitude\": $lng,
      \"slots\": $slots,
      \"pricePerKwh\": $price,
      \"staffId\": 1
    }"
done
