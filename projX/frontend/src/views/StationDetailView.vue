<template>
    <div>
      <h1>Booking: {{ station?.name }}</h1>
      <BookingForm :station="station" />
    </div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { useStationStore } from '../store/stationStore'
  import BookingForm from '../components/BookingForm.vue'
  
  const store = useStationStore()
  const route = useRoute()
  
  onMounted(() => {
    store.fetchStation(route.params.id)
  })
  
  const station = store.selectedStation
  </script>
  