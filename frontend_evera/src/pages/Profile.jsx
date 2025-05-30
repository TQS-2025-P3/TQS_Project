import React, { useEffect, useState } from 'react';
import {
  Container, Typography, CircularProgress, TextField, Button, Box, Snackbar,
  Alert, Dialog, DialogTitle, DialogContent, DialogActions, Grid, IconButton
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import VehicleCard from '../components/VehicleCard';
import { fetchCarsByUserId, addCar, deleteCar, updateCar } from '../services/api';

export default function Profile() {
  const userId = 1;
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [newCar, setNewCar] = useState({ brand: '', model: '', plate: '', batteryCapacity: '' });
  const [editingCar, setEditingCar] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [carToDelete, setCarToDelete] = useState(null);

  useEffect(() => { loadCars(); }, []);

  const loadCars = () => {
    setLoading(true);
    fetchCarsByUserId(userId)
      .then(setVehicles)
      .catch(() => showSnackbar("Erro ao carregar veículos", "error"))
      .finally(() => setLoading(false));
  };

  const showSnackbar = (message, severity) => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleInputChange = (e) => {
    setNewCar({ ...newCar, [e.target.name]: e.target.value });
  };

  const handleAddCar = async () => {
    const { brand, model, plate, batteryCapacity } = newCar;
    if (!brand || !model || !plate || batteryCapacity === '') {
      showSnackbar("Preenche todos os campos", "warning");
      return;
    }
    if (parseFloat(batteryCapacity) <= 0) {
      showSnackbar("Capacidade deve ser maior que zero", "warning");
      return;
    }

    try {
      const carToSend = { ...newCar, userId, batteryCapacity: parseFloat(batteryCapacity) };
      await addCar(carToSend);
      setNewCar({ brand: '', model: '', plate: '', batteryCapacity: '' });
      loadCars();
      showSnackbar("Carro adicionado com sucesso!", "success");
    } catch (err) {
      console.error(err);
      showSnackbar("Erro ao adicionar carro", "error");
    }
  };

  const handleRemoveCar = (carId) => {
    setCarToDelete(carId);
    setConfirmOpen(true);
  };

  const confirmRemoveCar = async () => {
    try {
      await deleteCar(carToDelete);
      loadCars();
      showSnackbar("Carro removido com sucesso", "success");
    } catch (err) {
      console.error(err);
      showSnackbar("Erro ao remover carro", "error");
    } finally {
      setConfirmOpen(false);
      setCarToDelete(null);
    }
  };

  const handleEditCar = (car) => {
    setEditingCar(car);
    setNewCar({ brand: car.brand, model: car.model, plate: car.plate, batteryCapacity: car.batteryCapacity });
  };

  const handleSaveEdit = async () => {
    try {
      const updated = { ...newCar, userId, batteryCapacity: parseFloat(newCar.batteryCapacity) };
      await updateCar(editingCar.id, updated);
      showSnackbar("Carro atualizado com sucesso!", "success");
      setEditingCar(null);
      setNewCar({ brand: '', model: '', plate: '', batteryCapacity: '' });
      loadCars();
    } catch (err) {
      console.error(err);
      showSnackbar("Erro ao atualizar carro", "error");
    }
  };

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Perfil do Utilizador</Typography>

      <Typography variant="h6" sx={{ mt: 3 }}>Seus Veículos</Typography>
      {loading ? (
        <CircularProgress />
      ) : vehicles.length > 0 ? (
        <Grid container spacing={2}>
          {vehicles.map(vehicle => (
            <Grid item xs={12} sm={6} md={4} key={vehicle.id}>
              <Box border={1} borderRadius={2} p={2} position="relative">
                <Typography variant="subtitle1"><strong>{vehicle.brand} {vehicle.model}</strong></Typography>
                <Typography variant="body2">Matrícula: {vehicle.plate}</Typography>
                <Typography variant="body2">Bateria: {vehicle.batteryCapacity} kWh</Typography>
                <Box display="flex" justifyContent="flex-end" gap={1} mt={1}>
                <IconButton onClick={() => handleEditCar(vehicle)} size="small" color="primary">
                    <Edit fontSize="small" />
                </IconButton>
                <IconButton onClick={() => handleRemoveCar(vehicle.id)} size="small" color="error">
                    <Delete fontSize="small" />
                </IconButton>
                </Box>
              </Box>
            </Grid>
          ))}
        </Grid>
      ) : (
        <Typography>Nenhum veículo registado.</Typography>
      )}

      <Box sx={{ mt: 4 }}>
        <Typography variant="h6" gutterBottom>
          {editingCar ? "Editar Veículo" : "Adicionar Novo Veículo"}
        </Typography>
        <TextField name="brand" label="Marca" value={newCar.brand} onChange={handleInputChange} fullWidth margin="normal" />
        <TextField name="model" label="Modelo" value={newCar.model} onChange={handleInputChange} fullWidth margin="normal" />
        <TextField name="plate" label="Matrícula" value={newCar.plate} onChange={handleInputChange} fullWidth margin="normal" />
        <TextField name="batteryCapacity" label="Capacidade da Bateria (kWh)" type="number" value={newCar.batteryCapacity} onChange={handleInputChange} fullWidth margin="normal" />

        {editingCar ? (
          <>
            <Button variant="contained" onClick={handleSaveEdit} sx={{ mt: 2, mr: 2 }}>Guardar Alterações</Button>
            <Button variant="outlined" color="secondary" onClick={() => {
              setEditingCar(null);
              setNewCar({ brand: '', model: '', plate: '', batteryCapacity: '' });
            }}>Cancelar</Button>
          </>
        ) : (
          <Button variant="contained" onClick={handleAddCar} sx={{ mt: 2 }}>Adicionar Carro</Button>
        )}
      </Box>

      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirmar remoção</DialogTitle>
        <DialogContent>
          <Typography>Tem a certeza que deseja remover este veículo?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>Cancelar</Button>
          <Button onClick={confirmRemoveCar} color="error">Remover</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snackbar.open} autoHideDuration={3000} onClose={handleCloseSnackbar}>
        <Alert severity={snackbar.severity} onClose={handleCloseSnackbar} variant="filled">
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}
