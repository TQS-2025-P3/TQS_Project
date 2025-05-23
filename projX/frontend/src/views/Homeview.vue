<template>
  <div>
    <h1>🔌 Available Charging Stations</h1>
    <StationCard 
      v-for="station in stations" 
      :key="station.id" 
      :station="station" 
    />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useStationStore } from '../store/stationStore'
import StationCard from '../components/StationCard.vue'

const stationStore = useStationStore()

onMounted(() => {
  stationStore.fetchStations()
})

const stations = stationStore.stations
</script>
