<template>
  <div class="multas">
    <!-- Encabezado y botón nueva multa -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Multas</h2>
          <button class="btn btn-primary btn-lg" @click="openNewModal">+ Nueva Multa</button>
        </div>
      </div>
    </div>

    <!-- Filtros -->
    <div class="row mb-3">
      <div class="col-md-3 mb-2">
        <input type="text" class="form-control" placeholder="Buscar por número" v-model="filters.numero">
      </div>
      <div class="col-md-3 mb-2">
        <select class="form-select" v-model="filters.juezCodigo">
          <option value="">Todos los jueces</option>
          <option v-for="j in jueces" :key="j.codigo" :value="j.codigo">{{ j.apellido }}</option>
        </select>
      </div>
      <div class="col-md-3 mb-2">
        <input type="date" class="form-control" v-model="filters.fecha">
      </div>
      <div class="col-md-3 mb-2">
        <input type="text" class="form-control" placeholder="Buscar lugar" v-model="filters.lugar">
      </div>
    </div>

    <!-- Tabla de multas -->
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
                    <th class="px-4 py-3 d-none d-lg-table-cell">Lugar</th>
                    <th class="px-4 py-3 d-none d-md-table-cell">Juez</th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="multa in filteredMultas" :key="multa.numero">
                    <td class="px-4 py-3"><strong>{{ multa.numero }}</strong></td>
                    <td class="px-4 py-3">
                      {{ formatDate(multa.fecha) }}
                      <div class="d-lg-none text-muted small">{{ multa.lugar }}</div>
                      <div class="d-md-none text-muted small">Juez: {{ multa.juez?.apellido || 'Sin juez' }}</div>
                    </td>
                    <td class="px-4 py-3 d-none d-lg-table-cell">{{ multa.lugar }}</td>
                    <td class="px-4 py-3 d-none d-md-table-cell">{{ multa.juez?.apellido || 'Sin juez' }}</td>
                    <td class="px-4 py-3 text-center">
                      <button class="btn btn-info btn-sm me-1" @click="viewDetalles(multa)">Detalles</button>
                      <button class="btn btn-primary btn-sm me-1" @click="editMulta(multa)">Editar</button>
                      <button class="btn btn-danger btn-sm" @click="deleteMulta(multa.numero)">Eliminar</button>
                    </td>
                  </tr>
                  <tr v-if="filteredMultas.length === 0">
                    <td colspan="5" class="text-center text-muted py-3">No se encontraron multas</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para crear/editar multa -->
    <div class="modal fade" :class="{ show: showModal }" :style="{ display: showModal ? 'block' : 'none' }">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editingMulta ? 'Editar' : 'Nueva' }} Multa</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveMulta">
              <div class="mb-3">
                <label class="form-label">Número *</label>
                <input type="text" class="form-control" v-model="form.numero" :disabled="editingMulta" required>
              </div>

              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label">Fecha *</label>
                  <input type="date" class="form-control" v-model="form.fecha" :max="today" required>
                </div>
                <div class="col-md-4">
                  <label class="form-label">Lugar *</label>
                  <input type="text" class="form-control" v-model="form.lugar" placeholder="Ej: Av. Corrientes y Callao" required>
                </div>
                <div class="col-md-4">
                  <label class="form-label">Juez *</label>
                  <input type="text" class="form-control mb-2" placeholder="Filtrar jueces..." v-model="juezSearch">
                  <select class="form-select" v-model="form.juez" required>
                    <option v-for="j in filteredJueces" :key="j.codigo" :value="j">{{ j.apellido }}</option>
                  </select>
                </div>
              </div>

              <hr>

              <!-- Vehículo -->
              <div class="row mb-3">
                <div class="col-md-6">
                  <h6>Vehículo multado</h6>
                  <input type="text" class="form-control mb-2" placeholder="Buscar vehículo..." v-model="vehiculoSearch">
                  <select class="form-select" v-model="form.vehiculo" required>
                    <option v-for="v in filteredVehiculos" :key="v.patente" :value="v">{{ v.patente }} — {{ v.marca }}</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Descripción *</label>
                  <textarea class="form-control" rows="3" v-model="form.detalleDescripcion" placeholder="Descripción de la infracción"></textarea>
                </div>
              </div>
            </form>

            <div v-if="apiError" class="alert alert-danger mt-3">{{ apiError }}</div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-lg" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary btn-lg" @click="saveMulta">Guardar</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal-backdrop fade show"></div>

    <!-- Modal de detalles de multa -->
    <div class="modal fade" :class="{ show: showDetallesModal }" :style="{ display: showDetallesModal ? 'block' : 'none' }">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Detalles de la Multa {{ selectedMulta?.numero }}</h5>
            <button type="button" class="btn-close" @click="closeDetallesModal"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-4"><strong>Fecha:</strong> {{ formatDate(selectedMulta?.fecha) }}</div>
              <div class="col-md-4"><strong>Lugar:</strong> {{ selectedMulta?.lugar }}</div>
              <div class="col-md-4"><strong>Juez:</strong> {{ selectedMulta?.juez?.apellido }}</div>
            </div>
            <div class="table-responsive">
              <table class="table table-sm">
                <thead>
                  <tr><th>Vehículo</th><th>Propietario</th><th>Descripción</th></tr>
                </thead>
                <tbody>
                  <tr v-for="detalle in detallesMulta" :key="detalle.vehiculo?.patente">
                    <td>{{ detalle.vehiculo?.patente }} - {{ detalle.vehiculo?.marca }}</td>
                    <td>{{ detalle.vehiculo?.propietario?.nombre }} {{ detalle.vehiculo?.propietario?.apellido }}</td>
                    <td>{{ detalle.descripcion }}</td>
                  </tr>
                </tbody>
              </table>
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

