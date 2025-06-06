import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Typography, 
  Grid, 
  Card, 
  CardContent, 
  CardActions, 
  Button, 
  Box,
  Avatar,
  Paper,
  CircularProgress,
  Alert
} from '@mui/material';
import {
  DirectionsCar as CarIcon,
  EvStation as StationIcon,
  Map as MapIcon,
  Person as ProfileIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  BarChart,
  Bar
} from 'recharts';

export default function Home() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [monthlyConsumption, setMonthlyConsumption] = useState([]);
  const [vehicleCharges, setVehicleCharges] = useState([]);
  const [totalStats, setTotalStats] = useState({});
  
  const userId = 1; 

  useEffect(() => {
    fetchUserStatistics();
  }, []);

  const fetchUserStatistics = async () => {
    try {
      setLoading(true);
       const baseUrl = process.env.REACT_APP_API_BASE_URL;
      console.log('A procurar carros do utilizador', userId);
      const carsResponse = await fetch(`${baseUrl}/api/cars/user/${userId}`);
      const cars = await carsResponse.json();
      
      console.log('Buscando bookings do utilizador', userId);
      const bookingsResponse = await fetch(`${baseUrl}/api/bookings/user/${userId}`);
      const bookings = await bookingsResponse.json();
      
      processMonthlyConsumption(bookings);
      processVehicleCharges(bookings, cars);
      calculateTotalStats(bookings);
      
    } catch (err) {
      console.error('Erro ao dar fetch das estatísticas:', err);
      setError('Erro ao carregar estatísticas');
    } finally {
      setLoading(false);
    }
  };

  const processMonthlyConsumption = (bookings) => {
    const monthlyData = {};
    
    bookings.forEach(booking => {
      if (booking.status === 'COMPLETED') {
        const date = new Date(booking.time);
        const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
        const monthName = date.toLocaleDateString('pt-PT', { year: 'numeric', month: 'short' });
        
        if (!monthlyData[monthKey]) {
          monthlyData[monthKey] = {
            month: monthName,
            consumo: 0,
            custo: 0,
            carregamentos: 0
          };
        }
        
        const pricePerKwh = booking.chargerStation.pricePerKwh;
        const exactKwh = booking.cost / pricePerKwh;
        
        monthlyData[monthKey].consumo += exactKwh;
        monthlyData[monthKey].custo += booking.cost;
        monthlyData[monthKey].carregamentos += 1;
      }
    });
    
    const sortedData = Object.keys(monthlyData)
      .sort()
      .map(key => ({
        ...monthlyData[key],
        consumo: Math.round(monthlyData[key].consumo * 10) / 10,
        custo: Math.round(monthlyData[key].custo * 100) / 100
      }));
    
    setMonthlyConsumption(sortedData);
  };

  const processVehicleCharges = (bookings, cars) => {
    const vehicleData = {};
    
    cars.forEach(car => {
      vehicleData[car.id] = {
        carName: `${car.brand} ${car.model}`,
        plate: car.plate,
        Jan: 0, Fev: 0, Mar: 0, Abr: 0, Mai: 0, Jun: 0
      };
    });
    
    bookings.forEach(booking => {
      if (booking.status === 'COMPLETED' && booking.car && vehicleData[booking.car.id]) {
        const date = new Date(booking.time);
        const year = date.getFullYear();
        const month = date.getMonth(); // 0-11
        
        if (year === 2025) {
          const monthNames = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 
                             'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
          const monthKey = monthNames[month];
          
          if (month <= 5 && vehicleData[booking.car.id][monthKey] !== undefined) {
            vehicleData[booking.car.id][monthKey] += 1;
          }
        }
      }
    });
    
    const chartData = Object.values(vehicleData).filter(car => {
      const totalCharges = car.Jan + car.Fev + car.Mar + car.Abr + car.Mai + car.Jun;
      return totalCharges > 0;
    });
    
    setVehicleCharges(chartData);
  };

  const calculateTotalStats = (bookings) => {
    const completedBookings = bookings.filter(b => b.status === 'COMPLETED');
    
    const totalCost = completedBookings.reduce((sum, b) => sum + b.cost, 0);
    const totalTime = completedBookings.reduce((sum, b) => sum + b.duration, 0);
    
    const totalKwh = completedBookings.reduce((sum, booking) => {
      const pricePerKwh = booking.chargerStation.pricePerKwh;
      const exactKwh = booking.cost / pricePerKwh;
      return sum + exactKwh;
    }, 0);
    
    setTotalStats({
      totalCharges: completedBookings.length,
      totalCost: Math.round(totalCost * 100) / 100,
      totalKwh: Math.round(totalKwh * 10) / 10,
      totalTime: Math.round(totalTime / 60 * 10) / 10, 
    });
  };

  const cards = [
    {
      title: 'Meus Veículos',
      description: 'Consulte ou adicione os seus veículos elétricos',
      icon: <CarIcon sx={{ fontSize: 40 }} />,
      color: '#1976d2',
      route: '/profile',
      buttonText: 'Ver Perfil'
    },
    {
      title: 'Estações de Carregamento',
      description: 'Consulte todas as estações disponíveis',
      icon: <StationIcon sx={{ fontSize: 40 }} />,
      color: '#2e7d32',
      route: '/stations',
      buttonText: 'Ver Estações'
    },
    {
      title: 'Mapa Interativo',
      description: 'Visualize as estações no mapa',
      icon: <MapIcon sx={{ fontSize: 40 }} />,
      color: '#ed6c02',
      route: '/map',
      buttonText: 'Abrir Mapa'
    }
  ];

  const handleCardClick = (route) => {
    navigate(route);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box textAlign="center" mb={6}>
        <Typography variant="h3" component="h1" gutterBottom color="primary">
          Bem-vindo ao Evera
        </Typography>
        <Typography variant="h6" color="text.secondary" sx={{ maxWidth: 600, mx: 'auto' }}>
          Plataforma inteligente para procura e gestão de carregamentos de veículos elétricos
        </Typography>
      </Box>

      <Grid container spacing={4} justifyContent="center">
        {cards.map((card, index) => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={index}>
            <Card 
              sx={{ 
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: 'all 0.3s ease-in-out',
                cursor: 'pointer',
                '&:hover': {
                  transform: 'translateY(-8px)',
                  boxShadow: 6
                }
              }}
              onClick={() => handleCardClick(card.route)}
            >
              <CardContent sx={{ flexGrow: 1, textAlign: 'center', pt: 4 }}>
                <Avatar
                  sx={{
                    bgcolor: card.color,
                    width: 80,
                    height: 80,
                    mx: 'auto',
                    mb: 2
                  }}
                >
                  {card.icon}
                </Avatar>

                <Typography variant="h5" component="h2" gutterBottom color="primary">
                  {card.title}
                </Typography>

                <Typography variant="body1" color="text.secondary">
                  {card.description}
                </Typography>
              </CardContent>

              <CardActions sx={{ justifyContent: 'center', pb: 3 }}>
                <Button 
                  variant="contained" 
                  size="large"
                  sx={{ 
                    bgcolor: card.color,
                    '&:hover': {
                      bgcolor: card.color,
                      filter: 'brightness(0.9)'
                    },
                    minWidth: 140
                  }}
                >
                  {card.buttonText}
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {!loading && !error && (
        <Box mt={8} textAlign="center">
          <Typography variant="h5" gutterBottom color="primary">
            Estatísticas Pessoais
          </Typography>
          <Grid container spacing={4} sx={{ mt: 2 }}>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Typography variant="h3" color="primary">{totalStats.totalCharges}</Typography>
              <Typography variant="body1" color="text.secondary">Carregamentos</Typography>
            </Grid>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Typography variant="h3" color="success.main">{totalStats.totalKwh} kWh</Typography>
              <Typography variant="body1" color="text.secondary">Energia Total</Typography>
            </Grid>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Typography variant="h3" color="warning.main">€{totalStats.totalCost}</Typography>
              <Typography variant="body1" color="text.secondary">Custo Total</Typography>
            </Grid>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Typography variant="h3" color="info.main">{totalStats.totalTime}h</Typography>
              <Typography variant="body1" color="text.secondary">Tempo Total</Typography>
            </Grid>
          </Grid>
        </Box>
      )}

      {!loading && !error && monthlyConsumption.length > 0 && (
        <Box mt={6}>
          <Typography variant="h5" gutterBottom color="primary" textAlign="center">
            Gráficos de Consumo
          </Typography>
          
          <Grid container spacing={4} sx={{ mt: 2 }}>
            <Grid size={{ xs: 12, lg: 4 }}>
              <Paper sx={{ p: 3 }}>
                <Typography variant="h6" gutterBottom>
                  💡 Consumo Mensal (kWh & €)
                </Typography>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={monthlyConsumption}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip 
                      formatter={(value, name, props) => [
                        props.dataKey === 'consumo' ? `${value} kWh` : `€${value}`,
                        props.dataKey === 'consumo' ? 'Energia' : 'Custo'
                      ]}
                    />
                    <Legend />
                    <Line 
                      type="monotone" 
                      dataKey="consumo" 
                      stroke="#1976d2" 
                      strokeWidth={3}
                      name="Energia (kWh)"
                    />
                    <Line 
                      type="monotone" 
                      dataKey="custo" 
                      stroke="#2e7d32" 
                      strokeWidth={3}
                      name="Custo (€)"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>

            <Grid size={{ xs: 12, lg: 4 }}>
              <Paper sx={{ p: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Carregamentos por Mês
                </Typography>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={monthlyConsumption}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis 
                      dataKey="month" 
                      angle={-45}
                      textAnchor="end"
                      height={60}
                    />
                    <YAxis />
                    <Tooltip formatter={(value) => [`${value} carregamentos`, 'Total']} />
                    <Bar dataKey="carregamentos" fill="#ed6c02" />
                  </BarChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>

            {vehicleCharges.length > 0 && (
              <Grid size={{ xs: 12, lg: 4 }}>
                <Paper sx={{ p: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Carregamentos por Veículo
                  </Typography>
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={vehicleCharges}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis 
                        dataKey="carName" 
                        angle={-45}
                        textAnchor="end"
                        height={60}
                      />
                      <YAxis />
                      <Tooltip />
                      <Legend 
                        wrapperStyle={{ fontSize: '12px' }}
                      />
                      <Bar dataKey="Jan" stackId="a" fill="#8884d8" />
                      <Bar dataKey="Fev" stackId="a" fill="#82ca9d" />
                      <Bar dataKey="Mar" stackId="a" fill="#ffc658" />
                      <Bar dataKey="Abr" stackId="a" fill="#ff7300" />
                      <Bar dataKey="Mai" stackId="a" fill="#00ff00" />
                      <Bar dataKey="Jun" stackId="a" fill="#ff6b6b" />
                    </BarChart>
                  </ResponsiveContainer>
                </Paper>
              </Grid>
            )}
          </Grid>
        </Box>
      )}

      {loading && (
        <Box mt={6} textAlign="center">
          <CircularProgress />
          <Typography sx={{ mt: 2 }}>A Carregar estatísticas...</Typography>
        </Box>
      )}

      {error && (
        <Box mt={6}>
          <Alert severity="error">{error}</Alert>
        </Box>
      )}
    </Container>
  );
}