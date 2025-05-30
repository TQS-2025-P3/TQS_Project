import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Container, Typography } from '@mui/material';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

// Corrigir ícones do Leaflet (problema comum com Webpack)
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

// Mock de estações
const mockStations = [
  { id: 1, name: "Estação Central", lat: 38.7169, lng: -9.1399, slots: 3 },
  { id: 2, name: "Eco Park", lat: 41.1496, lng: -8.6109, slots: 2 },
  { id: 3, name: "Carregamento Sul", lat: 37.0194, lng: -7.9304, slots: 4 },
];

export default function MapPage() {
  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Mapa de Estações
      </Typography>
      <Typography variant="body1" gutterBottom>
        Clique numa estação para ver mais detalhes.
      </Typography>

      <div style={{ height: '70vh', marginTop: '2rem', borderRadius: '8px', overflow: 'hidden' }}>
        <MapContainer center={[39.5, -8]} zoom={6} style={{ height: '100%', width: '100%' }}>
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {mockStations.map((station) => (
            <Marker key={station.id} position={[station.lat, station.lng]}>
              <Popup>
                <strong>{station.name}</strong><br />
                Slots disponíveis: {station.slots}<br />
                {/* Botão de reserva futuro */}
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </Container>
  );
}
