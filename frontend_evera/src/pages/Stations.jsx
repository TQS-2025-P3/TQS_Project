import React from 'react';
import { Container, Typography } from '@mui/material';
import StationCard from '../components/StationCard';

const mockStations = [
  { id: 1, name: "Estação Central", location: "Lisboa", slotsAvailable: 3 },
  { id: 2, name: "Eco Park", location: "Porto", slotsAvailable: 1 },
  { id: 3, name: "Carregamento Rápido Sul", location: "Faro", slotsAvailable: 5 },
];

export default function Stations() {
  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Estações de Carregamento</Typography>
      {mockStations.map(station => (
        <StationCard key={station.id} station={station} />
      ))}
    </Container>
  );
}
