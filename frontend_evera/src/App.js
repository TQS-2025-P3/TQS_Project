import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Stations from './pages/Stations';
import Profile from './pages/Profile';
import MapPage from './pages/MapPage';
import MyBookingsPage from './pages/MyBookingsPage';
import AdminBackoffice from './pages/AdminBackoffice';


export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/stations" element={<Stations />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/map" element={<MapPage />} />
        <Route path="/reservas" element={<MyBookingsPage />} />
        <Route path="/admin" element={<AdminBackoffice />} />

        

      


      </Routes>
    </BrowserRouter>
  );
}
