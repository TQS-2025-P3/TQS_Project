import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Container, Typography, CircularProgress } from '@mui/material';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

export default function MapPage() {
  const [stations, setStations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/api/stations")
      .then(res => res.json())
      .then(data => {
        setStations(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Erro ao buscar estações:", err);
        setLoading(false);
      });
  }, []);

  const handleReserve = (station) => {
    alert(`Estação selecionada: ${station.name}`);
    _//modal p reservar com detalhes
  };

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Mapa de Estações
      </Typography>
      <Typography variant="body1" gutterBottom>
        Clique numa estação para ver mais detalhes.
      </Typography>

      {loading ? (
        <CircularProgress />
      ) : (
        <div style={{ height: '70vh', marginTop: '2rem', borderRadius: '8px', overflow: 'hidden' }}>
          <MapContainer center={[39.5, -8]} zoom={6} style={{ height: '100%', width: '100%' }}>
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {stations.map((station) => (
              <Marker key={station.id} position={[station.latitude, station.longitude]}>
                <Popup>
                  <strong>{station.name}</strong><br />
                  Slots disponíveis: {station.slots}<br />
                  <button
                    style={{
                      marginTop: '8px',
                      padding: '6px 12px',
                      backgroundColor: '#1976d2',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer'
                    }}
                    onClick={() => handleReserve(station)}
                  >
                    Reservar
                  </button>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </div>
      )}
    </Container>
  );
}
