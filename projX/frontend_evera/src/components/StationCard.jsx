import React from 'react';
import { Card, CardContent, Typography, Button } from '@mui/material';

export default function StationCard({ station, onReserve }) {
  const availableSlots = station.slots - station.slotsInUse;

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography variant="h6" gutterBottom>{station.name}</Typography>
        <Typography color="text.secondary" gutterBottom>
          Preço por kWh: {station.pricePerKwh.toFixed(2)} €
        </Typography>
        <Typography gutterBottom>
          Slots disponíveis: {availableSlots}
        </Typography>
        <Button 
          variant="contained" 
          fullWidth
          sx={{ mt: 'auto' }} 
          onClick={onReserve}
        >
          Reservar
        </Button>
      </CardContent>
    </Card>
  );
}