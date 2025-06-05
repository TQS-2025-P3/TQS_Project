#!/bin/bash

show_help() {
    echo "  --full        Criar sistema completo do zero (APAGA TUDO)"
    echo "  --add-data    Adicionar mais dados históricos (sem apagar)"
    echo ""
}

wait_for_api() {
  sleep 0.1
}

insert_batch() {
  local batch_name=$1
  local sql_commands=$2
    
  sudo docker exec mysql_server mysql -uEvera_staff -psenha1234 -DEvera -e "$sql_commands" >/dev/null 2>&1
  
  if [ $? -eq 0 ]; then
    echo "$batch_name inserido com sucesso"
  else
    echo "Erro ao inserir $batch_name"
  fi
  
  sleep 0.2
}

create_full_system() {
    echo ""


    sudo docker exec mysql_server mysql -uEvera_staff -psenha1234 -DEvera -e "
    DELETE FROM book_charge;
    DELETE FROM car;
    DELETE FROM app_user;
    DELETE FROM charger_station;
    DELETE FROM staff;
    ALTER TABLE book_charge AUTO_INCREMENT = 1;
    ALTER TABLE car AUTO_INCREMENT = 1;
    ALTER TABLE app_user AUTO_INCREMENT = 1;
    ALTER TABLE charger_station AUTO_INCREMENT = 1;
    ALTER TABLE staff AUTO_INCREMENT = 1;
    " 2>/dev/null


    echo ""

    curl -s -X POST http://localhost:8080/api/staffs \
      -H "Content-Type: application/json" \
      -d '{"name": "Maria Silva", "email": "maria.silva@evera.com", "password": "staff123"}' > /dev/null

    wait_for_api

 
    echo ""

    users=(
      "Pedro Santos,pedro.santos@evera.com"
      "Ana Costa,ana.costa@evera.com" 
      "João Silva,joao.silva@evera.com"
      "Sofia Pereira,sofia.pereira@evera.com"
      "Miguel Ferreira,miguel.ferreira@evera.com"
      "Carla Oliveira,carla.oliveira@evera.com"
      "Rui Martins,rui.martins@evera.com"
      "Inês Rodrigues,ines.rodrigues@evera.com"
      "Tiago Almeida,tiago.almeida@evera.com"
      "Beatriz Cunha,beatriz.cunha@evera.com"
      "Carlos Moreira,carlos.moreira@evera.com"
      "Luísa Neves,luisa.neves@evera.com"
      "André Sousa,andre.sousa@evera.com"
      "Catarina Lopes,catarina.lopes@evera.com"
      "Nuno Ribeiro,nuno.ribeiro@evera.com"
      "Rita Barbosa,rita.barbosa@evera.com"
      "Bruno Carvalho,bruno.carvalho@evera.com"
      "Marta Gonçalves,marta.goncalves@evera.com"
      "Diogo Fernandes,diogo.fernandes@evera.com"
      "Helena Araújo,helena.araujo@evera.com"
      "Vasco Melo,vasco.melo@evera.com"
      "Patrícia Cardoso,patricia.cardoso@evera.com"
      "Ricardo Pinto,ricardo.pinto@evera.com"
      "Susana Correia,susana.correia@evera.com"
      "Hugo Torres,hugo.torres@evera.com"
      "Vera Machado,vera.machado@evera.com"
      "Paulo Castro,paulo.castro@evera.com"
      "Joana Freitas,joana.freitas@evera.com"
      "Filipe Antunes,filipe.antunes@evera.com"
      "Marina Azevedo,marina.azevedo@evera.com"
    )

    user_id=1
    for user_data in "${users[@]}"; do
      IFS=',' read -r name email <<< "$user_data"
      
      curl -s -X POST http://localhost:8080/api/users \
        -H "Content-Type: application/json" \
        -d "{\"name\":\"$name\",\"email\":\"$email\",\"password\":\"user123\"}" > /dev/null

      funds=$(awk -v min=150 -v max=500 'BEGIN{srand(); printf "%.2f", min+rand()*(max-min)}')
      curl -s -X PATCH "http://localhost:8080/api/users/$user_id/addFunds?amount=$funds" > /dev/null
      
      echo " $name criado com $funds€"
      wait_for_api
      user_id=$((user_id + 1))
    done



    stations=(
      "Lisboa Central,38.7169,-9.1399,8,0.18"
      "Porto Norte,41.1496,-8.6109,6,0.15"
      "Faro Sul,37.0194,-7.9304,5,0.22"
      "Braga Centro,41.5454,-8.4265,4,0.16"
      "Évora Plaza,38.5711,-7.9092,4,0.19"
      "Setúbal Charge,38.5244,-8.8882,3,0.17"
      "Viseu Power Hub,40.661,-7.9097,12,0.20"
      "Coimbra Estação,40.2033,-8.4103,6,0.18"
      "Cascais Beach,38.6979,-9.4215,5,0.21"
      "Aveiro Center,40.6405,-8.6538,4,0.16"
      "Funchal Marina,32.6669,-16.9241,3,0.25"
      "Angra Heroísmo,38.6551,-27.2208,2,0.24"
      "Vila Real Norte,41.3006,-7.7442,3,0.17"
      "Bragança Montanha,41.8071,-6.7575,2,0.18"
      "Guarda Serra,40.5367,-7.2676,3,0.19"
      "Castelo Branco,39.8222,-7.4931,4,0.17"
      "Santarém Ribatejo,39.2362,-8.6868,5,0.16"
      "Leiria Centro,39.7437,-8.8071,6,0.18"
      "Caldas Rainha,39.4032,-9.1381,3,0.19"
      "Óbidos Turismo,39.3606,-9.1569,2,0.21"
      "Torres Vedras,39.0910,-9.2581,4,0.17"
      "Sintra Romântica,38.7972,-9.3880,5,0.20"
      "Mafra Convento,38.9367,-9.3264,3,0.18"
      "Ericeira Surf,38.9631,-9.4197,4,0.22"
      "Peniche Costa,39.3558,-9.3814,3,0.19"
      "Nazaré Ondas,39.6013,-9.0705,4,0.20"
      "Alcobaça Mosteiro,39.5480,-8.9780,2,0.17"
      "Batalha Vitória,39.6606,-8.8254,3,0.18"
      "Porto Santo,33.0738,-16.3500,2,0.26"
      "Ponta Delgada,37.7394,-25.6681,4,0.23"
      "Beja Alentejo,38.0150,-7.8632,3,0.18"
      "Portalegre Fronteira,39.2967,-7.4307,2,0.19"
      "Elvas Muralhas,38.8814,-7.1634,3,0.17"
      "Estremoz Mármore,38.8429,-7.5868,2,0.18"
      "Monsaraz Medieval,38.4437,-7.3806,2,0.20"
      "Mértola Guadiana,37.6405,-7.6618,2,0.19"
      "Sagres Fim Mundo,37.0095,-8.9480,3,0.23"
      "Lagos Dourada,37.1022,-8.6756,5,0.21"
      "Portimão Marina,37.1365,-8.5756,6,0.20"
      "Silves Castelo,37.1881,-8.4383,3,0.19"
      "Monchique Serra,37.3167,-8.5542,2,0.22"
      "Aljezur Costa,37.3192,-8.8003,3,0.21"
      "Vila Real St António,37.1941,-7.4179,4,0.18"
      "Tavira Histórica,37.1267,-7.6483,4,0.19"
      "Olhão Ria Formosa,37.0267,-7.8431,5,0.17"
      "Quarteira Resort,37.0688,-8.0998,6,0.22"
      "Albufeira Praia,37.0886,-8.2504,8,0.23"
      "Vilamoura Luxo,37.0768,-8.1174,7,0.24"
      "Loulé Interior,37.1358,-8.0222,4,0.18"
      "São Brás Alportel,37.1528,-7.8944,3,0.17"
    )

    station_id=1
    for station in "${stations[@]}"; do
      IFS=',' read -r name lat lng slots price <<< "$station"
      
      curl -s -X POST http://localhost:8080/api/stations \
        -H "Content-Type: application/json" \
        -d "{
          \"name\": \"$name\",
          \"latitude\": $lat,
          \"longitude\": $lng,
          \"slots\": $slots,
          \"pricePerKwh\": $price,
          \"staffId\": 1
        }" > /dev/null

      echo " $name ($slots slots) criada"
      wait_for_api
      station_id=$((station_id + 1))
    done

    echo ""

    brands=("Tesla" "BMW" "Audi" "Mercedes" "Volkswagen" "Nissan" "Hyundai" "Kia" "Renault" "Peugeot" "Ford" "Volvo" "Jaguar" "Porsche" "Mini")
    models=("Model S" "Model 3" "Model Y" "i3" "iX" "i4" "e-tron" "Q4" "EQC" "EQA" "e-Golf" "ID.3" "ID.4" "Leaf" "Ariya" "Kona" "Ioniq" "EV6" "Zoe" "Megane" "e-208" "e-2008" "Mustang" "XC40" "I-Pace" "Taycan" "Cooper SE")

    car_id=1
    for user_id in {1..30}; do
      for car_num in {1..3}; do
        brand_idx=$(((car_id + user_id) % ${#brands[@]}))
        model_idx=$(((car_id + car_num + 3) % ${#models[@]}))
        
        brand="${brands[$brand_idx]}"
        model="${models[$model_idx]}"
        
        letters="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        letter1=${letters:$((car_id % 26)):1}
        letter2=${letters:$(((car_id + 7) % 26)):1}
        plate=$(printf "%02d-%c%c-%02d" $((car_id % 100)) $letter1 $letter2 $((car_id % 100)))
        
        battery=$(awk -v min=35 -v max=120 'BEGIN{srand('$car_id'); printf "%.1f", min+rand()*(max-min)}')
        
        curl -s -X POST http://localhost:8080/api/cars \
          -H "Content-Type: application/json" \
          -d "{
            \"brand\": \"$brand\",
            \"model\": \"$model\",
            \"plate\": \"$plate\",
            \"batteryCapacity\": $battery,
            \"userId\": $user_id
          }" > /dev/null

        echo " Car $car_id: $brand $model ($plate) -> User $user_id"
        wait_for_api
        car_id=$((car_id + 1))
      done
    done

    sleep 2

    add_historical_data

    for i in {1..10}; do
      user_id=$((RANDOM % 30 + 1))
      car_start=$(((user_id - 1) * 3 + 1))
      car_id=$((car_start + (i % 3)))
      station_id=$((RANDOM % 50 + 1))
      duration=$((RANDOM % 60 + 60))
      
      curl -s -X POST http://localhost:8080/api/bookings \
        -H "Content-Type: application/json" \
        -d "{
          \"userId\": $user_id,
          \"carId\": $car_id,
          \"stationId\": $station_id,
          \"duration\": $duration
        }" > /dev/null
      
      echo " Reserva ativa $i criada (User $user_id, Car $car_id)"
      wait_for_api
    done

    echo ""
    add_historical_data

}


add_historical_data() {
    
    insert_batch "Janeiro 2025 - Lote 1" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (1, 1, 7, '2025-01-03 08:15:23', 75, 'COMPLETED', 27.00),
    (2, 4, 1, '2025-01-03 18:22:45', 90, 'COMPLETED', 24.30),
    (3, 7, 15, '2025-01-05 07:45:12', 60, 'COMPLETED', 21.60),
    (4, 10, 25, '2025-01-05 17:30:56', 85, 'COMPLETED', 32.30),
    (5, 13, 8, '2025-01-08 08:45:33', 70, 'COMPLETED', 25.20),
    (6, 16, 12, '2025-01-08 12:15:22', 95, 'COMPLETED', 41.80),
    (7, 19, 3, '2025-01-10 18:15:44', 65, 'COMPLETED', 28.60),
    (8, 22, 22, '2025-01-12 07:55:11', 80, 'COMPLETED', 35.20),
    (9, 25, 33, '2025-01-12 14:30:55', 105, 'COMPLETED', 39.90),
    (10, 28, 5, '2025-01-15 08:30:22', 75, 'COMPLETED', 24.75);
    "

    insert_batch "Janeiro 2025 - Lote 2" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (11, 31, 18, '2025-01-15 17:45:33', 85, 'COMPLETED', 30.60),
    (12, 34, 28, '2025-01-18 11:20:45', 60, 'COMPLETED', 20.40),
    (13, 37, 41, '2025-01-20 18:30:12', 90, 'COMPLETED', 41.40),
    (14, 40, 9, '2025-01-20 08:10:34', 70, 'COMPLETED', 22.40),
    (15, 43, 17, '2025-01-22 13:45:22', 100, 'COMPLETED', 36.00),
    (16, 46, 31, '2025-01-25 07:50:11', 85, 'COMPLETED', 38.25),
    (17, 49, 14, '2025-01-25 17:20:44', 75, 'COMPLETED', 27.00),
    (18, 52, 24, '2025-01-26 15:30:33', 65, 'COMPLETED', 24.70),
    (19, 55, 39, '2025-01-26 08:25:55', 95, 'COMPLETED', 43.70),
    (20, 58, 6, '2025-01-28 18:40:22', 80, 'COMPLETED', 27.20);
    "

    insert_batch "Fevereiro 2025 - Lote 1" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (21, 61, 29, '2025-02-01 08:20:15', 80, 'COMPLETED', 28.80),
    (22, 64, 7, '2025-02-01 18:10:45', 95, 'COMPLETED', 34.20),
    (23, 67, 19, '2025-02-03 07:40:22', 70, 'COMPLETED', 25.20),
    (24, 70, 30, '2025-02-03 17:55:33', 85, 'COMPLETED', 32.30),
    (25, 73, 11, '2025-02-05 08:35:44', 90, 'COMPLETED', 32.40),
    (26, 76, 21, '2025-02-05 13:25:11', 75, 'COMPLETED', 27.00),
    (27, 79, 42, '2025-02-07 18:45:55', 100, 'COMPLETED', 46.00),
    (28, 82, 4, '2025-02-07 07:30:22', 65, 'COMPLETED', 20.80),
    (29, 85, 27, '2025-02-10 15:20:33', 110, 'COMPLETED', 41.80),
    (30, 88, 36, '2025-02-10 08:15:44', 85, 'COMPLETED', 32.30);
    "

    insert_batch "Fevereiro 2025 - Lote 2" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (1, 2, 13, '2025-02-12 17:40:11', 75, 'COMPLETED', 24.75),
    (2, 5, 23, '2025-02-12 12:30:22', 95, 'COMPLETED', 34.20),
    (3, 8, 46, '2025-02-14 18:25:33', 70, 'COMPLETED', 32.20),
    (4, 11, 10, '2025-02-14 08:50:44', 80, 'COMPLETED', 25.60),
    (5, 14, 32, '2025-02-17 14:15:55', 105, 'COMPLETED', 39.90),
    (6, 17, 20, '2025-02-17 07:45:11', 90, 'COMPLETED', 32.40),
    (7, 20, 40, '2025-02-19 17:30:22', 85, 'COMPLETED', 38.25),
    (8, 23, 15, '2025-02-19 11:40:33', 75, 'COMPLETED', 22.50),
    (9, 26, 37, '2025-02-21 18:20:44', 100, 'COMPLETED', 45.00),
    (10, 29, 26, '2025-02-21 08:10:55', 70, 'COMPLETED', 25.20);
    "

    insert_batch "Março 2025 - Lote 1" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (11, 32, 43, '2025-03-01 08:15:12', 85, 'COMPLETED', 39.10),
    (12, 35, 9, '2025-03-01 18:30:23', 90, 'COMPLETED', 32.40),
    (13, 38, 34, '2025-03-03 07:45:34', 75, 'COMPLETED', 28.50),
    (14, 41, 18, '2025-03-03 17:20:45', 100, 'COMPLETED', 36.00),
    (15, 44, 45, '2025-03-05 08:50:56', 80, 'COMPLETED', 36.80),
    (16, 47, 6, '2025-03-05 14:25:07', 95, 'COMPLETED', 32.30),
    (17, 50, 25, '2025-03-07 18:40:18', 70, 'COMPLETED', 31.50),
    (18, 53, 44, '2025-03-07 07:55:29', 110, 'COMPLETED', 50.60),
    (19, 56, 16, '2025-03-10 12:10:40', 85, 'COMPLETED', 30.60),
    (20, 59, 39, '2025-03-10 08:25:51', 90, 'COMPLETED', 40.50);
    "

    insert_batch "Abril 2025 - Lote 1" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (21, 62, 1, '2025-04-01 08:15:12', 85, 'COMPLETED', 25.50),
    (22, 65, 24, '2025-04-01 18:30:23', 90, 'COMPLETED', 34.20),
    (23, 68, 42, '2025-04-02 07:45:34', 75, 'COMPLETED', 34.50),
    (24, 71, 13, '2025-04-02 17:20:45', 100, 'COMPLETED', 33.00),
    (25, 74, 31, '2025-04-03 08:50:56', 80, 'COMPLETED', 30.40),
    (26, 77, 50, '2025-04-03 14:25:07', 95, 'COMPLETED', 45.60),
    (27, 80, 8, '2025-04-04 18:40:18', 70, 'COMPLETED', 25.20),
    (28, 83, 27, '2025-04-04 07:55:29', 110, 'COMPLETED', 41.80),
    (29, 86, 35, '2025-04-05 12:10:40', 85, 'COMPLETED', 32.30),
    (30, 89, 19, '2025-04-05 08:25:51', 90, 'COMPLETED', 32.40);
    "

    insert_batch "Maio 2025 - Lote 1" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (1, 3, 47, '2025-05-01 08:15:33', 105, 'COMPLETED', 48.30),
    (2, 6, 22, '2025-05-01 17:30:44', 80, 'COMPLETED', 28.80),
    (3, 9, 38, '2025-05-02 07:45:55', 95, 'COMPLETED', 42.75),
    (4, 12, 5, '2025-05-02 18:01:06', 75, 'COMPLETED', 24.75),
    (5, 15, 29, '2025-05-03 12:16:17', 110, 'COMPLETED', 41.80),
    (6, 18, 46, '2025-05-03 08:31:28', 85, 'COMPLETED', 39.10),
    (7, 21, 14, '2025-05-05 17:46:39', 90, 'COMPLETED', 27.00),
    (8, 24, 33, '2025-05-05 13:01:50', 100, 'COMPLETED', 38.00),
    (9, 27, 17, '2025-05-06 18:17:01', 70, 'COMPLETED', 25.20),
    (10, 30, 41, '2025-05-06 08:32:12', 95, 'COMPLETED', 43.70);
    "

    insert_batch "Junho 2025 - Atual" "
    INSERT INTO book_charge (user_id, car_id, station_id, time, duration, status, cost) VALUES
    (11, 33, 7, '2025-06-01 08:15:22', 85, 'COMPLETED', 30.60),
    (12, 36, 25, '2025-06-01 17:30:33', 90, 'COMPLETED', 40.50),
    (13, 39, 42, '2025-06-02 07:45:44', 75, 'COMPLETED', 34.50),
    (14, 42, 16, '2025-06-02 18:00:55', 100, 'COMPLETED', 36.00),
    (15, 45, 33, '2025-06-03 12:16:06', 80, 'COMPLETED', 30.40),
    (16, 48, 8, '2025-06-03 08:31:17', 95, 'COMPLETED', 34.20),
    (17, 51, 49, '2025-06-04 17:46:28', 85, 'COMPLETED', 40.80),
    (18, 54, 21, '2025-06-04 13:01:39', 90, 'COMPLETED', 32.40),
    (19, 57, 38, '2025-06-04 18:16:50', 70, 'COMPLETED', 31.50),
    (20, 60, 15, '2025-06-04 08:32:01', 105, 'COMPLETED', 31.50);
    "

}


main() {
    case "${1:-}" in
        --full)
            create_full_system
            ;;
        
        --add-data)
            add_historical_data
            ;;
        --help|*)
            show_help
            ;;
    esac
}


if ! sudo docker ps > /dev/null 2>&1; then
    echo " Erro: Docker não está a rodar ou não está acessível."
    echo " Executa: sudo systemctl start docker"
    exit 1
fi

if ! sudo docker ps -a | grep -q mysql_server; then
    echo " Erro: Container mysql_server não encontrado."
    echo " Executa primeiro o container MySQL do projeto."
    exit 1
fi

if ! sudo docker ps | grep -q mysql_server; then
    echo "  Container mysql_server não está a rodar. Iniciando..."
    sudo docker start mysql_server
    sleep 3
fi

if ! curl -s http://localhost:8080/api/users > /dev/null 2>&1; then
    echo "  API não está acessível em http://localhost:8080"
    echo " Certifica-te de que a aplicação Spring Boot está a rodar."
fi

echo ""

# Executar função principal
main "$@"