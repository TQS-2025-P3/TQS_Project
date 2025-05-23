<template>
    <form @submit.prevent="simulatePayment" class="payment-form">
      <h2>Enter Payment Details</h2>
  
      <div>
        <label>Full Name</label>
        <input v-model="name" required />
      </div>
  
      <div>
        <label>Email</label>
        <input v-model="email" type="email" required />
      </div>
  
      <div>
        <label>Card Number</label>
        <input v-model="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" required />
      </div>
  
      <div>
        <label>Expiry</label>
        <input v-model="expiry" placeholder="MM/YY" maxlength="5" required />
      </div>
  
      <div>
        <label>CVC</label>
        <input v-model="cvc" maxlength="3" required />
      </div>
  
      <button :disabled="processing" type="submit">
        {{ processing ? 'Processing...' : 'Pay & Book' }}
      </button>
  
      <p v-if="errorMessage" class="text-red-600 mt-2">{{ errorMessage }}</p>
    </form>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  
  const name = ref('')
  const email = ref('')
  const cardNumber = ref('')
  const expiry = ref('')
  const cvc = ref('')
  const processing = ref(false)
  const errorMessage = ref('')
  
  const simulatePayment = () => {
    // Basic mock validation
    if (!/^\d{4} \d{4} \d{4} \d{4}$/.test(cardNumber.value)) {
      errorMessage.value = 'Invalid card number'
      return
    }
  
    if (!/^\d{2}\/\d{2}$/.test(expiry.value)) {
      errorMessage.value = 'Invalid expiry date'
      return
    }
  
    if (!/^\d{3}$/.test(cvc.value)) {
      errorMessage.value = 'Invalid CVC'
      return
    }
  
    errorMessage.value = ''
    processing.value = true
  
    setTimeout(() => {
      processing.value = false
      router.push('/booking-success')
    }, 2000) // Simulate API delay
  }
  </script>
  
  <style scoped>
  .payment-form {
    max-width: 400px;
    margin: auto;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  input {
    padding: 0.5rem;
    font-size: 1rem;
    width: 100%;
  }
  button {
    background: #4f46e5;
    color: white;
    padding: 0.75rem;
    font-weight: bold;
    border: none;
    cursor: pointer;
  }
  button[disabled] {
    opacity: 0.6;
    cursor: not-allowed;
  }
  </style>
  