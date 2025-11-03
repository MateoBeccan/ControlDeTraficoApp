<template>
  <div class="dashboard">
    <div class="row mb-4">
      <div class="col-12">
        <h2 class="text-primary">Panel Principal</h2>
        <p class="text-muted">Sistema de Gestión de Tráfico</p>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-sm-6 col-lg-4 col-xl-3">
        <div class="card h-100 card-hover" @click="$router.push('/personas')">
          <div class="card-body text-center p-5">
            <i class="bi bi-people display-4 text-info mb-4"></i>
            <h5 class="card-title mb-3">Personas</h5>
            <p class="card-text text-muted">Gestionar información de personas</p>
            <span class="badge bg-info fs-6 px-3 py-2">{{ stats.personas }} registros</span>
          </div>
        </div>
      </div>

      <div class="col-sm-6 col-lg-4 col-xl-3">
        <div class="card h-100 card-hover" @click="$router.push('/vehiculos')">
          <div class="card-body text-center p-5">
            <i class="bi bi-car-front display-4 text-success mb-4"></i>
            <h5 class="card-title mb-3">Vehículos</h5>
            <p class="card-text text-muted">Administrar vehículos registrados</p>
            <span class="badge bg-success fs-6 px-3 py-2">{{ stats.vehiculos }} registros</span>
          </div>
        </div>
      </div>

      <div class="col-sm-6 col-lg-4 col-xl-3">
        <div class="card h-100 card-hover" @click="$router.push('/accidentes')">
          <div class="card-body text-center p-5">
            <i class="bi bi-exclamation-triangle display-4 text-warning mb-4"></i>
            <h5 class="card-title mb-3">Accidentes</h5>
            <p class="card-text text-muted">Registro de accidentes de tráfico</p>
            <span class="badge bg-warning fs-6 px-3 py-2">{{ stats.accidentes }} registros</span>
          </div>
        </div>
      </div>

      <div class="col-sm-6 col-lg-4 col-xl-3">
        <div class="card h-100 card-hover" @click="$router.push('/multas')">
          <div class="card-body text-center p-5">
            <i class="bi bi-receipt display-4 text-danger mb-4"></i>
            <h5 class="card-title mb-3">Multas</h5>
            <p class="card-text text-muted">Gestión de multas de tráfico</p>
            <span class="badge bg-danger fs-6 px-3 py-2">{{ stats.multas }} registros</span>
          </div>
        </div>
      </div>

      <div class="col-sm-6 col-lg-4 col-xl-3">
        <div class="card h-100 card-hover" @click="$router.push('/jueces')">
          <div class="card-body text-center p-5">
            <i class="bi bi-person-badge display-4 text-secondary mb-4"></i>
            <h5 class="card-title mb-3">Jueces</h5>
            <p class="card-text text-muted">Administrar jueces del sistema</p>
            <span class="badge bg-secondary fs-6 px-3 py-2">{{ stats.jueces }} registros</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from './api.js'

const stats = ref({
  personas: 0,
  vehiculos: 0,
  accidentes: 0,
  multas: 0,
  jueces: 0
})

const loadStats = async () => {
  try {
    const [personas, vehiculos, accidentes, multas, jueces] = await Promise.all([
      api.getPersonas(),
      api.getVehiculos(),
      api.getAccidentes(),
      api.getMultas(),
      api.getJueces()
    ])

    stats.value = {
      personas: personas.data.length,
      vehiculos: vehiculos.data.length,
      accidentes: accidentes.data.length,
      multas: multas.data.length,
      jueces: jueces.data.length
    }
  } catch (error) {
    console.error('Error loading stats:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.card-hover {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.bi {
  display: block;
}
</style>
