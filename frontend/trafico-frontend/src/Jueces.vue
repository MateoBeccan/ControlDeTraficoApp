<template>
  <div class="jueces">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Jueces</h2>
          <button class="btn btn-primary btn-lg" @click="showModal = true">
            + Nuevo Juez
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
                    <th class="px-4 py-3">Código</th>
                    <th class="px-4 py-3">Apellido</th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
            <tbody>
              <tr v-for="juez in jueces" :key="juez.codigo">
                <td class="px-4 py-3">
                  <strong>{{ juez.codigo }}</strong>
                </td>
                <td class="px-4 py-3">{{ juez.apellido }}</td>
                    <td class="px-4 py-3 text-center">
                      <button class="btn btn-primary btn-sm me-1" @click="editJuez(juez)">
                        Editar
                      </button>
                      <button class="btn btn-danger btn-sm" @click="deleteJuez(juez.codigo)">
                        Eliminar
                      </button>
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
            <h5 class="modal-title">{{ editingJuez ? 'Editar' : 'Nuevo' }} Juez</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveJuez">
              <div class="mb-4">
                <label class="form-label">Código *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.codigo }"
                  v-model="form.codigo" 
                  :disabled="editingJuez" 
                  @input="validateCodigo"
                  @blur="validateCodigo"
                  placeholder="Ej: 123"
                  required
                >
                <small v-if="errors.codigo" class="text-danger">{{ errors.codigo }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Apellido *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.apellido }"
                  v-model="form.apellido" 
                  @input="validateApellido"
                  @blur="validateApellido"
                  placeholder="Ej: García"
                  maxlength="50"
                  required
                >
                <small v-if="errors.apellido" class="text-danger">{{ errors.apellido }}</small>
              </div>
            </form>
            
            <div v-if="apiError" class="alert alert-danger">
              {{ apiError }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-lg" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary btn-lg" @click="saveJuez" :disabled="!isFormValid">Guardar</button>
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

const jueces = ref([])
const showModal = ref(false)
const editingJuez = ref(false)
const apiError = ref('')
const form = ref({
  codigo: '',
  apellido: ''
})
const errors = ref({
  codigo: '',
  apellido: ''
})

const isFormValid = computed(() => {
  return form.value.codigo && 
         form.value.apellido &&
         !errors.value.codigo && 
         !errors.value.apellido
})

const validateCodigo = () => {
  const codigo = form.value.codigo?.toString() || ''
  if (!codigo) {
    errors.value.codigo = 'El código es obligatorio'
  } else if (!/^\d+$/.test(codigo)) {
    errors.value.codigo = 'El código debe ser numérico'
  } else {
    errors.value.codigo = ''
  }
}

const validateApellido = () => {
  const apellido = form.value.apellido?.trim() || ''
  if (!apellido) {
    errors.value.apellido = 'El apellido es obligatorio'
  } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(apellido)) {
    errors.value.apellido = 'El apellido solo puede contener letras'
  } else if (apellido.length < 2 || apellido.length > 50) {
    errors.value.apellido = 'El apellido debe tener entre 2 y 50 caracteres'
  } else {
    errors.value.apellido = ''
  }
}

const loadJueces = async () => {
  try {
    const response = await api.getJueces()
    jueces.value = response.data
  } catch (error) {
    console.error('Error loading jueces:', error)
  }
}

const editJuez = (juez) => {
  form.value = { ...juez }
  editingJuez.value = true
  showModal.value = true
}

const saveJuez = async () => {
  // Validar todos los campos
  validateCodigo()
  validateApellido()
  
  if (!isFormValid.value) {
    return
  }
  
  try {
    apiError.value = ''
    if (editingJuez.value) {
      await api.updateJuez(form.value.codigo, form.value)
    } else {
      await api.createJuez(form.value)
    }
    closeModal()
    loadJueces()
  } catch (error) {
    console.error('Error saving juez:', error)
    if (error.response?.data?.message) {
      apiError.value = error.response.data.message
    } else {
      apiError.value = 'Error al guardar el juez. Verifique los datos.'
    }
  }
}

const deleteJuez = async (codigo) => {
  if (confirm('¿Está seguro de eliminar este juez?')) {
    try {
      await api.deleteJuez(codigo)
      loadJueces()
    } catch (error) {
      console.error('Error deleting juez:', error)
      alert('Error al eliminar el juez')
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingJuez.value = false
  apiError.value = ''
  form.value = { codigo: '', apellido: '' }
  errors.value = { codigo: '', apellido: '' }
}

onMounted(() => {
  loadJueces()
})
</script>