import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Tabs,
  Tab,
  Box,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Alert,
  Chip,
  Card,
  CardContent,
  Grid
} from '@mui/material';

import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon
} from '@mui/icons-material';
import StatisticsPage from './StatisticsPage';

const AdminBackoffice = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [password, setPassword] = useState('');
  const [tabValue, setTabValue] = useState(0);

  const handleLogin = () => {
    if (password === 'admin123') {
      setIsAuthenticated(true);
    } else {
      alert('Password incorreta!');
    }
  };

  if (!isAuthenticated) {
    return (
      <Container maxWidth="sm" sx={{ mt: 8 }}>
        <Paper sx={{ p: 4 }}>
          <Typography variant="h4" align="center" gutterBottom>
            Admin Login
          </Typography>
          <TextField
            fullWidth
            type="password"
            label="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleLogin()}
            sx={{ mb: 3 }}
          />
          <Button
            fullWidth
            variant="contained"
            onClick={handleLogin}
            size="large"
          >
            Entrar
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Typography variant="h3" gutterBottom>
        Backoffice Administrador
      </Typography>
      
      <Paper sx={{ width: '100%', mb: 2 }}>
        <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
          <Tab label="Estações de Carregamento" />
          <Tab label="Utilizadores" />
          <Tab label="Estatisticas" />
        </Tabs>
        
        <Box sx={{ p: 3 }}>
          {tabValue === 0 && <StationsManagement />}
          {tabValue === 1 && <UsersManagement />}
          {tabValue === 2 && <StatisticsPage />}
        </Box>
      </Paper>
    </Container>
  );
};