const multas = ref([])
const jueces = ref([])
const detallesMulta = ref([])
const vehiculos = ref([])

const showModal = ref(false)
const showDetallesModal = ref(false)
const editingMulta = ref(false)
const selectedMulta = ref(null)
const apiError = ref('')

const form = ref({
  numero: '',
  fecha: '',
  lugar: '',
  juez: null,
  vehiculo: null,
  detalleDescripcion: ''
})

const filters = ref({
  numero: '',
  juezCodigo: '',
  fecha: '',
  lugar: ''
})

const vehiculoSearch = ref('')
const juezSearch = ref('')

const today = new Date().toISOString().split('T')[0]

const loadMultas = async () => {
  try { const r = await api.getMultas(); multas.value = r.data } catch (e) { console.error(e) }
}
const loadJueces = async () => {
  try { const r = await api.getJueces(); jueces.value = r.data } catch (e) { console.error(e) }
}
const loadVehiculos = async () => {
  try { const r = await api.getVehiculos(); vehiculos.value = r.data } catch (e) { console.error(e) }
}
const loadDetallesMulta = async (numeroMulta) => {
  try {
    const r = await api.getDetalleMultas()
    detallesMulta.value = r.data.filter(d => d.multa?.numero === numeroMulta)
  } catch (e) { console.error(e) }
}

const filteredMultas = computed(() => {
  return multas.value.filter(m => {
    const matchNumero = !filters.value.numero || String(m.numero).includes(filters.value.numero)
    const matchJuez = !filters.value.juezCodigo || (m.juez && m.juez.codigo == filters.value.juezCodigo)
    const matchFecha = !filters.value.fecha || m.fecha === filters.value.fecha
    const matchLugar = !filters.value.lugar || m.lugar.toLowerCase().includes(filters.value.lugar.toLowerCase())
    return matchNumero && matchJuez && matchFecha && matchLugar
  })
})

const filteredVehiculos = computed(() => {
  if (!vehiculoSearch.value) return vehiculos.value
  const q = vehiculoSearch.value.toLowerCase()
  return vehiculos.value.filter(v =>
    v.patente.toLowerCase().includes(q) ||
    v.marca.toLowerCase().includes(q) ||
    (v.propietario?.nombre + ' ' + (v.propietario?.apellido || '')).toLowerCase().includes(q)
  )
})

const filteredJueces = computed(() => {
  if (!juezSearch.value) return jueces.value
  const q = juezSearch.value.toLowerCase()
  return jueces.value.filter(j => j.apellido.toLowerCase().includes(q))
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('es-ES') : ''

const openNewModal = () => {
  showModal.value = true
  editingMulta.value = false
  form.value = { numero: '', fecha: '', lugar: '', juez: null, vehiculo: null, detalleDescripcion: '' }
}
const closeModal = () => { showModal.value = false; apiError.value = '' }

const viewDetalles = async (multa) => {
  selectedMulta.value = multa
  await loadDetallesMulta(multa.numero)
  showDetallesModal.value = true
}
const closeDetallesModal = () => { showDetallesModal.value = false; selectedMulta.value = null; detallesMulta.value = [] }

const editMulta = async (multa) => {
  selectedMulta.value = multa
  form.value.numero = multa.numero
  form.value.fecha = multa.fecha
  form.value.lugar = multa.lugar
  form.value.juez = multa.juez
  form.value.vehiculo = multa.vehiculo || null
  form.value.detalleDescripcion = multa.descripcion || ''
  editingMulta.value = true
  showModal.value = true
}

const saveMulta = async () => {
  try {
    const payload = {
      numero: form.value.numero,
      fecha: form.value.fecha,
      lugar: form.value.lugar,
      juez: form.value.juez
    }
    if (editingMulta.value) await api.updateMulta(payload.numero, payload)
    else await api.createMulta(payload)

    if (form.value.vehiculo) {
      await api.createDetalleMulta({
        multa: { numero: payload.numero },
        vehiculo: { patente: form.value.vehiculo.patente },
        descripcion: form.value.detalleDescripcion
      })
    }
    await loadMultas()
    closeModal()
  } catch (err) {
    apiError.value = err.response?.data?.message || err.message
  }
}

const deleteMulta = async (numero) => {
  if (!confirm('¿Está seguro de eliminar esta multa?')) return
  try { await api.deleteMulta(numero); await loadMultas() } catch (e) { alert('Error al eliminar la multa') }
}

onMounted(async () => { await Promise.all([loadMultas(), loadJueces(), loadVehiculos()]) })
</script>

