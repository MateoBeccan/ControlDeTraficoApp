<template>
  <div class="vehiculos">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Vehículos</h2>
          <button class="btn btn-primary btn-lg" @click="showModal = true">
            <i class="bi bi-plus-circle me-2"></i> Nuevo Vehículo
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
                    <th class="px-4 py-3">Patente</th>
                    <th class="px-4 py-3">Marca</th>
                    <th class="px-4 py-3 d-none d-lg-table-cell">Propietario</th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="vehiculo in vehiculos" :key="vehiculo.patente">
                    <td class="px-4 py-3">
                      <strong>{{ vehiculo.patente }}</strong>
                    </td>
                    <td class="px-4 py-3">
                      <div>
                        {{ vehiculo.marca }}
                        <div class="d-lg-none text-muted small">{{ vehiculo.propietario?.nombre || 'Sin propietario' }}</div>
                      </div>
                    </td>
                    <td class="px-4 py-3 d-none d-lg-table-cell">{{ vehiculo.propietario?.nombre || 'Sin propietario' }}</td>
                    <td class="px-4 py-3 text-center">
                      <div class="btn-group" role="group">
                        <button class="btn btn-outline-primary" @click="editVehiculo(vehiculo)" title="Editar">
                          <i class="bi bi-pencil me-1"></i> Editar
                        </button>
                        <button class="btn btn-outline-danger" @click="deleteVehiculo(vehiculo.patente)" title="Eliminar">
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
            <h5 class="modal-title">{{ editingVehiculo ? 'Editar' : 'Nuevo' }} Vehículo</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveVehiculo">
              <div class="mb-4">
                <label class="form-label">Patente *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.patente }"
                  v-model="form.patente" 
                  :disabled="editingVehiculo" 
                  @blur="validatePatente"
                  maxlength="7"
                  required
                >
                <div v-if="errors.patente" class="invalid-feedback">{{ errors.patente }}</div>
              </div>
              <div class="mb-4">
                <label class="form-label">Marca *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.marca }"
                  v-model="form.marca" 
                  @blur="validateMarca"
                  maxlength="30"
                  required
                >
                <div v-if="errors.marca" class="invalid-feedback">{{ errors.marca }}</div>
              </div>
              <div class="mb-4">
                <label class="form-label">Propietario *</label>
                <select 
                  class="form-select" 
                  :class="{ 'is-invalid': errors.propietario }"
                  v-model="form.propietario" 
                  @change="validatePropietario"
                  required
                >
                  <option value="">Seleccione un propietario</option>
                  <option v-for="persona in personas" :key="persona.dni" :value="persona">
                    {{ persona.nombre }} ({{ persona.dni }})
                  </option>
                </select>
                <div v-if="errors.propietario" class="invalid-feedback">{{ errors.propietario }}</div>
              </div>
            </form>
            
            <div v-if="apiError" class="alert alert-danger">
              {{ apiError }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-lg" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary btn-lg" @click="saveVehiculo" :disabled="!isFormValid">Guardar</button>
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

const vehiculos = ref([])
const personas = ref([])
const showModal = ref(false)
const editingVehiculo = ref(false)
const apiError = ref('')
const form = ref({
  patente: '',
  marca: '',
  propietario: null
})
const errors = ref({
  patente: '',
  marca: '',
  propietario: ''
})

const isFormValid = computed(() => {
  return form.value.patente && 
         form.value.marca && 
         form.value.propietario &&
         !errors.value.patente && 
         !errors.value.marca && 
         !errors.value.propietario
})

const validatePatente = () => {
  if (!form.value.patente || form.value.patente.trim() === '') {
    errors.value.patente = 'La patente es obligatoria'
  } else if (form.value.patente.length > 7) {
    errors.value.patente = 'La patente no puede exceder 7 caracteres'
  } else {
    errors.value.patente = ''
  }
}

const validateMarca = () => {
  if (!form.value.marca || form.value.marca.trim() === '') {
    errors.value.marca = 'La marca no puede estar vacía'
  } else if (form.value.marca.length > 30) {
    errors.value.marca = 'La marca no puede exceder 30 caracteres'
  } else {
    errors.value.marca = ''
  }
}

const validatePropietario = () => {
  if (!form.value.propietario) {
    errors.value.propietario = 'El propietario es obligatorio'
  } else {
    errors.value.propietario = ''
  }
}

const loadVehiculos = async () => {
  try {
    const response = await api.getVehiculos()
    vehiculos.value = response.data
  } catch (error) {
    console.error('Error loading vehiculos:', error)
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

const editVehiculo = (vehiculo) => {
  form.value = { ...vehiculo }
  editingVehiculo.value = true
  showModal.value = true
}

const saveVehiculo = async () => {
  // Validar todos los campos
  validatePatente()
  validateMarca()
  validatePropietario()
  
  if (!isFormValid.value) {
    return
  }
  
  try {
    apiError.value = ''
    if (editingVehiculo.value) {
      await api.updateVehiculo(form.value.patente, form.value)
    } else {
      await api.createVehiculo(form.value)
    }
    closeModal()
    loadVehiculos()
  } catch (error) {
    console.error('Error saving vehiculo:', error)
    if (error.response?.data?.message) {
      apiError.value = error.response.data.message
    } else {
      apiError.value = 'Error al guardar el vehículo. Verifique los datos.'
    }
  }
}

const deleteVehiculo = async (patente) => {
  if (confirm('¿Está seguro de eliminar este vehículo?')) {
    try {
      await api.deleteVehiculo(patente)
      loadVehiculos()
    } catch (error) {
      console.error('Error deleting vehiculo:', error)
      alert('Error al eliminar el vehículo')
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingVehiculo.value = false
  apiError.value = ''
  form.value = { patente: '', marca: '', propietario: null }
  errors.value = { patente: '', marca: '', propietario: '' }
}

onMounted(() => {
  loadVehiculos()
  loadPersonas()
})
</script>