const StationsManagement = () => {
  const [stations, setStations] = useState([]);
  const [open, setOpen] = useState(false);
  const [editingStation, setEditingStation] = useState(null);
  const [alert, setAlert] = useState({ show: false, message: '', severity: 'success' });

  const [formData, setFormData] = useState({
    name: '',
    latitude: '',
    longitude: '',
    slots: '',
    pricePerKwh: ''
  });

  useEffect(() => {
    fetchStations();
  }, []);

 const baseUrl = process.env.REACT_APP_API_BASE_URL;
      
  const fetchStations = async () => {
    try {
      const response = await fetch(`${baseUrl}/api/stations`);
      const data = await response.json();
      setStations(data);
    } catch (error) {
      showAlert('Erro ao carregar estações', 'error');
    }
  };

  const showAlert = (message, severity = 'success') => {
    setAlert({ show: true, message, severity });
    setTimeout(() => setAlert({ show: false, message: '', severity: 'success' }), 3000);
  };

  const handleSubmit = async () => {
    try {
      const requestBody = {
        name: formData.name,
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude),
        slots: parseInt(formData.slots),
        pricePerKwh: parseFloat(formData.pricePerKwh),
        staffId: 1
      };

      console.log('Dados enviados para estação:', requestBody);

      const url = editingStation 
        ? `${baseUrl}/api/stations/${editingStation.id}`
        : `${baseUrl}/api/stations`;
      
      const method = editingStation ? 'PUT' : 'POST';
      
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Erro do servidor:', errorText);
        showAlert(`Erro: ${errorText}`, 'error');
        return;
      }

      showAlert(editingStation ? 'Estação atualizada!' : 'Estação criada!');
      setOpen(false);
      resetForm();
      fetchStations();
    } catch (error) {
      console.error('Erro:', error);
      showAlert('Erro ao salvar estação', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja eliminar esta estação?')) {
      try {
        const response = await fetch(`${baseUrl}/api/stations/${id}`, {
          method: 'DELETE'
        });
        
        if (response.ok) {
          showAlert('Estação eliminada!');
          fetchStations();
        }
      } catch (error) {
        showAlert('Erro ao eliminar estação', 'error');
      }
    }
  };

  const handleEdit = (station) => {
    setEditingStation(station);
    setFormData({
      name: station.name,
      latitude: station.latitude.toString(),
      longitude: station.longitude.toString(),
      slots: station.slots.toString(),
      pricePerKwh: station.pricePerKwh.toString()
    });
    setOpen(true);
  };

  const resetForm = () => {
    setFormData({
      name: '',
      latitude: '',
      longitude: '',
      slots: '',
      pricePerKwh: ''
    });
    setEditingStation(null);
  };

  const handleClose = () => {
    setOpen(false);
    resetForm();
  };

  return (
    <>
      {alert.show && (
        <Alert severity={alert.severity} sx={{ mb: 2 }}>
          {alert.message}
        </Alert>
      )}

      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5">Estações de Carregamento</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpen(true)}
        >
          Adicionar Estação
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>Localização</TableCell>
              <TableCell>Slots</TableCell>
              <TableCell>Disponível</TableCell>
              <TableCell>Preço/kWh</TableCell>
              <TableCell>Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {stations.map((station) => (
              <TableRow key={station.id}>
                <TableCell>{station.name}</TableCell>
                <TableCell>
                  {station.latitude.toFixed(4)}, {station.longitude.toFixed(4)}
                </TableCell>
                <TableCell>{station.slots}</TableCell>
                <TableCell>
                <Chip 
                label={`${station.slots - station.slotsInUse}/${station.slots}`}
                color={station.slots - station.slotsInUse > 0 ? 'success' : 'error'}
                onClick={() => {}}
                />
                </TableCell>
                <TableCell>€{station.pricePerKwh}</TableCell>
                <TableCell>
                  <IconButton onClick={() => handleEdit(station)} color="primary">
                    <EditIcon />
                  </IconButton>
                  <IconButton onClick={() => handleDelete(station.id)} color="error">
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>
          {editingStation ? 'Editar Estação' : 'Adicionar Estação'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nome"
                value={formData.name}
                onChange={(e) => setFormData({...formData, name: e.target.value})}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Latitude"
                type="number"
                inputProps={{ step: 'any' }}
                value={formData.latitude}
                onChange={(e) => setFormData({...formData, latitude: e.target.value})}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Longitude"
                type="number"
                inputProps={{ step: 'any' }}
                value={formData.longitude}
                onChange={(e) => setFormData({...formData, longitude: e.target.value})}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Total Slots"
                type="number"
                value={formData.slots}
                onChange={(e) => setFormData({...formData, slots: e.target.value})}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Preço por kWh (€)"
                type="number"
                inputProps={{ step: 0.01 }}
                value={formData.pricePerKwh}
                onChange={(e) => setFormData({...formData, pricePerKwh: e.target.value})}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingStation ? 'Atualizar' : 'Criar'}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

const UsersManagement = () => {
  const [users, setUsers] = useState([]);
  const [openEdit, setOpenEdit] = useState(false);
  const [openView, setOpenView] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [viewingUser, setViewingUser] = useState(null);
  const [alert, setAlert] = useState({ show: false, message: '', severity: 'success' });

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    balance: '',
    password: ''
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const baseUrl = process.env.REACT_APP_API_BASE_URL;
      const response = await fetch(`${baseUrl}/api/users`);
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      showAlert('Erro ao carregar utilizadores', 'error');
    }
  };

  const showAlert = (message, severity = 'success') => {
    setAlert({ show: true, message, severity });
    setTimeout(() => setAlert({ show: false, message: '', severity: 'success' }), 3000);
  };

  const handleSubmit = async () => {
    try {
      const requestBody = {
        name: formData.name,
        email: formData.email,
        balance: parseFloat(formData.balance),
        password: formData.password || 'manterpwd123' // Password temporária se não fornecida
      };

      console.log('Dados enviados para user:', requestBody);
      const baseUrl = process.env.REACT_APP_API_BASE_URL;

      const response = await fetch(`${baseUrl}/api/users/${editingUser.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Erro do servidor:', errorText);
        showAlert(`Erro: ${errorText}`, 'error');
        return;
      }

      showAlert('Utilizador atualizado!');
      setOpenEdit(false);
      resetForm();
      fetchUsers();
    } catch (error) {
      console.error('Erro:', error);
      showAlert('Erro ao atualizar utilizador', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja eliminar este utilizador?')) {
      try {
        const baseUrl = process.env.REACT_APP_API_BASE_URL;

        const response = await fetch(`${baseUrl}/api/users/${id}`, {
          method: 'DELETE'
        });
        
        if (response.ok) {
          showAlert('Utilizador eliminado!');
          fetchUsers();
        }
      } catch (error) {
        showAlert('Erro ao eliminar utilizador', 'error');
      }
    }
  };

  const handleEdit = (user) => {
    setEditingUser(user);
    setFormData({
      name: user.name,
      email: user.email,
      balance: user.balance.toString(),
      password: ''
    });
    setOpenEdit(true);
  };

  const resetForm = () => {
    setFormData({ 
      name: '', 
      email: '', 
      balance: '', 
      password: '' 
    });
    setEditingUser(null);
  };

  return (
    <>
      {alert.show && (
        <Alert severity={alert.severity} sx={{ mb: 2 }}>
          {alert.message}
        </Alert>
      )}

      <Typography variant="h5" gutterBottom>
        Utilizadores Registados
      </Typography>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Nome</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Saldo</TableCell>
              <TableCell>Carros</TableCell>
              <TableCell>Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.name}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>€{user.balance.toFixed(2)}</TableCell>
                <TableCell>
                <Chip 
                label={user.cars ? user.cars.length : 0} color="primary" onClick={() => {}} />                
                </TableCell>
                <TableCell>
                  <IconButton onClick={() => { setViewingUser(user); setOpenView(true); }} color="info">
                    <ViewIcon />
                  </IconButton>
                  <IconButton onClick={() => handleEdit(user)} color="primary">
                    <EditIcon />
                  </IconButton>
                  <IconButton onClick={() => handleDelete(user.id)} color="error">
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openEdit} onClose={() => setOpenEdit(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Editar Utilizador</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nome"
                value={formData.name}
                onChange={(e) => setFormData({...formData, name: e.target.value})}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Saldo (€)"
                type="number"
                inputProps={{ step: 1 }}
                value={formData.balance}
                onChange={(e) => setFormData({...formData, balance: e.target.value})}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nova Password (deixar vazio para manter atual)"
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenEdit(false)}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained">
            Atualizar
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openView} onClose={() => setOpenView(false)} maxWidth="md" fullWidth>
        <DialogTitle>Detalhes do Utilizador</DialogTitle>
        <DialogContent>
          {viewingUser && (
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <Card>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>Informações Pessoais</Typography>
                    <Typography><strong>Nome:</strong> {viewingUser.name}</Typography>
                    <Typography><strong>Email:</strong> {viewingUser.email}</Typography>
                    <Typography><strong>Saldo:</strong> €{viewingUser.balance.toFixed(2)}</Typography>
                  </CardContent>
                </Card>
              </Grid>
              {viewingUser.cars && viewingUser.cars.length > 0 && (
                <Grid item xs={12}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6" gutterBottom>Carros Registados</Typography>
                      {viewingUser.cars.map((car, index) => (
                        <Paper key={index} sx={{ p: 2, mt: 1 }}>
                          <Typography><strong>Modelo:</strong> {car.model}</Typography>
                        </Paper>
                      ))}
                    </CardContent>
                  </Card>
                </Grid>
              )}
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenView(false)}>Fechar</Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default AdminBackoffice;