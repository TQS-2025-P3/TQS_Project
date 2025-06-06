import React, { useEffect, useState } from 'react';
import {
  Container, Typography, Divider, Card, CardContent, Button, Stack
} from '@mui/material';

export default function MyBookingsPage() {
  const userId = 1; // estatico para user com id igual a 1
  const [bookings, setBookings] = useState([]);
  const baseUrl = process.env.REACT_APP_API_BASE_URL;

  const fetchBookings = async () => {
    try {
      const res = await fetch(`${baseUrl}/api/bookings/user/${userId}`);
      const data = await res.json();
      setBookings(data);
    } catch (err) {
      console.error("Erro ao buscar reservas:", err);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleStatusUpdate = async (id, newStatus) => {
    try {
      await fetch(`${baseUrl}/bookings/${id}/status?status=${newStatus}`, {
        method: 'PATCH'
      });
      fetchBookings(); 
    } catch (err) {
      console.error("Erro ao atualizar status:", err);
    }
  };

  const activeBookings = bookings.filter(b => b.status === "RESERVED");
  const pastBookings = bookings.filter(b => b.status === "COMPLETED" || b.status === "CANCELLED");

  const renderBooking = (booking) => (
    <Card key={booking.id} sx={{ mb: 2 }}>
      <CardContent>
        <Typography variant="h6">{booking.chargerStation.name}</Typography>
        <Typography>
          Carro: {booking.car.brand} {booking.car.model} ({booking.car.plate})
        </Typography>
        <Typography>Início: {new Date(booking.time).toLocaleString()}</Typography>
        <Typography>Duração: {booking.duration} min</Typography>
        <Typography>Custo: {booking.cost?.toFixed(2)} €</Typography>
        <Typography>Status: <strong>{booking.status}</strong></Typography>
        {booking.status === "RESERVED" && (
          <Stack direction="row" spacing={1} mt={2}>
            <Button onClick={() => handleStatusUpdate(booking.id, "COMPLETED")} variant="contained" color="success">
              Marcar como Concluída
            </Button>
            <Button onClick={() => handleStatusUpdate(booking.id, "CANCELLED")} variant="outlined" color="error">
              Cancelar
            </Button>
          </Stack>
        )}
      </CardContent>
    </Card>
  );
  

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Minhas Reservas</Typography>

      <Typography variant="h6" gutterBottom>Reservas Ativas</Typography>
      {activeBookings.length === 0 ? (
        <Typography>Nenhuma reserva ativa.</Typography>
      ) : (
        activeBookings.map(renderBooking)
      )}

      <Divider sx={{ my: 4 }} />

      <Typography variant="h6" gutterBottom>Histórico de Reservas</Typography>
      {pastBookings.length === 0 ? (
        <Typography>Sem histórico ainda.</Typography>
      ) : (
        pastBookings.map(renderBooking)
      )}
    </Container>
  );
}
