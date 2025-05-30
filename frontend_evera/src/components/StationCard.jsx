import React from 'react';
import { Card, CardContent, Typography, Button } from '@mui/material';

export default function StationCard({ station }) {
  return (
    <Card sx={{ marginBottom: 2 }}>
      <CardContent>
        <Typography variant="h6">{station.name}</Typography>
        <Typography color="text.secondary">
          Localização: {station.location}
        </Typography>
        <Typography>
          Slots disponíveis: {station.slotsAvailable}
        </Typography>
        <Button variant="contained" sx={{ mt: 1 }}>
          Reservar
        </Button>
      </CardContent>
    </Card>
  );
}
