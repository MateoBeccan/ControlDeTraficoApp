<template>
  <div class="accidentes">
    <!-- Encabezado y botón nuevo accidente -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Accidentes</h2>
          <button class="btn btn-primary btn-lg" @click="showModal = true">
            <i class="bi bi-plus-circle me-1"></i> Nuevo Accidente
          </button>
        </div>
      </div>
    </div>

    <!-- Filtros de búsqueda -->
    <div class="row mb-3">
      <div class="col-md-3 mb-2">
        <input type="text" class="form-control" placeholder="Número" v-model="filter.numero">
      </div>
      <div class="col-md-3 mb-2">
        <input type="text" class="form-control" placeholder="Lugar" v-model="filter.lugar">
      </div>
      <div class="col-md-3 mb-2">
        <input type="date" class="form-control" v-model="filter.fechaDesde" placeholder="Desde">
      </div>
      <div class="col-md-3 mb-2">
        <input type="date" class="form-control" v-model="filter.fechaHasta" placeholder="Hasta">
      </div>
    </div>

    <!-- Tabla de Accidentes -->
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                  <tr>
                    <th class="px-4 py-3" @click="sort('numero')" style="cursor: pointer">
                      Número
                      <i :class="sortIcon('numero')"></i>
                    </th>
                    <th class="px-4 py-3" @click="sort('fecha')" style="cursor: pointer">
                      Fecha
                      <i :class="sortIcon('fecha')"></i>
                    </th>
                    <th class="px-4 py-3 d-none d-md-table-cell" @click="sort('lugar')" style="cursor: pointer">
                      Lugar
                      <i :class="sortIcon('lugar')"></i>
                    </th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="accidente in sortedAccidentes" :key="accidente.numero">
                    <td class="px-4 py-3"><strong>{{ accidente.numero }}</strong></td>
                    <td class="px-4 py-3">
                      <div>
                        {{ formatDate(accidente.fecha) }}
                        <div class="d-md-none text-muted small">{{ accidente.lugar }}</div>
                      </div>
                      </td>
                    <td class="px-4 py-3 d-none d-md-table-cell">{{ accidente.lugar }}</td>
                    <td class="px-4 py-3 text-center">
                      <div class="d-flex justify-content-center">
                        <button
                          class="btn btn-info btn-sm me-2 d-flex align-items-center justify-content-center px-2"
                          @click="viewDetalles(accidente)">
                          <i class="bi bi-eye me-1"></i> Detalles
                        </button>
                        <button
                          class="btn btn-primary btn-sm me-2 d-flex align-items-center justify-content-center px-2"
                          @click="editAccidente(accidente)">
                          <i class="bi bi-pencil me-1"></i> Editar
                        </button>
                        <button
                          class="btn btn-danger btn-sm d-flex align-items-center justify-content-center px-2"
                          @click="deleteAccidente(accidente.numero)">
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

    <!-- Modal Crear / Editar Accidente -->
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
                <input type="text" class="form-control" :class="{ 'is-invalid': errors.numero }"
                       v-model="form.numero" :disabled="editingAccidente"
                       @input="validateNumero" @blur="validateNumero" placeholder="Ej: 12345" required>
                <small v-if="errors.numero" class="text-danger">{{ errors.numero }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Fecha *</label>
                <input type="date" class="form-control" :class="{ 'is-invalid': errors.fecha }"
                       v-model="form.fecha" @change="validateFecha" @blur="validateFecha"
                       :max="new Date().toISOString().split('T')[0]" required>
                <small v-if="errors.fecha" class="text-danger">{{ errors.fecha }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Lugar *</label>
                <input type="text" class="form-control" :class="{ 'is-invalid': errors.lugar }"
                       v-model="form.lugar" @input="validateLugar" @blur="validateLugar"
                       placeholder="Ej: Av. Corrientes y Callao" maxlength="100" required>
                <small v-if="errors.lugar" class="text-danger">{{ errors.lugar }}</small>
              </div>
            </form>
            <div v-if="apiError" class="alert alert-danger">{{ apiError }}</div>
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
              <div class="col-md-6"><strong>Fecha:</strong> {{ formatDate(selectedAccidente?.fecha) }}</div>
              <div class="col-md-6"><strong>Lugar:</strong> {{ selectedAccidente?.lugar }}</div>
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
                      <button class="btn btn-sm btn-outline-danger d-flex align-items-center" @click="deleteDetalle(detalle)">
                        <i class="bi bi-trash me-1"></i> Eliminar
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

// --- Datos ---
const accidentes = ref([])
const vehiculos = ref([])
const detallesAccidente = ref([])

// --- Estados UI ---
const showModal = ref(false)
const showDetallesModal = ref(false)
const showDetalleForm = ref(false)
const editingAccidente = ref(false)
const selectedAccidente = ref(null)
const apiError = ref('')

// --- Form Accidente ---
const form = ref({ numero: '', fecha: '', lugar: '' })
const detalleForm = ref({ vehiculo: null, descripcion: '' })
const errors = ref({ numero: '', fecha: '', lugar: '' })

// --- Filtros ---
const filter = ref({ numero: '', lugar: '', fechaDesde: '', fechaHasta: '' })

// --- Ordenamiento ---
const sortColumn = ref('numero')
const sortAsc = ref(true)

const isFormValid = computed(() => form.value.numero && form.value.fecha && form.value.lugar &&
  !errors.value.numero && !errors.value.fecha && !errors.value.lugar)

// --- Validaciones ---
const validateNumero = () => {
  const numero = form.value.numero?.toString() || ''
  errors.value.numero = !numero ? 'El número es obligatorio'
                     : !/^\d+$/.test(numero) ? 'El número debe ser numérico' : ''
}

const validateFecha = () => {
  if (!form.value.fecha) { errors.value.fecha = 'La fecha es obligatoria'; return }
  const fechaSeleccionada = new Date(form.value.fecha)
  const hoy = new Date(); hoy.setHours(23,59,59,999)
  errors.value.fecha = fechaSeleccionada > hoy ? 'La fecha no puede ser futura' : ''
}

const validateLugar = () => {
  const lugar = form.value.lugar?.trim() || ''
  errors.value.lugar = !lugar ? 'El lugar es obligatorio' : (lugar.length < 3 || lugar.length > 100) ? 'El lugar debe tener entre 3 y 100 caracteres' : ''
}

// --- Formateo ---
const formatDate = (dateString) => dateString ? new Date(dateString).toLocaleDateString('es-ES') : ''

// --- Filtrado ---
const filteredAccidentes = computed(() => {
  return accidentes.value.filter(acc => {
    const matchNumero = filter.value.numero ? acc.numero.toString().includes(filter.value.numero) : true
    const matchLugar = filter.value.lugar ? acc.lugar.toLowerCase().includes(filter.value.lugar.toLowerCase()) : true
    const accDate = new Date(acc.fecha)
    const desde = filter.value.fechaDesde ? new Date(filter.value.fechaDesde) : null
    const hasta = filter.value.fechaHasta ? new Date(filter.value.fechaHasta) : null
    return matchNumero && matchLugar && (!desde || accDate >= desde) && (!hasta || accDate <= hasta)
  })
})

// --- Ordenamiento ---
const sortedAccidentes = computed(() => {
  return [...filteredAccidentes.value].sort((a,b)=>{
    let valA = a[sortColumn.value], valB = b[sortColumn.value]
    if (sortColumn.value === 'fecha') { valA = new Date(valA); valB = new Date(valB) }
    if (valA < valB) return sortAsc.value ? -1 : 1
    if (valA > valB) return sortAsc.value ? 1 : -1
    return 0
  })
})
const sort = (col) => {
  if (sortColumn.value === col) sortAsc.value = !sortAsc.value
  else { sortColumn.value = col; sortAsc.value = true }
}
const sortIcon = (col) => {
  if (sortColumn.value !== col) return 'bi bi-chevron-expand'
  return sortAsc.value ? 'bi bi-chevron-up' : 'bi bi-chevron-down'
}

// --- Carga de datos ---
const loadAccidentes = async () => { accidentes.value = (await api.getAccidentes()).data }
const loadVehiculos = async () => { vehiculos.value = (await api.getVehiculos()).data }
const loadDetallesAccidente = async (numeroAccidente) => {
  detallesAccidente.value = (await api.getDetalleAccidentes()).data.filter(d => d.accidente?.numero == numeroAccidente)
}

// --- Acciones ---
const viewDetalles = async (accidente) => { selectedAccidente.value = accidente; await loadDetallesAccidente(accidente.numero); await loadVehiculos(); showDetallesModal.value = true }
const closeDetallesModal = () => { showDetallesModal.value = false; showDetalleForm.value = false; selectedAccidente.value = null; detallesAccidente.value = []; detalleForm.value = { vehiculo:null, descripcion:'' } }

const saveDetalle = async () => {
  if (!detalleForm.value.vehiculo || !detalleForm.value.descripcion) { alert('Debe completar todos los campos'); return }
  await api.createDetalleAccidente({ accidente:selectedAccidente.value, ...detalleForm.value })
  await loadDetallesAccidente(selectedAccidente.value.numero)
  cancelDetalleForm()
}
const deleteDetalle = async (detalle) => { if(confirm('¿Está seguro?')) { await api.deleteDetalleAccidente(detalle.accidente.numero, detalle.vehiculo.patente); await loadDetallesAccidente(selectedAccidente.value.numero) } }
const cancelDetalleForm = () => { showDetalleForm.value=false; detalleForm.value={ vehiculo:null, descripcion:'' } }

const editAccidente = (accidente) => { form.value = { ...accidente }; editingAccidente.value = true; showModal.value = true }
const saveAccidente = async () => {
  validateNumero(); validateFecha(); validateLugar()
  if(!isFormValid.value) return
  try {
    apiError.value = ''
    if(editingAccidente.value) await api.updateAccidente(form.value.numero, form.value)
    else await api.createAccidente(form.value)
    closeModal(); loadAccidentes()
  } catch(err) { apiError.value = err.response?.data?.message || 'Error al guardar el accidente' }
}
const deleteAccidente = async (numero) => { if(confirm('¿Está seguro?')) { await api.deleteAccidente(numero); loadAccidentes() } }
const closeModal = () => { showModal.value=false; editingAccidente.value=false; apiError.value=''; form.value={ numero:'', fecha:'', lugar:'' }; errors.value={ numero:'', fecha:'', lugar:'' } }

onMounted(() => { loadAccidentes() })
</script>
