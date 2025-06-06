import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  Grid,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControlLabel,
  Checkbox,
  Chip,
  Paper,
  IconButton,
  Divider
} from '@mui/material';
import {
  Refresh as RefreshIcon,
  Business as BusinessIcon,
  Close as CloseIcon,
  TrendingUp as TrendingUpIcon,
  AttachMoney as AttachMoneyIcon,
  Schedule as ScheduleIcon
} from '@mui/icons-material';
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';

const StatisticsPage = () => {
  const [loading, setLoading] = useState(true);
  const [loadingStationRevenue, setLoadingStationRevenue] = useState(false);
  const [alert, setAlert] = useState({ show: false, message: '', severity: 'success' });
  
  // Estados para dados
  const [stations, setStations] = useState([]);
  const [selectedStations, setSelectedStations] = useState([]);
  const [stationUsageData, setStationUsageData] = useState([]);
  const [rushHourData, setRushHourData] = useState([]);
  const [reservationTrends, setReservationTrends] = useState([]);
  const [revenueData, setRevenueData] = useState([]);
  const [stationRevenueData, setStationRevenueData] = useState([]);
  
  // Estados para modal
  const [showStationModal, setShowStationModal] = useState(false);
  const [tempSelectedStations, setTempSelectedStations] = useState([]);
  const baseUrl = process.env.REACT_APP_API_BASE_URL;

  const API_BASE_URL = `${baseUrl}:8080/api`;

  useEffect(() => {
    fetchStations();
    fetchAllStatistics();
  }, []);

  useEffect(() => {
    if (selectedStations.length > 0) {
      console.log('Selected stations changed:', selectedStations);
      fetchStationUsage();
      fetchStationRevenueForStations(selectedStations); 
    } else {
      console.log(' No stations selected, clearing data');
      setStationUsageData([]);
      setStationRevenueData([]);
    }
  }, [selectedStations]);

  const fetchStations = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/stations`);
      if (!response.ok) throw new Error('Erro ao carregar estações');
      
      const data = await response.json();
      setStations(data);
      
    } catch (error) {
      console.error('Erro ao listar estações:', error);
      showAlert('Erro ao carregar estações', 'error');
    }
  };

  const fetchAllStatistics = async () => {
    setLoading(true);
    try {
      await Promise.all([
        fetchRushHourData(),
        fetchReservationTrends(),
        fetchRevenueData()
      ]);
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error);
      showAlert('Erro ao carregar estatísticas', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchStationUsage = async () => {
    try {
      const filteredStations = stations.filter(station => 
        selectedStations.includes(station.id)
      );
      
      const usageData = filteredStations.map(station => ({
        name: station.name,
        total: station.slots,
        occupied: station.slotsInUse || 0,
        available: station.slots - (station.slotsInUse || 0)
      }));
      
      setStationUsageData(usageData);
      
    } catch (error) {
      console.error('Erro ao calcular uso das estações:', error);
      showAlert('Erro ao calcular uso das estações', 'error');
    }
  };

  const fetchStationRevenue = async () => {
    setLoadingStationRevenue(true);
    try {
      const stationRevenuePromises = selectedStations.map(async (stationId) => {
        try {
          const response = await fetch(`${API_BASE_URL}/stations/${stationId}/monthly-revenue`);
          if (!response.ok) {
            console.warn(`Erro ao buscar dados da estação ${stationId}, usando fallback`);
            return {
              stationId,
              stationName: getStationNameById(stationId),
              data: generateStationRevenueData(stationId)
            };
          }
          
          const data = await response.json();
          return {
            stationId,
            stationName: getStationNameById(stationId),
            data: data
          };
        } catch (error) {
          console.error(`Erro ao buscar Faturação da estação ${stationId}:`, error);
          return {
            stationId,
            stationName: getStationNameById(stationId),
            data: generateStationRevenueData(stationId)
          };
        }
      });
      
      const results = await Promise.all(stationRevenuePromises);
      setStationRevenueData(results);
      
    } catch (error) {
      console.error('Erro ao aceder à Faturação por estação:', error);
      showAlert('Erro ao carregar Faturação por estação', 'error');
    } finally {
      setLoadingStationRevenue(false);
    }
  };

  const getStationNameById = (stationId) => {
    const station = stations.find(s => s.id === stationId);
    return station ? station.name : `Estação ${stationId}`;
  };

  const generateStationRevenueData = (stationId) => {
    const baseMultiplier = (stationId % 3) + 1;
    const months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun'];
    
    return months.map((month, index) => ({
      month,
      revenue: Math.round((200 + Math.random() * 300) * baseMultiplier * (1 + index * 0.1)),
      sessions: Math.round((20 + Math.random() * 40) * baseMultiplier * (1 + index * 0.05))
    }));
  };

  const fetchRushHourData = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/rush-hour-stats`);
      if (!response.ok) throw new Error('Erro ao carregar dados de rush hour');
      
      const data = await response.json();
      setRushHourData(data);
      
    } catch (error) {
      console.error('Erro ao carregar rush hour data:', error);
      setRushHourData([
        { hour: '00:00', reservations: 3 },
        { hour: '01:00', reservations: 1 },
        { hour: '02:00', reservations: 1 },
        { hour: '03:00', reservations: 2 },
        { hour: '04:00', reservations: 2 },
        { hour: '05:00', reservations: 3 },
        { hour: '06:00', reservations: 5 },
        { hour: '07:00', reservations: 15 },
        { hour: '08:00', reservations: 35 },
        { hour: '09:00', reservations: 45 },
        { hour: '10:00', reservations: 25 },
        { hour: '11:00', reservations: 20 },
        { hour: '12:00', reservations: 30 },
        { hour: '13:00', reservations: 28 },
        { hour: '14:00', reservations: 22 },
        { hour: '15:00', reservations: 18 },
        { hour: '16:00', reservations: 25 },
        { hour: '17:00', reservations: 40 },
        { hour: '18:00', reservations: 50 },
        { hour: '19:00', reservations: 35 },
        { hour: '20:00', reservations: 15 },
        { hour: '21:00', reservations: 10 },
        { hour: '22:00', reservations: 8 },
        { hour: '23:00', reservations: 3 }
      ]);
    }
  };

  const fetchReservationTrends = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/weekly-trends`);
      if (!response.ok) throw new Error('Erro ao carregar tendências');
      
      const data = await response.json();
      setReservationTrends(data);
      
    } catch (error) {
      console.error('Erro ao buscar reservation trends:', error);
      setReservationTrends([
        { date: '27/05', reservations: 120, completed: 115 },
        { date: '28/05', reservations: 135, completed: 128 },
        { date: '29/05', reservations: 98, completed: 95 },
        { date: '30/05', reservations: 145, completed: 140 },
        { date: '31/05', reservations: 167, completed: 160 },
        { date: '01/06', reservations: 189, completed: 185 },
        { date: '02/06', reservations: 156, completed: 150 }
      ]);
    }
  };

  const fetchRevenueData = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/payments/monthly-revenue`);
      if (!response.ok) throw new Error('Erro ao carregar dados de Faturação');
      
      const data = await response.json();
      setRevenueData(data);
      
    } catch (error) {
      console.error('Erro ao buscar revenue data:', error);
      setRevenueData([
        { month: 'Jan', revenue: 2400, sessions: 240 },
        { month: 'Fev', revenue: 2800, sessions: 280 },
        { month: 'Mar', revenue: 3200, sessions: 320 },
        { month: 'Abr', revenue: 2900, sessions: 290 },
        { month: 'Mai', revenue: 3500, sessions: 350 },
        { month: 'Jun', revenue: 3800, sessions: 380 }
      ]);
    }
  };

  const showAlert = (message, severity = 'success') => {
    setAlert({ show: true, message, severity });
    setTimeout(() => setAlert({ show: false, message: '', severity: 'success' }), 4000);
  };

  const openStationModal = () => {
    setTempSelectedStations([...selectedStations]);
    setShowStationModal(true);
  };

  const handleStationModalSave = () => {
    setSelectedStations([...tempSelectedStations]);
    setShowStationModal(false);
    showAlert(`${tempSelectedStations.length} estações selecionadas para análise`, 'success');
    
    if (tempSelectedStations.length > 0) {
      setTimeout(() => {
        fetchStationRevenueForStations(tempSelectedStations);
      }, 100);
    }
  };

  const fetchStationRevenueForStations = async (stationIds) => {
    if (stationIds.length === 0) {
      setStationRevenueData([]);
      return;
    }

    setLoadingStationRevenue(true);
    console.log('🔍 Fetching revenue for stations:', stationIds);
    
    try {
      const stationRevenuePromises = stationIds.map(async (stationId) => {
        try {
          const response = await fetch(`${API_BASE_URL}/stations/${stationId}/monthly-revenue`);
          
          if (!response.ok) {
            console.warn(` API error for station ${stationId}, using fallback data`);
            return {
              stationId,
              stationName: getStationNameById(stationId),
              data: generateStationRevenueData(stationId)
            };
          }
          
          const data = await response.json();
          
          
          return {
            stationId,
            stationName: getStationNameById(stationId),
            data: data
          };
        } catch (error) {
          console.error(` Error fetching revenue for station ${stationId}:`, error);
          return {
            stationId,
            stationName: getStationNameById(stationId),
            data: generateStationRevenueData(stationId)
          };
        }
      });
      
      const results = await Promise.all(stationRevenuePromises);
      setStationRevenueData(results);
      
    } catch (error) {
      showAlert('Erro ao carregar Faturação por estação', 'error');
    } finally {
      setLoadingStationRevenue(false);
    }
  };

  const toggleTempStation = (stationId) => {
    setTempSelectedStations(prev => {
      if (prev.includes(stationId)) {
        return prev.filter(id => id !== stationId);
      } else {
        return [...prev, stationId];
      }
    });
  };

  const toggleAllTempStations = () => {
    if (tempSelectedStations.length === stations.length) {
      setTempSelectedStations([]);
    } else {
      setTempSelectedStations(stations.map(station => station.id));
    }
  };

  const refreshData = () => {
    setLoading(true);
    fetchAllStatistics();
    if (selectedStations.length > 0) {
      fetchStationUsage();
      fetchStationRevenueForStations(selectedStations); 
    }
  };

  const calculateCompletionRate = () => {
    if (reservationTrends.length === 0) return 0;
    
    const totals = reservationTrends.reduce((acc, day) => ({
      reservations: acc.reservations + day.reservations,
      completed: acc.completed + day.completed
    }), { reservations: 0, completed: 0 });
    
    return totals.reservations > 0 ? ((totals.completed / totals.reservations) * 100).toFixed(1) : 0;
  };

  const findPeakHours = () => {
    if (rushHourData.length === 0) return "Não disponível";
    
    const sorted = [...rushHourData].sort((a, b) => b.reservations - a.reservations);
    const top2 = sorted.slice(0, 2);
    
    return top2.map(item => `${item.hour} (${item.reservations})`).join(' e ');
  };

  const prepareStationRevenueChartData = () => {
    

    
    const monthsMapping = [
      { frontend: 'Jan', backend: 'jan.' },
      { frontend: 'Fev', backend: 'fev.' },
      { frontend: 'Mar', backend: 'mar.' },
      { frontend: 'Abr', backend: 'abr.' },
      { frontend: 'Mai', backend: 'mai.' },
      { frontend: 'Jun', backend: 'jun.' }
    ];
    
    const chartData = monthsMapping.map(({ frontend, backend }) => {
      const monthData = { month: frontend };
      
      stationRevenueData.forEach((station, stationIndex) => {
        
        const revenueKey = `station${stationIndex}_revenue`;
        const sessionsKey = `station${stationIndex}_sessions`;
        
        const monthRecord = station.data.find(d => d.month === backend);
        if (monthRecord) {
          monthData[revenueKey] = monthRecord.revenue;
          monthData[sessionsKey] = monthRecord.sessions;
        } else {
          monthData[revenueKey] = 0;
          monthData[sessionsKey] = 0;
        }
      });
      
      return monthData;
    });
    
    return chartData;
  };

  const getSelectedStationNames = (selectedIds) => {
    return stations
      .filter(station => selectedIds.includes(station.id))
      .map(station => station.name);
  };

  const generateStationColors = () => {
    const baseColors = ['#1976d2', '#388e3c', '#f57c00', '#d32f2f', '#7b1fa2', '#0288d1', '#5d4037', '#616161'];
    return stationRevenueData.map((_, index) => baseColors[index % baseColors.length]);
  };

  if (loading) {
    return (
      <Box 
        display="flex" 
        justifyContent="center" 
        alignItems="center" 
        minHeight="80vh"
        flexDirection="column"
      >
        <CircularProgress size={60} sx={{ mb: 2 }} />
        <Typography variant="h6" color="text.secondary">
          A carregar estatísticas...
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: '1400px', mx: 'auto', p: 3 }}>
      {alert.show && (
        <Alert 
          severity={alert.severity} 
          onClose={() => setAlert({ show: false, message: '', severity: 'success' })}
          sx={{ mb: 3 }}
        >
          {alert.message}
        </Alert>
      )}

      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Box>
          <Typography variant="h3" component="h1" fontWeight="bold" gutterBottom>
            Estatísticas Principais
          </Typography>
          <Typography variant="subtitle1" color="text.secondary">
            Análise de desempenho das estações e reservas em tempo real
          </Typography>
        </Box>
        <Box display="flex" gap={2}>
          <Button
            variant="contained"
            color="secondary"
            startIcon={<BusinessIcon />}
            onClick={openStationModal}
            sx={{ minWidth: 200 }}
          >
            Selecionar Estações ({selectedStations.length})
          </Button>
          <Button
            variant="contained"
            startIcon={<RefreshIcon />}
            onClick={refreshData}
            disabled={loading}
          >
            Atualizar Dados
          </Button>
        </Box>
      </Box>
      
      {selectedStations.length > 0 && (
        <Paper sx={{ p: 3, mb: 4, bgcolor: 'secondary.50', border: '1px solid', borderColor: 'secondary.200' }}>
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Box>
              <Typography variant="h6" color="secondary.main" gutterBottom>
                Estações Selecionadas para Análise
              </Typography>
              <Box display="flex" flexWrap="wrap" gap={1} mb={1}>
                {getSelectedStationNames(selectedStations).map((name, index) => (
                  <Chip 
                    key={index}
                    label={name} 
                    color="secondary" 
                    size="small" 
                  />
                ))}
              </Box>
              <Typography variant="body2" color="text.secondary">
                Estas estações serão usadas nos gráficos de uso de slots e Faturação por estação
              </Typography>
            </Box>
            <Button
              variant="outlined"
              color="secondary"
              onClick={openStationModal}
              size="small"
            >
              Alterar
            </Button>
          </Box>
        </Paper>
      )}

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Typography variant="h5" component="h2">
              Uso das Estações por Localização
            </Typography>
            {selectedStations.length === 0 && (
              <Button
                variant="outlined"
                color="secondary"
                startIcon={<BusinessIcon />}
                onClick={openStationModal}
              >
                Selecionar Estações
              </Button>
            )}
          </Box>

          {stationUsageData.length > 0 ? (
            <Box sx={{ width: '100%', height: 400 }}>
              <ResponsiveContainer>
                <BarChart data={stationUsageData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="occupied" fill="#f44336" name="Slots Ocupados" />
                  <Bar dataKey="available" fill="#4caf50" name="Slots Disponíveis" />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          ) : (
            <Paper 
              sx={{ 
                height: 200, 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                bgcolor: 'grey.50'
              }}
            >
              <Box textAlign="center">
                <BusinessIcon sx={{ fontSize: 48, color: 'grey.400', mb: 2 }} />
                <Typography variant="body1" color="text.secondary" gutterBottom>
                Seleciona estações para ver estatísticas               </Typography>
                <Button
                  variant="contained"
                  color="secondary"
                  onClick={openStationModal}
                  size="small"
                >
                  Selecionar Estações
                </Button>
              </Box>
            </Paper>
          )}
        </CardContent>
      </Card>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h5" component="h2" gutterBottom>
            Análise de Rush Hour - Reservas por Hora
          </Typography>
          <Box sx={{ width: '100%', height: 400, mb: 2 }}>
            <ResponsiveContainer>
              <LineChart data={rushHourData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="hour" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line 
                  type="monotone" 
                  dataKey="reservations" 
                  stroke="#2196f3" 
                  strokeWidth={3}
                  name="Reservas"
                  dot={{ fill: '#2196f3', strokeWidth: 2, r: 4 }}
                  activeDot={{ r: 6, stroke: '#2196f3', strokeWidth: 2 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </Box>
          <Paper sx={{ p: 2, bgcolor: 'primary.50' }}>
            <Typography variant="body2" color="primary.main">
              <strong>Picos identificados:</strong> {findPeakHours()}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Ajustar preços ou adicionar mais estações durante estes horários para maior eficiência financeira e sustentável
            </Typography>
          </Paper>
        </CardContent>
      </Card>

      <Grid container spacing={4} sx={{ mb: 4 }}>
        <Grid item xs={12} lg={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Tendência de Reservas (Última Semana)
              </Typography>
              <Box sx={{ width: '100%', height: 300, mb: 2 }}>
                <ResponsiveContainer>
                  <LineChart data={reservationTrends}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line 
                      type="monotone" 
                      dataKey="reservations" 
                      stroke="#ff9800" 
                      name="Reservas"
                      strokeWidth={2}
                      dot={{ fill: '#ff9800', strokeWidth: 2, r: 3 }}
                    />
                    <Line 
                      type="monotone" 
                      dataKey="completed" 
                      stroke="#4caf50" 
                      name="Completadas"
                      strokeWidth={2}
                      dot={{ fill: '#4caf50', strokeWidth: 2, r: 3 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </Box>
              <Typography variant="body2" color="text.secondary">
                Taxa de conclusão média: <strong style={{ color: '#4caf50' }}>{calculateCompletionRate()}%</strong>
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} lg={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Faturação Total por Mês
              </Typography>
              <Box sx={{ width: '100%', height: 300, mb: 2 }}>
                <ResponsiveContainer>
                  <BarChart data={revenueData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar 
                      dataKey="revenue" 
                      fill="#4caf50" 
                      name="Faturação (€)"
                    />
                    <Bar 
                      dataKey="sessions" 
                      fill="#2196f3" 
                      name="Sessões"
                    />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Faturação total: <strong style={{ color: '#4caf50' }}>
                      €{revenueData.reduce((sum, item) => sum + item.revenue, 0).toFixed(2)}
                    </strong>
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Sessões totais: <strong style={{ color: '#2196f3' }}>
                      {revenueData.reduce((sum, item) => sum + item.sessions, 0)}
                    </strong>
                  </Typography>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Typography variant="h5" component="h2">
              Faturação e Sessões por Estação
            </Typography>
            {selectedStations.length === 0 && (
              <Button
                variant="outlined"
                color="secondary"
                startIcon={<BusinessIcon />}
                onClick={openStationModal}
              >
                Selecionar Estações
              </Button>
            )}
          </Box>

          {loadingStationRevenue ? (
            <Box display="flex" justifyContent="center" alignItems="center" height={200}>
              <Box textAlign="center">
                <CircularProgress sx={{ mb: 2 }} />
                <Typography variant="body2" color="text.secondary">
                  Carregando dados de Faturação para {selectedStations.length} estações...
                </Typography>
              </Box>
            </Box>
          ) : stationRevenueData.length > 0 ? (
            <Box>
              <Typography variant="caption" color="text.secondary" sx={{ mb: 2, display: 'block' }}>
                A mostrar dados de {stationRevenueData.length} estações: {stationRevenueData.map(s => s.stationName).join(', ')}
              </Typography>
              
              <Box sx={{ width: '100%', height: 400 }}>
                <ResponsiveContainer>
                  <BarChart data={prepareStationRevenueChartData()}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    {stationRevenueData.map((station, index) => {
                      const colors = generateStationColors();
                      const revenueKey = `station${index}_revenue`;
                      const sessionsKey = `station${index}_sessions`;
                      
                      
                      return (
                        <Bar 
                          key={`station-${index}`}
                          dataKey={revenueKey}
                          fill={colors[index]} 
                          name={`${station.stationName} (€)`}
                        />
                      );
                    })}
                  </BarChart>
                </ResponsiveContainer>
              </Box>
              
              
            </Box>
          ) : (
            <Paper 
              sx={{ 
                height: 200, 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                bgcolor: 'grey.50'
              }}
            >
              <Box textAlign="center">
                <AttachMoneyIcon sx={{ fontSize: 48, color: 'grey.400', mb: 2 }} />
                <Typography variant="body1" color="text.secondary" gutterBottom>
                  Selecione estações para ver análise de Faturação por estação
                </Typography>
                <Button
                  variant="contained"
                  color="secondary"
                  onClick={openStationModal}
                  size="small"
                >
                  Selecionar Estações
                </Button>
              </Box>
            </Paper>
          )}
        </CardContent>
      </Card>

      <Paper 
        sx={{ 
          p: 4, 
          background: 'linear-gradient(135deg, #1976d2 0%, #7b1fa2 100%)',
          color: 'white'
        }}
      >
        <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <TrendingUpIcon /> Resumo de Insights
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={4}>
            <Box display="flex" alignItems="center" gap={1}>
              <ScheduleIcon />
              <Typography variant="body2">
                <strong>Rush Hour:</strong> {findPeakHours()}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} md={4}>
            <Box display="flex" alignItems="center" gap={1}>
              <TrendingUpIcon />
              <Typography variant="body2">
                <strong>Eficiência:</strong> {calculateCompletionRate()}% das reservas completadas
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} md={4}>
            <Box display="flex" alignItems="center" gap={1}>
              <AttachMoneyIcon />
              <Typography variant="body2">
                <strong>Faturação:</strong> €{revenueData.reduce((sum, item) => sum + item.revenue, 0).toFixed(2)} total
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      <Dialog 
        open={showStationModal} 
        onClose={() => setShowStationModal(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="h6">Selecionar Estações para Análise</Typography>
            <IconButton onClick={() => setShowStationModal(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        
        <DialogContent>
          <Paper sx={{ p: 2, mb: 3, bgcolor: 'secondary.50' }}>
            <Typography variant="body2" color="secondary.main" sx={{ mb: 1, fontWeight: 'medium' }}>
              As estações selecionadas serão usadas em:
            </Typography>
            <Typography variant="body2" color="text.secondary" component="ul" sx={{ m: 0, pl: 2 }}>
              <li>Gráfico de Uso de Slots (ocupados vs disponíveis)</li>
              <li>Gráfico de Faturação por Estação (comparação mensal)</li>
            </Typography>
          </Paper>
          
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Button
              variant="outlined"
              onClick={toggleAllTempStations}
              size="small"
            >
              {tempSelectedStations.length === stations.length ? 'Desmarcar Todas' : 'Selecionar Todas'}
            </Button>
            <Typography variant="body2" color="text.secondary">
              {tempSelectedStations.length} de {stations.length} selecionadas
            </Typography>
          </Box>

          <Grid container spacing={2} sx={{ maxHeight: 300, overflow: 'auto' }}>
            {stations.map(station => (
              <Grid item xs={12} sm={6} key={station.id}>
                <Paper 
                  sx={{ 
                    p: 2, 
                    cursor: 'pointer',
                    border: tempSelectedStations.includes(station.id) ? 2 : 1,
                    borderColor: tempSelectedStations.includes(station.id) ? 'secondary.main' : 'grey.300',
                    '&:hover': { bgcolor: 'grey.50' }
                  }}
                  onClick={() => toggleTempStation(station.id)}
                >
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={tempSelectedStations.includes(station.id)}
                        onChange={() => toggleTempStation(station.id)}
                        color="secondary"
                      />
                    }
                    label={
                      <Box>
                        <Typography variant="subtitle2" fontWeight="medium">
                          {station.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {station.slots} slots total • {station.slotsInUse || 0} ocupados
                        </Typography>
                      </Box>
                    }
                  />
                </Paper>
              </Grid>
            ))}
          </Grid>
        </DialogContent>
        
        <DialogActions sx={{ p: 3 }}>
          <Button 
            onClick={() => setShowStationModal(false)}
            color="inherit"
          >
            Cancelar
          </Button>
          <Button 
            onClick={handleStationModalSave}
            variant="contained"
            color="secondary"
          >
            Concluir ({tempSelectedStations.length})
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default StatisticsPage;