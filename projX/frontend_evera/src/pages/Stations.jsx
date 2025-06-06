import React, { useEffect, useState } from 'react';
import {
  Container, Typography, Slider, Dialog, DialogTitle, DialogContent, DialogActions,
  Button, RadioGroup, FormControlLabel, Radio, CircularProgress, Grid
} from '@mui/material';
import StationCard from '../components/StationCard';

export default function Stations() {
  const userId = 1;
  const [stations, setStations] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [selectedStation, setSelectedStation] = useState(null);
  const [selectedCarId, setSelectedCarId] = useState(null);
  const [selectedCar, setSelectedCar] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [successDialogOpen, setSuccessDialogOpen] = useState(false);
  const [errorDialogOpen, setErrorDialogOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [reservationDetails, setReservationDetails] = useState(null);
  const [userBalance, setUserBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [maxPrice, setMaxPrice] = useState(0.50);
  const [minSlots, setMinSlots] = useState(1);

  useEffect(() => {
    fetch("http://localhost:8080/api/stations")
      .then(res => res.json())
      .then(data => {
        setStations(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Erro ao carregar estações:", err);
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

  const handleReserveClick = async (station) => {
    setSelectedStation(station);
    try {
      const res = await fetch(`http://localhost:8080/api/cars/user/${userId}`);
      const data = await res.json();
      setVehicles(data);
      setModalOpen(true);
    } catch (err) {
      console.error("Erro ao carregar veículos:", err);
    }
  };

  const handleConfirm = async () => {
    const bookingData = {
      userId,
      carId: parseInt(selectedCarId),
      stationId: selectedStation.id,
      duration: 60
    };

    try {
      const response = await fetch("http://localhost:8080/api/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookingData)
      });

      if (!response.ok) {
        const errText = await response.text();
        const lowerErr = errText.toLowerCase();

        if (lowerErr.includes("reserva ativa")) {
          throw new Error("Este veículo já possui uma reserva ativa.");
        } else if (lowerErr.includes("saldo insuficiente")) {
          throw new Error("Saldo insuficiente. Por favor, adicione fundos à sua conta.");
        } else if (lowerErr.includes("slots disponíveis")) {
          throw new Error("Esta estação está sem vagas disponíveis.");
        } else {
          throw new Error("Erro inesperado ao criar reserva.");
        }
      }

      const result = await response.json();
      const userRes = await fetch(`http://localhost:8080/api/users/${userId}`);
      const userData = await userRes.json();
      setUserBalance(userData.balance);

      setReservationDetails({
        station: result.chargerStation.name,
        vehicle: `${result.car.brand} ${result.car.model}`,
        cost: (result.car.batteryCapacity * result.chargerStation.pricePerKwh).toFixed(2)
      });

      setModalOpen(false);
      setSelectedCarId(null);
      setSelectedCar(null);
      setSuccessDialogOpen(true);

      // Refresh stations
      const updatedStations = await fetch("http://localhost:8080/api/stations").then(r => r.json());
      setStations(updatedStations);

    } catch (error) {
      console.error("Erro ao confirmar reserva:", error);
      setErrorMessage(error.message);
      setErrorDialogOpen(true);
    }
  };

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Estações de Carregamento</Typography>

      {/* Filtro de Preço */}
      <div style={{ marginBottom: '1rem', maxWidth: 300 }}>
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
            { value: 0.6, label: '0.60 €' },
          ]}
          sx={{ color: '#1976d2' }}
        />
      </div>
      <div style={{ marginBottom: '1.5rem', maxWidth: 300 }}>
        <Typography gutterBottom>Slots mínimos disponíveis: {minSlots}</Typography>
        <Slider
          value={minSlots}
          min={1}
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
        <Grid container spacing={2} sx={{ mt: 2 }}>
          {stations
            .filter(station =>
              station.pricePerKwh <= maxPrice &&
              station.availableSlots >= minSlots
            )
            .map(station => (
              <Grid item xs={12} sm={6} md={3} key={station.id}>
                <StationCard station={station} onReserve={() => handleReserveClick(station)} />
              </Grid>
            ))}
        </Grid>
      )}

      {/* Dialog Seleção de Veículo */}
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
                  <strong>Custo estimado:</strong> {(selectedCar.batteryCapacity * selectedStation.pricePerKwh).toFixed(2)} €
                </Typography>
              )}
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setModalOpen(false)}>Cancelar</Button>
          <Button onClick={handleConfirm} disabled={!selectedCarId} variant="contained">
            Confirmar Reserva
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog de Sucesso */}
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

      {/* Dialog de Erro */}
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