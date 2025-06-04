import React from 'react';
import { Container, Typography } from '@mui/material';

export default function Home() {
  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Bem-vindo ao Evera</Typography>
      <Typography>Plataforma inteligente para procura e gestão de carregamentos de veículos elétricos.</Typography>
    </Container>
  );
}
