import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import {
  Container, Typography, CircularProgress, Dialog, DialogTitle,
  DialogContent, DialogActions, Button, RadioGroup, FormControlLabel, Radio, Slider 
} from '@mui/material';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';


delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

export default function MapPage() {
  const userId = 1;
  const [stations, setStations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [vehicles, setVehicles] = useState([]);
  const [selectedStation, setSelectedStation] = useState(null);
  const [selectedCarId, setSelectedCarId] = useState(null);
  const [selectedCar, setSelectedCar] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [successDialogOpen, setSuccessDialogOpen] = useState(false);
  const [reservationDetails, setReservationDetails] = useState(null);
  const [userBalance, setUserBalance] = useState(null);
  const [errorDialogOpen, setErrorDialogOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [maxPrice, setMaxPrice] = useState(0.40); 
  const [minSlots, setMinSlots] = useState(1); 





  useEffect(() => {
    fetch("http://localhost:8080/api/stations")
      .then(res => res.json())
      .then(data => {
        setStations(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Erro ao buscar estações:", err);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (selectedCarId) {
      const found = vehicles.find(v => v.id.toString() === selectedCarId);
      setSelectedCar(found);
    } else {
      setSelectedCar(null);
    }
  }, [selectedCarId, vehicles]);

  const calculateCost = () => {
    if (!selectedCar || !selectedStation) return "0.00";
    const price = selectedStation.pricePerKwh || 0.10;
    return (selectedCar.batteryCapacity * price).toFixed(2);
  };
  

  const handleReserve = async (station) => {
    setSelectedStation(station);
    try {
      const res = await fetch(`http://localhost:8080/api/cars/user/${userId}`);
      const data = await res.json();
      setVehicles(data);
      setModalOpen(true);
    } catch (error) {
      console.error("Erro ao buscar veículos:", error);
    }
  };

  const handleConfirmReserve = async () => {
    const bookingData = {
      userId: userId,
      carId: parseInt(selectedCarId),
      stationId: selectedStation.id,
      duration: 60
    };
  
    try {
      const response = await fetch("http://localhost:8080/api/bookings", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(bookingData)
      });
  
      if (!response.ok) {
        const errText = await response.text();
        console.log("Mensagem de erro bruta:", errText);
        const lowerErr = errText.toLowerCase();
      
        if (lowerErr.includes("reserva ativa")) {
          throw new Error("Este veículo já possui uma reserva ativa. Finalize-a antes de reservar novamente.");
        } else if (lowerErr.includes("saldo insuficiente")) {
          throw new Error("Saldo insuficiente. Por favor, adicione fundos à sua conta.");
        } else if (lowerErr.includes("slots disponíveis")) {
          throw new Error("Esta estação está atualmente sem vagas disponíveis.");
        } else {
          throw new Error("Erro inesperado ao criar reserva.");
        }
      }
      
  
      const result = await response.json();
  
      window.dispatchEvent(new Event("userUpdated"));
  
      setReservationDetails({
        station: result.chargerStation.name,
        vehicle: `${result.car.brand} ${result.car.model}`,
        cost: (result.car.batteryCapacity * result.chargerStation.pricePerKwh).toFixed(2)
      });
      
  
      const userRes = await fetch(`http://localhost:8080/api/users/${userId}`);
      const userData = await userRes.json();
      setUserBalance(userData.balance);
  
      const updatedStations = await fetch("http://localhost:8080/api/stations").then(r => r.json());
      setStations(updatedStations);
  
      setModalOpen(false);
      setSelectedCarId(null);
      setSelectedCar(null);
      setSuccessDialogOpen(true);
    } catch (error) {
      console.error("Erro ao confirmar reserva:", error);
      setErrorMessage(error.message);
      setErrorDialogOpen(true);
    }
      
  };
  
  
  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Mapa de Estações</Typography>
      <Typography variant="body1" gutterBottom>
        Clique numa estação para ver mais detalhes e reservar.
      </Typography>
  
      {/* Filtro por preço máximo */}
      <div style={{ marginBottom: '1.5rem', maxWidth: 300 }}>
  <Typography gutterBottom>Preço máximo por kWh: {maxPrice.toFixed(2)} €</Typography>
  <Slider
    value={maxPrice}
    min={0}
    max={0.60}
    step={0.01}
    onChange={(e, newValue) => setMaxPrice(newValue)}
    valueLabelDisplay="auto"
    marks={[
      { value: 0, label: '0 €' },
      { value: 0.3, label: '0.30 €' },
      { value: 0.6, label: '0.60 €' }
    ]}
    sx={{ color: '#1976d2' }}
  />

  <Typography gutterBottom sx={{ mt: 3 }}>
    Mínimo de slots disponíveis: {minSlots}
  </Typography>
  <Slider
    value={minSlots}
    min={0}
    max={5}
    step={1}
    onChange={(e, newValue) => setMinSlots(newValue)}
    valueLabelDisplay="auto"
    marks
    sx={{ color: '#388e3c' }}
  />
</div>


  
      {loading ? (
        <CircularProgress />
      ) : (
        <div style={{ height: '70vh', marginTop: '2rem', borderRadius: '8px', overflow: 'hidden' }}>
          <MapContainer center={[39.5, -8]} zoom={6} style={{ height: '100%', width: '100%' }}>
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {stations
  .filter(station =>
    station.pricePerKwh <= maxPrice &&
    station.availableSlots >= minSlots
  )
              .map((station) => (
                <Marker key={station.id} position={[station.latitude, station.longitude]}>
                  <Popup>
                    <strong>{station.name}</strong><br />
                    Preço: {station.pricePerKwh.toFixed(2)} €/kWh<br />
                    Slots disponíveis: {station.availableSlots}<br />
                    <button
                      style={{
                        marginTop: '8px',
                        padding: '6px 12px',
                        backgroundColor: '#1976d2',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                      }}
                      onClick={() => handleReserve(station)}
                    >
                      Reservar
                    </button>
                  </Popup>
                </Marker>
              ))}
          </MapContainer>
        </div>
      )}
  
      {/* Modal seleção de carro */}
      <Dialog open={modalOpen} onClose={() => setModalOpen(false)}>
        <DialogTitle>Escolha um veículo</DialogTitle>
        <DialogContent>
          {vehicles.length === 0 ? (
            <Typography>Não há veículos registados.</Typography>
          ) : (
            <>
              <RadioGroup value={selectedCarId} onChange={(e) => setSelectedCarId(e.target.value)}>
                {vehicles.map(car => (
                  <FormControlLabel
                    key={car.id}
                    value={car.id.toString()}
                    control={<Radio />}
                    label={`${car.brand} ${car.model} (${car.plate}) - ${car.batteryCapacity} kWh`}
                  />
                ))}
              </RadioGroup>
              {selectedCar && (
                <Typography sx={{ mt: 2 }}>
                  <strong>Custo estimado:</strong> {calculateCost()} €
                </Typography>
              )}
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setModalOpen(false)}>Cancelar</Button>
          <Button onClick={handleConfirmReserve} disabled={!selectedCarId} variant="contained">
            Confirmar Reserva
          </Button>
        </DialogActions>
      </Dialog>
  
      {/* Dialog de sucesso */}
      <Dialog open={successDialogOpen} onClose={() => setSuccessDialogOpen(false)}>
        <DialogTitle>Reserva efetuada com sucesso!</DialogTitle>
        <DialogContent>
          {reservationDetails && (
            <>
              <Typography>Estação: {reservationDetails.station}</Typography>
              <Typography>Veículo: {reservationDetails.vehicle}</Typography>
              <Typography>Custo: {reservationDetails.cost} €</Typography>
              <Typography>Saldo restante: {userBalance?.toFixed(2)} €</Typography>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSuccessDialogOpen(false)}>Fechar</Button>
        </DialogActions>
      </Dialog>
  
      {/* Dialog de erro */}
      <Dialog open={errorDialogOpen} onClose={() => setErrorDialogOpen(false)}>
        <DialogTitle sx={{ color: 'error.main' }}>Erro na Reserva</DialogTitle>
        <DialogContent>
          <Typography>{errorMessage}</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setErrorDialogOpen(false)}>Fechar</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
  
  
  
}