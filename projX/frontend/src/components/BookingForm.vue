<template>
  <div>
    <label>Choose Time:</label>
    <select v-model="timeSlot">
      <option v-for="slot in station.timeSlots" :value="slot" :key="slot">{{ slot }}</option>
    </select>

    <div v-if="timeSlot">
      <PaymentForm />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PaymentForm from './PaymentForm.vue'

const props = defineProps(['station'])
const timeSlot = ref(null)
</script>
