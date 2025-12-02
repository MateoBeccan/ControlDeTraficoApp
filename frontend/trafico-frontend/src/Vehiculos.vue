<template>
  <div class="vehiculos">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Vehículos</h2>
          <button class="btn btn-primary btn-lg" @click="openNew">
            <i class="bi bi-plus-circle me-2"></i> Nuevo Vehículo
          </button>
        </div>
      </div>
    </div>

    <!-- Filtros -->
    <div class="row mb-3">
      <div class="col-12">
        <div class="card p-3">
          <div class="row g-2">
            <div class="col-12 col-md-3">
              <label class="form-label small mb-1">Patente</label>
              <input class="form-control" type="text" v-model="filters.patente" @input="onFilterInput" placeholder="Buscar por patente (ej: ABC-123)">
            </div>
            <div class="col-12 col-md-3">
              <label class="form-label small mb-1">Marca</label>
              <input class="form-control" type="text" v-model="filters.marca" @input="onFilterInput" placeholder="Buscar por marca">
            </div>
            <div class="col-12 col-md-4">
              <label class="form-label small mb-1">Propietario (DNI o Nombre)</label>
              <input class="form-control" type="text" v-model="filters.propietario" @input="onFilterInput" placeholder="Buscar por dni o nombre del propietario">
            </div>
            <div class="col-12 col-md-2 d-grid">
              <button class="btn btn-outline-secondary" @click="clearFilters">Limpiar filtros</button>
            </div>
          </div>
          <div class="small text-muted mt-2">Resultados: {{ filteredVehiculos.length }}</div>
        </div>
      </div>
    </div>

    <!-- Tabla -->
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
                  <tr v-for="vehiculo in filteredVehiculos" :key="vehiculo.patente">
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
                  <tr v-if="!filteredVehiculos.length">
                    <td colspan="4" class="text-center py-4 text-muted">No se encontraron vehículos</td>
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
                  @input="onPatenteInput"
                  @blur="validatePatente"
                  placeholder="Ej: ABC-123"
                  maxlength="7"
                  style="text-transform: uppercase"
                  required
                >
                <small v-if="errors.patente" class="text-danger">{{ errors.patente }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Marca *</label>
                <input
                  type="text"
                  class="form-control"
                  :class="{ 'is-invalid': errors.marca }"
                  v-model="form.marca"
                  @input="validateMarca"
                  @blur="validateMarca"
                  placeholder="Ej: Toyota"
                  maxlength="30"
                  required
                >
                <small v-if="errors.marca" class="text-danger">{{ errors.marca }}</small>
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
                <small v-if="errors.propietario" class="text-danger">{{ errors.propietario }}</small>
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

const filters = ref({
  patente: '',
  marca: '',
  propietario: ''
})

let filterDebounce = null

const isFormValid = computed(() => {
  return form.value.patente &&
         form.value.marca &&
         form.value.propietario &&
         !errors.value.patente &&
         !errors.value.marca &&
         !errors.value.propietario
})

const onPatenteInput = () => {
  form.value.patente = (form.value.patente || '').toUpperCase()
  validatePatente()
}

const validatePatente = () => {
  const patente = (form.value.patente || '').trim().toUpperCase()
  if (!patente) {
    errors.value.patente = 'La patente es obligatoria'
  } else if (!/^[A-Z]{3}-\d{3}$/.test(patente)) {
    errors.value.patente = 'La patente debe tener formato AAA-000 (3 letras + guión + 3 números)'
  } else {
    errors.value.patente = ''
  }
}

const validateMarca = () => {
  const marca = (form.value.marca || '').trim()
  if (!marca) {
    errors.value.marca = 'La marca es obligatoria'
  } else if (marca.length < 2 || marca.length > 30) {
    errors.value.marca = 'La marca debe tener entre 2 y 30 caracteres'
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
    vehiculos.value = response.data || []
  } catch (error) {
    console.error('Error loading vehiculos:', error)
  }
}

const loadPersonas = async () => {
  try {
    const response = await api.getPersonas()
    personas.value = response.data || []
  } catch (error) {
    console.error('Error loading personas:', error)
  }
}

const editVehiculo = (vehiculo) => {
  // Asegurarse de que propietario sea referencia a objeto persona en la lista (para select)
  const propietarioObj = vehiculo.propietario
    ? personas.value.find(p => String(p.dni) === String(vehiculo.propietario.dni)) || vehiculo.propietario
    : null

  form.value = {
    patente: vehiculo.patente,
    marca: vehiculo.marca,
    propietario: propietarioObj
  }
  errors.value = { patente: '', marca: '', propietario: '' }
  editingVehiculo.value = true
  showModal.value = true
}

const saveVehiculo = async () => {
  validatePatente()
  validateMarca()
  validatePropietario()
  if (!isFormValid.value) return

  try {
    apiError.value = ''
    const payload = {
      patente: form.value.patente,
      marca: form.value.marca,
      propietario: form.value.propietario
    }
    if (editingVehiculo.value) {
      await api.updateVehiculo(form.value.patente, payload)
    } else {
      await api.createVehiculo(payload)
    }
    await loadVehiculos()
    closeModal()
  } catch (error) {
    console.error('Error saving vehiculo:', error)
    apiError.value = error.response?.data?.message || 'Error al guardar el vehículo. Verifique los datos.'
  }
}

const deleteVehiculo = async (patente) => {
  if (confirm('¿Está seguro de eliminar este vehículo?')) {
    try {
      await api.deleteVehiculo(patente)
      await loadVehiculos()
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

/* Filtering logic (client-side with debounce) */
const onFilterInput = () => {
  clearTimeout(filterDebounce)
  filterDebounce = setTimeout(() => {
    // client-side filtering done in computed filteredVehiculos
    // if you want server-side filtering, call an API with params here
  }, 200)
}

const clearFilters = () => {
  filters.value = { patente: '', marca: '', propietario: '' }
}

/* computed filtered list */
const filteredVehiculos = computed(() => {
  const fPat = (filters.value.patente || '').trim().toLowerCase()
  const fMarca = (filters.value.marca || '').trim().toLowerCase()
  const fProp = (filters.value.propietario || '').trim().toLowerCase()

  if (!fPat && !fMarca && !fProp) return vehiculos.value

  return vehiculos.value.filter(v => {
    const patenteStr = (v.patente || '').toString().toLowerCase()
    const marcaStr = (v.marca || '').toString().toLowerCase()
    const propNombre = (v.propietario?.nombre || '').toString().toLowerCase()
    const propDni = (v.propietario?.dni || '').toString().toLowerCase()

    const patenteMatch = fPat ? patenteStr.includes(fPat) : true
    const marcaMatch = fMarca ? marcaStr.includes(fMarca) : true
    const propMatch = fProp ? (propNombre.includes(fProp) || propDni.includes(fProp)) : true

    return patenteMatch && marcaMatch && propMatch
  })
})

onMounted(async () => {
  await Promise.all([loadVehiculos(), loadPersonas()])
})
</script>

<style scoped>
.card.p-3 { padding: 1rem; }
.small.text-muted.mt-2 { margin-top: 0.5rem; }
</style>
