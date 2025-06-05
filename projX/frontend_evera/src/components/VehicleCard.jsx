import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  IconButton,
  Box
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';

export default function VehicleCard({ vehicle, onRemove, onEdit }) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Box>
            <Typography variant="h6">{vehicle.brand} {vehicle.model}</Typography>
            <Typography variant="body2">Matrícula: {vehicle.plate}</Typography>
            <Typography variant="body2">Bateria: {vehicle.batteryCapacity} kWh</Typography>
          </Box>
          <Box>
            <IconButton onClick={() => onEdit(vehicle)} color="primary">
              <EditIcon />
            </IconButton>
            <IconButton onClick={() => onRemove(vehicle.id)} color="error">
              <DeleteIcon />
            </IconButton>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}
