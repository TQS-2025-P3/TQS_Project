import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link } from 'react-router-dom';

export default function Navbar() {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          Evera
        </Typography>
        <Button color="inherit" component={Link} to="/">Início</Button>
        <Button color="inherit" component={Link} to="/stations">Estações</Button>
        <Button color="inherit" component={Link} to="/profile">Perfil</Button>


      </Toolbar>
    </AppBar>
  );
}
