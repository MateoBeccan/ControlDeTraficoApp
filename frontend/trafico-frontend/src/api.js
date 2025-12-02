import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  auth: {
    username: 'admin',
    password: 'admin123'
  }
})

export default {
  // Personas
  getPersonas: () => api.get('/personas'),
  getPersona: (dni) => api.get(`/personas/${dni}`),
  createPersona: (data) => api.post('/personas', data),
  updatePersona: (dni, data) => api.put(`/personas/${dni}`, data),
  deletePersona: (dni) => api.delete(`/personas/${dni}`),

  // Vehiculos
  getVehiculos: () => api.get('/vehiculos'),
  getVehiculo: (patente) => api.get(`/vehiculos/${patente}`),
  createVehiculo: (data) => api.post('/vehiculos', data),
  updateVehiculo: (patente, data) => api.put(`/vehiculos/${patente}`, data),
  deleteVehiculo: (patente) => api.delete(`/vehiculos/${patente}`),

  // Accidentes
  getAccidentes: () => api.get('/accidentes'),
  getAccidente: (id) => api.get(`/accidentes/${id}`),
  createAccidente: (data) => api.post('/accidentes', data),
  updateAccidente: (id, data) => api.put(`/accidentes/${id}`, data),
  deleteAccidente: (id) => api.delete(`/accidentes/${id}`),

  // Multas
  getMultas: () => api.get('/multas'),
  getMulta: (id) => api.get(`/multas/${id}`),
  createMulta: (data) => api.post('/multas', data),
  updateMulta: (id, data) => api.put(`/multas/${id}`, data),
  deleteMulta: (id) => api.delete(`/multas/${id}`),

  // Jueces
  getJueces: () => api.get('/jueces'),
  getJuez: (id) => api.get(`/jueces/${id}`),
  createJuez: (data) => api.post('/jueces', data),
  updateJuez: (id, data) => api.put(`/jueces/${id}`, data),
  deleteJuez: (id) => api.delete(`/jueces/${id}`),

  // Detalle Accidentes
  getDetalleAccidentes: () => api.get('/detalle-accidentes'),
  getDetalleAccidente: (accidente, vehiculo) => api.get(`/detalle-accidentes/buscar?accidente=${accidente}&vehiculo=${vehiculo}`),
  createDetalleAccidente: (data) => api.post('/detalle-accidentes', data),
  updateDetalleAccidente: (data) => api.put('/detalle-accidentes', data),
  deleteDetalleAccidente: (accidente, vehiculo) => api.delete(`/detalle-accidentes?accidente=${accidente}&vehiculo=${vehiculo}`),

  // Detalle Multas
  getDetalleMultas: () => api.get('/detalle-multas'),
  getDetalleMulta: (multa, vehiculo) => api.get(`/detalle-multas/buscar?multa=${multa}&vehiculo=${vehiculo}`),
  createDetalleMulta: (data) => api.post('/detalle-multas', data),
  updateDetalleMulta: (data) => api.put('/detalle-multas', data),
  deleteDetalleMulta: (multa, vehiculo) => api.delete(`/detalle-multas?multa=${multa}&vehiculo=${vehiculo}`)
}