<template>
  <div class="personas">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Personas</h2>
          <button class="btn btn-primary btn-lg" @click="showModal = true">
            <i class="bi bi-plus-circle me-2"></i> Nueva Persona
          </button>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                  <tr>
                    <th class="px-4 py-3">DNI</th>
                    <th class="px-4 py-3">Nombre</th>
                    <th class="px-4 py-3 d-none d-md-table-cell">Dirección</th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="persona in personas" :key="persona.dni">
                    <td class="px-4 py-3">{{ persona.dni }}</td>
                    <td class="px-4 py-3">
                      <div>
                        <strong>{{ persona.nombre }}</strong>
                        <div class="d-md-none text-muted small">{{ persona.direccion }}</div>
                      </div>
                    </td>
                    <td class="px-4 py-3 d-none d-md-table-cell">{{ persona.direccion }}</td>
                    <td class="px-4 py-3 text-center">
                      <div class="btn-group" role="group">
                        <button class="btn btn-outline-primary" @click="editPersona(persona)" title="Editar">
                          <i class="bi bi-pencil me-1"></i> Editar
                        </button>
                        <button class="btn btn-outline-danger" @click="deletePersona(persona.dni)" title="Eliminar">
                          <i class="bi bi-trash me-1"></i> Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" :class="{ show: showModal }" :style="{ display: showModal ? 'block' : 'none' }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editingPersona ? 'Editar' : 'Nueva' }} Persona</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="savePersona">
              <div class="mb-4">
                <label class="form-label">DNI *</label>
                <input 
                  type="number" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.dni }"
                  v-model="form.dni" 
                  :disabled="editingPersona" 
                  @blur="validateDni"
                  required
                >
                <div v-if="errors.dni" class="invalid-feedback">{{ errors.dni }}</div>
              </div>
              <div class="mb-4">
                <label class="form-label">Nombre *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.nombre }"
                  v-model="form.nombre" 
                  @blur="validateNombre"
                  maxlength="50"
                  required
                >
                <div v-if="errors.nombre" class="invalid-feedback">{{ errors.nombre }}</div>
              </div>
              <div class="mb-4">
                <label class="form-label">Dirección</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.direccion }"
                  v-model="form.direccion"
                  @blur="validateDireccion"
                  maxlength="100"
                >
                <div v-if="errors.direccion" class="invalid-feedback">{{ errors.direccion }}</div>
              </div>
            </form>
            
            <div v-if="apiError" class="alert alert-danger">
              {{ apiError }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-lg" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary btn-lg" @click="savePersona" :disabled="!isFormValid">Guardar</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from './api.js'

const personas = ref([])
const showModal = ref(false)
const editingPersona = ref(false)
const apiError = ref('')
const form = ref({
  dni: '',
  nombre: '',
  direccion: ''
})
const errors = ref({
  dni: '',
  nombre: '',
  direccion: ''
})

const isFormValid = computed(() => {
  return form.value.dni && 
         form.value.nombre && 
         !errors.value.dni && 
         !errors.value.nombre && 
         !errors.value.direccion
})

const validateDni = () => {
  if (!form.value.dni) {
    errors.value.dni = 'El DNI es obligatorio'
  } else if (form.value.dni <= 0) {
    errors.value.dni = 'El DNI debe ser un número positivo'
  } else {
    errors.value.dni = ''
  }
}

const validateNombre = () => {
  if (!form.value.nombre || form.value.nombre.trim() === '') {
    errors.value.nombre = 'El nombre no puede estar vacío'
  } else if (form.value.nombre.length > 50) {
    errors.value.nombre = 'El nombre no puede exceder 50 caracteres'
  } else {
    errors.value.nombre = ''
  }
}

const validateDireccion = () => {
  if (form.value.direccion && form.value.direccion.length > 100) {
    errors.value.direccion = 'La dirección no puede exceder 100 caracteres'
  } else {
    errors.value.direccion = ''
  }
}

const loadPersonas = async () => {
  try {
    const response = await api.getPersonas()
    personas.value = response.data
  } catch (error) {
    console.error('Error loading personas:', error)
  }
}

const editPersona = (persona) => {
  form.value = { ...persona }
  editingPersona.value = true
  showModal.value = true
}

const savePersona = async () => {
  // Validar todos los campos
  validateDni()
  validateNombre()
  validateDireccion()
  
  if (!isFormValid.value) {
    return
  }
  
  try {
    apiError.value = ''
    if (editingPersona.value) {
      await api.updatePersona(form.value.dni, form.value)
    } else {
      await api.createPersona(form.value)
    }
    closeModal()
    loadPersonas()
  } catch (error) {
    console.error('Error saving persona:', error)
    if (error.response?.data?.message) {
      apiError.value = error.response.data.message
    } else {
      apiError.value = 'Error al guardar la persona. Verifique los datos.'
    }
  }
}

const deletePersona = async (dni) => {
  if (confirm('¿Está seguro de eliminar esta persona?')) {
    try {
      await api.deletePersona(dni)
      loadPersonas()
    } catch (error) {
      console.error('Error deleting persona:', error)
      alert('Error al eliminar la persona')
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingPersona.value = false
  apiError.value = ''
  form.value = { dni: '', nombre: '', direccion: '' }
  errors.value = { dni: '', nombre: '', direccion: '' }
}

onMounted(() => {
  loadPersonas()
})
</script>