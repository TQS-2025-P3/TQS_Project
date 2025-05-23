import { defineStore } from 'pinia'
import axios from 'axios'

export const useStationStore = defineStore('station', {
  state: () => ({
    stations: [],
    selectedStation: null,
  }),
  actions: {
    async fetchStations() {
      // Replace with your API later
      const { data } = await axios.get('/api/stations')
      this.stations = data
    },
    async fetchStation(id) {
      const { data } = await axios.get(`/api/stations/${id}`)
      this.selectedStation = data
    },
  },
})
