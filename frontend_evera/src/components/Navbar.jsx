import React, { useEffect, useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { Link } from 'react-router-dom';

export default function Navbar() {
  const userId = 1; // por agora estático
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchUser = () => {
      fetch(`http://localhost:8080/api/users/${userId}`)
        .then(res => res.json())
        .then(data => setUser(data))
        .catch(err => console.error("Erro ao buscar dados do utilizador:", err));
    };

    fetchUser();

    window.addEventListener("userUpdated", fetchUser);

    return () => {
      window.removeEventListener("userUpdated", fetchUser);
    };
  }, []);

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          Evera
        </Typography>
        <Button color="inherit" component={Link} to="/">Início</Button>
        <Button color="inherit" component={Link} to="/stations">Estações</Button>
        <Button color="inherit" component={Link} to="/profile">Perfil</Button>
        <Button color="inherit" component={Link} to="/map">Mapa</Button>
        <Button color="inherit" component={Link} to="/reservas">Minhas Reservas</Button>

        {user && (
          <Box sx={{ ml: 2 }}>
            <Typography variant="body2">
              {user.name} | Saldo: {user.balance.toFixed(2)} €
            </Typography>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
}
