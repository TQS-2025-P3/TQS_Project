<template>
    <form @submit.prevent="submitBooking">
      <label>Choose Time:</label>
      <select v-model="timeSlot">
        <option v-for="slot in station.timeSlots" :value="slot" :key="slot">{{ slot }}</option>
      </select>
  
      <router-link to="/booking-success">
        <button type="submit">Proceed to Payment</button>
      </router-link>
    </form>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const props = defineProps(['station'])
  const timeSlot = ref(null)
  
  const submitBooking = () => {
    console.log(`Booked ${props.station.name} at ${timeSlot.value}`)
  }
  </script>
  