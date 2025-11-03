<template>
  <div class="login-container">
    <div class="container">
      <div class="row justify-content-center align-items-center min-vh-100">
        <div class="col-11 col-sm-8 col-md-6 col-lg-4 col-xl-3">
          <div class="card shadow-lg">
            <div class="card-body p-4 p-md-5">
              <div class="text-center mb-4">
                <i class="bi bi-car-front fs-1 text-primary"></i>
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
                  required
                >
              </div>
              
              <div class="mb-4">
                <label class="form-label">Contraseña</label>
                <input 
                  type="password" 
                  class="form-control" 
                  v-model="credentials.password"
                  required
                >
              </div>
              
              <button type="submit" class="btn btn-primary w-100">
                Iniciar Sesión
              </button>
            </form>
            
            <div v-if="error" class="alert alert-danger mt-3">
              {{ error }}
            </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const credentials = ref({
  username: 'admin',
  password: 'admin123'
})
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
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1rem;
}

.card {
  border-radius: 1rem;
  border: none;
}

.bi-car-front {
  font-size: 3rem;
}

@media (min-width: 768px) {
  .login-container {
    padding: 2rem;
  }
}
</style>