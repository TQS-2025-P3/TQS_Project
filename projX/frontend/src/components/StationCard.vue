<template>
    <div class="card">
      <h2>{{ station.name }}</h2>
      <p>Status: <strong :class="{ 'text-green-600': station.available }">{{ station.available ? 'Available' : 'Occupied' }}</strong></p>
      <router-link :to="`/station/${station.id}`">
        <button :disabled="!station.available">Book Now</button>
      </router-link>
    </div>
  </template>
  
  <script setup>
  defineProps(['station'])
  </script>
  