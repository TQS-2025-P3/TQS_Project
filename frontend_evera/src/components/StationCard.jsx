import React from 'react';
import { Card, CardContent, Typography, Button } from '@mui/material';

export default function StationCard({ station, onReserve }) {
  const availableSlots = station.slots - station.slotsInUse;

  return (
    <Card sx={{ marginBottom: 2 }}>
      <CardContent>
        <Typography variant="h6">{station.name}</Typography>
        <Typography color="text.secondary">
          Preço por kWh: {station.pricePerKwh.toFixed(2)} €
        </Typography>
        <Typography>
          Slots disponíveis: {availableSlots}
        </Typography>
        <Button variant="contained" sx={{ mt: 1 }} onClick={onReserve}>
          Reservar
        </Button>
      </CardContent>
    </Card>
  );
}
