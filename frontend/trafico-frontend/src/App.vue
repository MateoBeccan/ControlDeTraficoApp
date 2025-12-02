<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const route = useRoute()
const isLoggedIn = ref(true)

const logout = () => {
  isLoggedIn.value = false
  router.push('/login')
}
</script>

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
    <main class="container-fluid py-4" :class="{ 'with-sidebar': route.path !== '/login' }">
      <div class="row">
        <div class="col-12">
          <router-view />
        </div>
      </div>
    </main>
  </div>
</template>

<style>
body {
  background-color: #f8f9fa;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 16px;
}

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

.card {
  border: none;
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.1);
  border-radius: 0.75rem;
}

.btn {
  border-radius: 0.5rem;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

.btn-lg {
  padding: 1rem 2rem;
  font-size: 1.1rem;
}

.form-control, .form-select {
  padding: 0.75rem 1rem;
  font-size: 1rem;
  border-radius: 0.5rem;
}

.form-label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.table {
  border-radius: 0.75rem;
  overflow: hidden;
  font-size: 1rem;
}

.table th {
  font-weight: 600;
  font-size: 1rem;
  padding: 1rem;
}

.table td {
  padding: 1rem;
  vertical-align: middle;
}

.bg-primary {
  background-color: #4a6fa5 !important;
}

.btn-primary {
  background-color: #4a6fa5;
  border-color: #4a6fa5;
}

.btn-primary:hover {
  background-color: #3d5a91;
  border-color: #3d5a91;
}

.modal-dialog {
  max-width: 600px;
}

.modal-header {
  padding: 1.5rem;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  padding: 1.5rem;
}

.alert {
  padding: 1rem;
  border-radius: 0.5rem;
  font-size: 0.95rem;
}

/* Responsive improvements */
@media (min-width: 768px) {
  .container-fluid {
    max-width: 1400px;
    margin: 0 auto;
  }
}

@media (min-width: 992px) {
  .with-sidebar {
    padding-left: 2rem;
    padding-right: 2rem;
  }
}

.table-responsive {
  border-radius: 0.75rem;
}

@media (max-width: 767px) {
  .btn {
    padding: 0.6rem 1.2rem;
  }

  .btn-sm {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
}

/* Mejoras para btn-group */
.btn-group .btn {
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  border-radius: 0;
}

.btn-group .btn:first-child {
  border-top-left-radius: 0.375rem;
  border-bottom-left-radius: 0.375rem;
}

.btn-group .btn:last-child {
  border-top-right-radius: 0.375rem;
  border-bottom-right-radius: 0.375rem;
}

.btn-group .btn i {
  font-size: 1rem;
}

/* Botones de acción en tablas */
.table .btn {
  min-width: 2.5rem;
  height: 2.5rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.table .btn i {
  font-size: 0.875rem;
}

.table .btn-sm {
  min-width: 2rem;
  height: 2rem;
  padding: 0.25rem 0.5rem;
}
</style>
