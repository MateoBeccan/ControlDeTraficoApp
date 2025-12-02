<template>
  <div class="accidentes">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Accidentes</h2>
          <button class="btn btn-primary btn-lg" @click="showModal = true">
            + Nuevo Accidente
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
                    <th class="px-4 py-3">Número</th>
                    <th class="px-4 py-3">Fecha</th>
                    <th class="px-4 py-3 d-none d-md-table-cell">Lugar</th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="accidente in accidentes" :key="accidente.numero">
                    <td class="px-4 py-3">
                      <strong>{{ accidente.numero }}</strong>
                    </td>
                    <td class="px-4 py-3">
                      <div>
                        {{ formatDate(accidente.fecha) }}
                        <div class="d-md-none text-muted small">{{ accidente.lugar }}</div>
                      </div>
                    </td>
                    <td class="px-4 py-3 d-none d-md-table-cell">{{ accidente.lugar }}</td>
                    <td class="px-4 py-3 text-center">
                      <button class="btn btn-info btn-sm me-1" @click="viewDetalles(accidente)">
                        Detalles
                      </button>
                      <button class="btn btn-primary btn-sm me-1" @click="editAccidente(accidente)">
                        Editar
                      </button>
                      <button class="btn btn-danger btn-sm" @click="deleteAccidente(accidente.numero)">
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
            <h5 class="modal-title">{{ editingAccidente ? 'Editar' : 'Nuevo' }} Accidente</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveAccidente">
              <div class="mb-4">
                <label class="form-label">Número *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.numero }"
                  v-model="form.numero" 
                  :disabled="editingAccidente" 
                  @input="validateNumero"
                  @blur="validateNumero"
                  placeholder="Ej: 12345"
                  required
                >
                <small v-if="errors.numero" class="text-danger">{{ errors.numero }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Fecha *</label>
                <input 
                  type="date" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.fecha }"
                  v-model="form.fecha" 
                  @change="validateFecha"
                  @blur="validateFecha"
                  :max="new Date().toISOString().split('T')[0]"
                  required
                >
                <small v-if="errors.fecha" class="text-danger">{{ errors.fecha }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Lugar *</label>
                <input 
                  type="text" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.lugar }"
                  v-model="form.lugar" 
                  @input="validateLugar"
                  @blur="validateLugar"
                  placeholder="Ej: Av. Corrientes y Callao"
                  maxlength="100"
                  required
                >
                <small v-if="errors.lugar" class="text-danger">{{ errors.lugar }}</small>
              </div>
            </form>
            
            <div v-if="apiError" class="alert alert-danger">
              {{ apiError }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-lg" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary btn-lg" @click="saveAccidente" :disabled="!isFormValid">Guardar</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal-backdrop fade show"></div>

    <!-- Modal Detalles -->
    <div class="modal fade" :class="{ show: showDetallesModal }" :style="{ display: showDetallesModal ? 'block' : 'none' }">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Detalles del Accidente {{ selectedAccidente?.numero }}</h5>
            <button type="button" class="btn-close" @click="closeDetallesModal"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <strong>Fecha:</strong> {{ formatDate(selectedAccidente?.fecha) }}
              </div>
              <div class="col-md-6">
                <strong>Lugar:</strong> {{ selectedAccidente?.lugar }}
              </div>
            </div>
            
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h6>Vehículos Involucrados</h6>
              <button class="btn btn-success btn-sm" @click="showDetalleForm = true">
                <i class="bi bi-plus"></i> Agregar Vehículo
              </button>
            </div>
            
            <div class="table-responsive">
              <table class="table table-sm">
                <thead>
                  <tr>
                    <th>Vehículo</th>
                    <th>Propietario</th>
                    <th>Descripción</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="detalle in detallesAccidente" :key="detalle.vehiculo?.patente">
                    <td>{{ detalle.vehiculo?.patente }} - {{ detalle.vehiculo?.marca }}</td>
                    <td>{{ detalle.vehiculo?.propietario?.nombre }}</td>
                    <td>{{ detalle.descripcion }}</td>
                    <td>
                      <button class="btn btn-sm btn-outline-danger" @click="deleteDetalle(detalle)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- Formulario agregar detalle -->
            <div v-if="showDetalleForm" class="border-top pt-3 mt-3">
              <h6>Agregar Vehículo al Accidente</h6>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Vehículo *</label>
                  <select class="form-select" v-model="detalleForm.vehiculo" required>
                    <option value="">Seleccione un vehículo</option>
                    <option v-for="vehiculo in vehiculos" :key="vehiculo.patente" :value="vehiculo">
                      {{ vehiculo.patente }} - {{ vehiculo.marca }}
                    </option>
                  </select>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Descripción *</label>
                  <textarea class="form-control" v-model="detalleForm.descripcion" maxlength="200" rows="2" placeholder="Descripción del daño o situación"></textarea>
                </div>
              </div>
              <div class="text-end">
                <button class="btn btn-secondary me-2" @click="cancelDetalleForm">Cancelar</button>
                <button class="btn btn-primary" @click="saveDetalle">Guardar</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showDetallesModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from './api.js'

const accidentes = ref([])
const vehiculos = ref([])
const detallesAccidente = ref([])
const showModal = ref(false)
const showDetallesModal = ref(false)
const showDetalleForm = ref(false)
const editingAccidente = ref(false)
const selectedAccidente = ref(null)
const apiError = ref('')
const form = ref({
  numero: '',
  fecha: '',
  lugar: ''
})
const detalleForm = ref({
  vehiculo: null,
  descripcion: ''
})
const errors = ref({
  numero: '',
  fecha: '',
  lugar: ''
})

const isFormValid = computed(() => {
  return form.value.numero && 
         form.value.fecha &&
         form.value.lugar &&
         !errors.value.numero && 
         !errors.value.fecha &&
         !errors.value.lugar
})

const validateNumero = () => {
  const numero = form.value.numero?.toString() || ''
  if (!numero) {
    errors.value.numero = 'El número es obligatorio'
  } else if (!/^\d+$/.test(numero)) {
    errors.value.numero = 'El número debe ser numérico'
  } else {
    errors.value.numero = ''
  }
}

const validateFecha = () => {
  if (!form.value.fecha) {
    errors.value.fecha = 'La fecha es obligatoria'
  } else {
    const fechaSeleccionada = new Date(form.value.fecha)
    const hoy = new Date()
    hoy.setHours(23, 59, 59, 999) // Fin del día actual
    
    if (fechaSeleccionada > hoy) {
      errors.value.fecha = 'La fecha no puede ser futura'
    } else {
      errors.value.fecha = ''
    }
  }
}

const validateLugar = () => {
  const lugar = form.value.lugar?.trim() || ''
  if (!lugar) {
    errors.value.lugar = 'El lugar es obligatorio'
  } else if (lugar.length < 3 || lugar.length > 100) {
    errors.value.lugar = 'El lugar debe tener entre 3 y 100 caracteres'
  } else {
    errors.value.lugar = ''
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('es-ES')
}

const loadAccidentes = async () => {
  try {
    const response = await api.getAccidentes()
    accidentes.value = response.data
  } catch (error) {
    console.error('Error loading accidentes:', error)
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

const loadDetallesAccidente = async (numeroAccidente) => {
  try {
    const response = await api.getDetalleAccidentes()
    detallesAccidente.value = response.data.filter(d => d.accidente?.numero == numeroAccidente)
  } catch (error) {
    console.error('Error loading detalles:', error)
  }
}

const viewDetalles = async (accidente) => {
  selectedAccidente.value = accidente
  await loadDetallesAccidente(accidente.numero)
  await loadVehiculos()
  showDetallesModal.value = true
}

const saveDetalle = async () => {
  if (!detalleForm.value.vehiculo || !detalleForm.value.descripcion) {
    alert('Debe completar todos los campos')
    return
  }
  
  try {
    const detalle = {
      accidente: selectedAccidente.value,
      vehiculo: detalleForm.value.vehiculo,
      descripcion: detalleForm.value.descripcion
    }
    
    await api.createDetalleAccidente(detalle)
    await loadDetallesAccidente(selectedAccidente.value.numero)
    cancelDetalleForm()
  } catch (error) {
    console.error('Error saving detalle:', error)
    alert('Error al guardar el detalle')
  }
}

const deleteDetalle = async (detalle) => {
  if (confirm('¿Está seguro de eliminar este detalle?')) {
    try {
      await api.deleteDetalleAccidente(detalle.accidente.numero, detalle.vehiculo.patente)
      await loadDetallesAccidente(selectedAccidente.value.numero)
    } catch (error) {
      console.error('Error deleting detalle:', error)
      alert('Error al eliminar el detalle')
    }
  }
}

const cancelDetalleForm = () => {
  showDetalleForm.value = false
  detalleForm.value = { vehiculo: null, descripcion: '' }
}

const closeDetallesModal = () => {
  showDetallesModal.value = false
  showDetalleForm.value = false
  selectedAccidente.value = null
  detallesAccidente.value = []
  detalleForm.value = { vehiculo: null, descripcion: '' }
}

const editAccidente = (accidente) => {
  form.value = { ...accidente }
  editingAccidente.value = true
  showModal.value = true
}

const saveAccidente = async () => {
  // Validar todos los campos
  validateNumero()
  validateFecha()
  validateLugar()
  
  if (!isFormValid.value) {
    return
  }
  
  try {
    apiError.value = ''
    if (editingAccidente.value) {
      await api.updateAccidente(form.value.numero, form.value)
    } else {
      await api.createAccidente(form.value)
    }
    closeModal()
    loadAccidentes()
  } catch (error) {
    console.error('Error saving accidente:', error)
    if (error.response?.data?.message) {
      apiError.value = error.response.data.message
    } else {
      apiError.value = 'Error al guardar el accidente. Verifique los datos.'
    }
  }
}

const deleteAccidente = async (numero) => {
  if (confirm('¿Está seguro de eliminar este accidente?')) {
    try {
      await api.deleteAccidente(numero)
      loadAccidentes()
    } catch (error) {
      console.error('Error deleting accidente:', error)
      alert('Error al eliminar el accidente')
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingAccidente.value = false
  apiError.value = ''
  form.value = { numero: '', fecha: '', lugar: '' }
  errors.value = { numero: '', fecha: '', lugar: '' }
}

onMounted(() => {
  loadAccidentes()
})
</script>