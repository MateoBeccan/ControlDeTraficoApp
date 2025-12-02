<template>
  <div id="app">
    <!-- Navbar -->
    <nav v-if="route.path !== '/login'" class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container-fluid">
        <router-link class="navbar-brand" to="/dashboard">
          <i class="bi bi-car-front"></i> Sistema de Tráfico
        </router-link>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/dashboard">Panel principal</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/personas">Personas</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/vehiculos">Vehículos</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/accidentes">Accidentes</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/multas">Multas</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/jueces">Jueces</router-link>
            </li>
          </ul>
          <button class="btn btn-outline-light" @click="logout">
            <i class="bi bi-box-arrow-right"></i> Cerrar Sesión
          </button>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main :class="['main-container', { 'login-page': route.path === '/login' }]">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const logout = () => {
  router.push('/login')
}
</script>

<style>
/* Reset básico */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-family: 'Inter', sans-serif;
}

/* Fondo general */
body {
  background-color: #f0f2f5;
}

/* Navbar */
.navbar {
  padding: 1rem 0;
}

.navbar-brand {
  font-weight: 600;
  font-size: 1.5rem;
}

.nav-link {
  font-size: 1.1rem;
  padding: 0.75rem 1rem !important;
}

.nav-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 0.5rem;
}

/* Botones */
.btn {
  border-radius: 0.5rem;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
}

.btn-primary {
  background-color: #667eea;
  border-color: #667eea;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: #5a67d8;
}

/* Tarjetas y formularios */
.card {
  border: none;
  border-radius: 1rem;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.form-control {
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  border: 1px solid #ccc;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 0.25rem rgba(102,126,234,0.25);
}

/* Main content */
.main-container {
  min-height: 100vh;
  padding: 2rem;
}

/* Login específico */
.main-container.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
}

/* Responsive */
@media (min-width: 768px) {
  .container-fluid {
    max-width: 1400px;
    margin: 0 auto;
  }
}
</style>
