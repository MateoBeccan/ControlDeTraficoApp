<template>
  <div class="login-container d-flex justify-content-center align-items-center">
    <div class="login-card shadow-lg">
      <div class="text-center mb-4">
        <i class="bi bi-car-front text-primary"></i>
        <h3 class="mt-3">Sistema de Tráfico</h3>
        <p class="text-muted">Ingrese sus credenciales</p>
      </div>

      <form @submit.prevent="login">
        <div class="mb-3">
          <label class="form-label">Usuario</label>
          <input
            type="text"
            class="form-control"
            v-model="credentials.username"
            placeholder="Ingrese usuario"
            required
          >
        </div>

        <div class="mb-4">
          <label class="form-label">Contraseña</label>
          <input
            type="password"
            class="form-control"
            v-model="credentials.password"
            placeholder="Ingrese contraseña"
            required
          >
        </div>

        <button type="submit" class="btn btn-primary w-100">
          Iniciar Sesión
        </button>
      </form>

      <div v-if="error" class="alert alert-danger mt-3 text-center">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const credentials = ref({ username: '', password: '' })
const error = ref('')

const login = () => {
  if (credentials.value.username === 'admin' && credentials.value.password === 'admin123') {
    router.push('/dashboard')
  } else {
    error.value = 'Credenciales incorrectas'
  }
}
</script>

<style scoped>
/* Contenedor principal ocupa toda la pantalla y sin padding extra */
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
}

/* Tarjeta centrada y elegante */
.login-card {
  background-color: #ffffff;
  border-radius: 1rem;
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  text-align: center;
}

/* Icono grande y colorido */
.bi-car-front {
  font-size: 3rem;
}

/* Inputs modernos */
.form-control {
  border-radius: 0.5rem;
  padding: 0.75rem 1rem;
  transition: all 0.3s ease;
}

.form-control:focus {
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.5);
  border-color: #667eea;
}

/* Botón con transición */
.btn-primary {
  border-radius: 0.5rem;
  padding: 0.75rem 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: #5562d6;
  border-color: #5562d6;
}

/* Alert centrado */
.alert {
  margin-top: 1rem;
}

/* Responsivo */
@media (max-width: 576px) {
  .login-card {
    padding: 2rem 1.5rem;
  }
}
</style>
