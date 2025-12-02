<template>
  <div class="multas">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Multas</h2>
          <button class="btn btn-primary btn-lg" @click="openNewModal">+ Nueva Multa</button>
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
                    <th class="px-4 py-3 d-none d-lg-table-cell">Lugar</th>
                    <th class="px-4 py-3 d-none d-md-table-cell">Juez</th>
                    <th class="px-4 py-3 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="multa in multas" :key="multa.numero">
                    <td class="px-4 py-3"><strong>{{ multa.numero }}</strong></td>
                    <td class="px-4 py-3">
                      <div>
                        {{ formatDate(multa.fecha) }}
                        <div class="d-lg-none text-muted small">{{ multa.lugar }}</div>
                        <div class="d-md-none text-muted small">Juez: {{ multa.juez?.apellido || 'Sin juez' }}</div>
                      </div>
                    </td>
                    <td class="px-4 py-3 d-none d-lg-table-cell">{{ multa.lugar }}</td>
                    <td class="px-4 py-3 d-none d-md-table-cell">{{ multa.juez?.apellido || 'Sin juez' }}</td>
                    <td class="px-4 py-3 text-center">
                      <button class="btn btn-info btn-sm me-1" @click="viewDetalles(multa)">Detalles</button>
                      <button class="btn btn-primary btn-sm me-1" @click="editMulta(multa)">Editar</button>
                      <button class="btn btn-danger btn-sm" @click="deleteMulta(multa.numero)">Eliminar</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" :class="{ show: showModal }" :style="{ display: showModal ? 'block' : 'none' }">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editingMulta ? 'Editar' : 'Nueva' }} Multa</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveMulta">
              <div class="mb-4">
                <label class="form-label">Número *</label>
                <input type="text" class="form-control" :class="{ 'is-invalid': errors.numero }" v-model="form.numero" :disabled="editingMulta"
                  @input="validateNumero" @blur="validateNumero" placeholder="Ej: 12345" required>
                <small v-if="errors.numero" class="text-danger">{{ errors.numero }}</small>
              </div>

              <div class="row">
                <div class="col-md-4 mb-4">
                  <label class="form-label">Fecha *</label>
                  <input type="date" class="form-control" :class="{ 'is-invalid': errors.fecha }" v-model="form.fecha"
                    @change="validateFecha" @blur="validateFecha" :max="new Date().toISOString().split('T')[0]" required>
                  <small v-if="errors.fecha" class="text-danger">{{ errors.fecha }}</small>
                </div>
                <div class="col-md-4 mb-4">
                  <label class="form-label">Lugar *</label>
                  <input type="text" class="form-control" :class="{ 'is-invalid': errors.lugar }" v-model="form.lugar"
                    @input="validateLugar" @blur="validateLugar" placeholder="Ej: Av. Corrientes y Callao" maxlength="100" required>
                  <small v-if="errors.lugar" class="text-danger">{{ errors.lugar }}</small>
                </div>
                <div class="col-md-4 mb-4">
                  <label class="form-label">Juez *</label>
                  <select class="form-select" :class="{ 'is-invalid': errors.juez }" v-model="form.juez" @change="validateJuez" required>
                    <option value="">Seleccione un juez</option>
                    <option v-for="juez in jueces" :key="juez.codigo" :value="juez">{{ juez.apellido }}</option>
                  </select>
                  <small v-if="errors.juez" class="text-danger">{{ errors.juez }}</small>
                </div>
              </div>

              <hr>

              <div class="row">
                <div class="col-md-6">
                  <h6>Vehículo multado</h6>

                  <div class="mb-2 form-check">
                    <input class="form-check-input" type="radio" id="veh-existing" value="existing" v-model="vehiculoMode">
                    <label class="form-check-label" for="veh-existing">Elegir vehículo existente</label>
                  </div>

                  <div v-if="vehiculoMode === 'existing'" class="mb-3">
                    <input type="text" class="form-control" v-model="vehiculoSearch" @input="onVehiculoSearchInput" placeholder="Buscar vehículo (patente/marca/propietario)">
                    <div class="list-group position-relative" v-if="vehiculoResults.length">
                      <button type="button" class="list-group-item list-group-item-action" v-for="v in vehiculoResults" :key="v.patente"
                        @click="selectVehiculo(v)">{{ v.patente }} — {{ v.marca }} — {{ v.propietario?.nombre }} {{ v.propietario?.apellido || '' }}</button>
                    </div>
                    <div v-else class="small text-muted mt-1">No se encontraron vehículos</div>
                  </div>

                  <div class="mb-3 form-check">
                    <input class="form-check-input" type="radio" id="veh-new" value="new" v-model="vehiculoMode">
                    <label class="form-check-label" for="veh-new">Crear nuevo vehículo</label>
                  </div>

                  <div v-if="vehiculoMode === 'new'">
                    <div class="mb-3">
                      <label class="form-label">Patente *</label>
                      <input type="text" class="form-control" v-model="form.vehiculo.patente" @input="validatePatente" @blur="validatePatente"
                        :class="{ 'is-invalid': errors.patente }" placeholder="Ej: ABC123" maxlength="10">
                      <small v-if="errors.patente" class="text-danger">{{ errors.patente }}</small>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Marca *</label>
                      <input type="text" class="form-control" v-model="form.vehiculo.marca" @input="validateMarca" @blur="validateMarca"
                        :class="{ 'is-invalid': errors.marca }" placeholder="Ej: Toyota" maxlength="50">
                      <small v-if="errors.marca" class="text-danger">{{ errors.marca }}</small>
                    </div>

                    <div class="mb-2">
                      <label class="form-label">Propietario</label>
                    </div>

                    <div class="mb-2 form-check">
                      <input class="form-check-input" type="radio" id="prop-existing" value="existing" v-model="propietarioMode">
                      <label class="form-check-label" for="prop-existing">Elegir persona existente</label>
                    </div>

                    <div v-if="propietarioMode === 'existing'" class="mb-3">
                      <input type="text" class="form-control" v-model="personaSearch" @input="onPersonaSearchInput" placeholder="Buscar persona (dni/nombre)">
                      <div class="list-group position-relative" v-if="personaResults.length">
                        <button type="button" class="list-group-item list-group-item-action" v-for="p in personaResults" :key="p.dni"
                          @click="selectPersona(p)">{{ p.dni }} — {{ p.nombre }} {{ p.apellido || '' }}</button>
                      </div>
                      <div v-else class="small text-muted mt-1">No se encontraron personas</div>
                    </div>

                    <div class="mb-3 form-check">
                      <input class="form-check-input" type="radio" id="prop-new" value="new" v-model="propietarioMode">
                      <label class="form-check-label" for="prop-new">Crear nueva persona</label>
                    </div>

                    <div v-if="propietarioMode === 'new'">
                      <div class="row">
                        <div class="col-md-6 mb-3">
                          <label class="form-label">Nombre *</label>
                          <input type="text" class="form-control" v-model="form.vehiculo.propietario.nombre" @input="validatePropNombre" @blur="validatePropNombre"
                            :class="{ 'is-invalid': errors.propNombre }" maxlength="50" placeholder="Nombre">
                          <small v-if="errors.propNombre" class="text-danger">{{ errors.propNombre }}</small>
                        </div>
                        <div class="col-md-6 mb-3">
                          <label class="form-label">Apellido</label>
                          <input type="text" class="form-control" v-model="form.vehiculo.propietario.apellido" @input="validatePropApellido" @blur="validatePropApellido"
                            :class="{ 'is-invalid': errors.propApellido }" maxlength="50" placeholder="Apellido">
                          <small v-if="errors.propApellido" class="text-danger">{{ errors.propApellido }}</small>
                        </div>
                      </div>
                      <div class="mb-3">
                        <label class="form-label">DNI *</label>
                        <input type="text" class="form-control" v-model="form.vehiculo.propietario.dni" @input="validatePropDni" @blur="validatePropDni"
                          :class="{ 'is-invalid': errors.propDni }" maxlength="12" placeholder="Ej: 30123456">
                        <small v-if="errors.propDni" class="text-danger">{{ errors.propDni }}</small>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="col-md-6">
                  <h6>Vista previa</h6>
                  <div class="border rounded p-3">
                    <p><strong>Vehículo seleccionado:</strong>
                      <span v-if="vehiculoMode === 'existing'">{{ selectedVehiculoPreview || '—' }}</span>
                      <span v-else>{{ form.vehiculo.patente || '—' }} — {{ form.vehiculo.marca || '—' }}</span>
                    </p>
                    <p><strong>Propietario:</strong>
                      <span v-if="vehiculoMode === 'existing'">{{ selectedVehiculoOwner || '—' }}</span>
                      <span v-else>
                        <span v-if="propietarioMode === 'existing'">{{ selectedPersonaPreview || '—' }}</span>
                        <span v-else>{{ form.vehiculo.propietario.nombre || '—' }} {{ form.vehiculo.propietario.apellido || '' }} (DNI: {{ form.vehiculo.propietario.dni || '—' }})</span>
                      </span>
                    </p>

                    <div class="mb-3">
                      <label class="form-label">Descripción (detalle multa)</label>
                      <textarea class="form-control" v-model="form.detalleDescripcion" rows="3" placeholder="Descripción de la infracción (opcional)"></textarea>
                    </div>

                    <p class="small text-muted">Al guardar se crearán/asegurarán las entidades y se vinculará la multa mediante detalle-multas.</p>
                  </div>
                </div>
              </div>
            </form>

            <div v-if="apiError" class="alert alert-danger mt-3">{{ apiError }}</div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-lg" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary btn-lg" @click="saveMulta" :disabled="!isFormValid">Guardar</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal-backdrop fade show"></div>

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

            <div class="mb-3"><h6>Vehículos Multados</h6></div>

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
const personas = ref([])

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
  vehiculo: { patente: '', marca: '', propietario: { nombre: '', apellido: '', dni: '' } },
  detalleDescripcion: ''
})

const vehiculoMode = ref('existing')
const propietarioMode = ref('existing')
const selectedVehiculoPatente = ref('')
const selectedPersonaDni = ref('')

const errors = ref({
  numero: '', fecha: '', lugar: '', juez: '', patente: '', marca: '', propNombre: '', propApellido: '', propDni: ''
})

const vehiculoSearch = ref('')
const personaSearch = ref('')
const vehiculoResults = ref([])
const personaResults = ref([])
let vehDebounce = null
let persDebounce = null

const selectedVehiculoPreview = ref(null)
const selectedVehiculoOwner = ref(null)
const selectedPersonaPreview = ref(null)

const isFormValid = computed(() => {
  const baseOk = form.value.numero && form.value.fecha && form.value.lugar && form.value.juez && !errors.value.numero && !errors.value.fecha && !errors.value.lugar && !errors.value.juez
  if (!baseOk) return false
  if (vehiculoMode.value === 'existing') return !!selectedVehiculoPatente.value
  const vehOk = form.value.vehiculo?.patente && form.value.vehiculo?.marca && !errors.value.patente && !errors.value.marca
  if (!vehOk) return false
  if (propietarioMode.value === 'existing') return !!selectedPersonaDni.value
  return form.value.vehiculo.propietario.nombre && form.value.vehiculo.propietario.dni && !errors.value.propNombre && !errors.value.propDni
})

const validateNumero = () => {
  const numero = form.value.numero?.toString() || ''
  if (!numero) errors.value.numero = 'El número es obligatorio'
  else if (!/^\d+$/.test(numero)) errors.value.numero = 'El número debe ser numérico'
  else errors.value.numero = ''
}

const validateFecha = () => {
  if (!form.value.fecha) errors.value.fecha = 'La fecha es obligatoria'
  else {
    const fechaSeleccionada = new Date(form.value.fecha)
    const hoy = new Date(); hoy.setHours(23, 59, 59, 999)
    if (fechaSeleccionada > hoy) errors.value.fecha = 'La fecha no puede ser futura'
    else errors.value.fecha = ''
  }
}

const validateLugar = () => {
  const lugar = form.value.lugar?.trim() || ''
  if (!lugar) errors.value.lugar = 'El lugar es obligatorio'
  else if (lugar.length < 3 || lugar.length > 100) errors.value.lugar = 'El lugar debe tener entre 3 y 100 caracteres'
  else errors.value.lugar = ''
}

const validateJuez = () => {
  if (!form.value.juez) errors.value.juez = 'El juez es obligatorio'
  else errors.value.juez = ''
}

const validatePatente = () => {
  const p = (form.value.vehiculo?.patente || '').trim().toUpperCase()
  if (!p) errors.value.patente = 'La patente es obligatoria'
  else if (!/^[A-Z0-9\-]{4,10}$/.test(p)) errors.value.patente = 'Patente inválida. Use solo letras, números y guión, 4-10 caracteres'
  else { errors.value.patente = ''; form.value.vehiculo.patente = p }
}

const validateMarca = () => {
  const m = (form.value.vehiculo?.marca || '').trim()
  if (!m) errors.value.marca = 'La marca es obligatoria'
  else if (m.length < 2) errors.value.marca = 'La marca es demasiado corta'
  else errors.value.marca = ''
}

const validatePropNombre = () => {
  const n = (form.value.vehiculo?.propietario?.nombre || '').trim()
  if (!n) errors.value.propNombre = 'El nombre es obligatorio'
  else errors.value.propNombre = ''
}

const validatePropApellido = () => {
  const a = (form.value.vehiculo?.propietario?.apellido || '').trim()
  if (!a) errors.value.propApellido = 'El apellido es obligatorio'
  else errors.value.propApellido = ''
}

const validatePropDni = () => {
  const d = (form.value.vehiculo?.propietario?.dni || '').trim()
  if (!d) errors.value.propDni = 'El DNI es obligatorio'
  else if (!/^\d{6,12}$/.test(d)) errors.value.propDni = 'DNI inválido (solo números, 6-12 dígitos)'
  else errors.value.propDni = ''
}

const formatDate = (dateString) => dateString ? new Date(dateString).toLocaleDateString('es-ES') : ''

const loadMultas = async () => { try { const r = await api.getMultas(); multas.value = r.data } catch (e) { console.error(e) } }
const loadJueces = async () => { try { const r = await api.getJueces(); jueces.value = r.data } catch (e) { console.error(e) } }
const loadVehiculos = async () => { try { const r = await api.getVehiculos(); vehiculos.value = r.data } catch (e) { console.error(e) } }
const loadPersonas = async () => { try { const r = await api.getPersonas(); personas.value = r.data } catch (e) { console.error(e) } }
const loadDetallesMulta = async (numeroMulta) => { try { const r = await api.getDetalleMultas(); detallesMulta.value = r.data.filter(d => d.multa?.numero == numeroMulta) } catch (e) { console.error(e) } }

const openNewModal = () => { closeModal(); vehiculoMode.value = 'existing'; propietarioMode.value = 'existing'; showModal.value = true }
const closeModal = () => {
  showModal.value = false; editingMulta.value = false; apiError.value = ''; selectedVehiculoPatente.value = ''; selectedPersonaDni.value = ''
  form.value = { numero: '', fecha: '', lugar: '', juez: null, vehiculo: { patente: '', marca: '', propietario: { nombre: '', apellido: '', dni: '' } }, detalleDescripcion: '' }
  errors.value = { numero: '', fecha: '', lugar: '', juez: '', patente: '', marca: '', propNombre: '', propApellido: '', propDni: '' }
  vehiculoSearch.value = ''; personaSearch.value = ''; vehiculoResults.value = []; personaResults.value = []
  selectedVehiculoPreview.value = null; selectedVehiculoOwner.value = null; selectedPersonaPreview.value = null
}

const selectVehiculo = (v) => {
  selectedVehiculoPatente.value = v.patente
  vehiculoMode.value = 'existing'
  selectedVehiculoPreview.value = `${v.patente} — ${v.marca}`
  selectedVehiculoOwner.value = v.propietario ? `${v.propietario.nombre} ${v.propietario.apellido || ''} (DNI: ${v.propietario.dni})` : '—'
  vehiculoResults.value = []; vehiculoSearch.value = ''
}

const selectPersona = (p) => {
  selectedPersonaDni.value = p.dni
  propietarioMode.value = 'existing'
  selectedPersonaPreview.value = `${p.nombre} ${p.apellido || ''} (DNI: ${p.dni})`
  personaResults.value = []; personaSearch.value = ''
}

const editMulta = async (multa) => {
  try {
    const resp = await api.getMulta(multa.numero)
    const full = resp.data
    form.value.numero = full.numero
    form.value.fecha = full.fecha
    form.value.lugar = full.lugar
    form.value.juez = full.juez || null
    await loadVehiculos(); await loadPersonas()
    const detallesResp = await api.getDetalleMultas()
    const asociados = detallesResp.data.filter(d => d.multa?.numero === full.numero)
    if (asociados.length > 0 && asociados[0].vehiculo) {
      selectedVehiculoPatente.value = asociados[0].vehiculo.patente
      vehiculoMode.value = 'existing'
      selectedVehiculoPreview.value = getVehiculoPreviewSync(selectedVehiculoPatente.value)
      selectedVehiculoOwner.value = getVehiculoOwnerSync(selectedVehiculoPatente.value)
      form.detalleDescripcion = asociados[0].descripcion || ''
    } else {
      vehiculoMode.value = 'new'
      form.detalleDescripcion = ''
    }
    errors.value = { numero: '', fecha: '', lugar: '', juez: '', patente: '', marca: '', propNombre: '', propApellido: '', propDni: '' }
    editingMulta.value = true; showModal.value = true
  } catch (e) { apiError.value = 'Error cargando multa para editar' }
}

const saveMulta = async () => {
  validateNumero(); validateFecha(); validateLugar(); validateJuez()
  if (vehiculoMode.value === 'new') { validatePatente(); validateMarca(); if (propietarioMode.value === 'new') { validatePropNombre(); validatePropDni() } }
  if (!isFormValid.value) return
  apiError.value = ''
  try {
    let personaObj = null
    if (vehiculoMode.value === 'new' && propietarioMode.value === 'new') {
      const personaPayload = { dni: parseInt(form.value.vehiculo.propietario.dni), nombre: form.value.vehiculo.propietario.nombre, direccion: form.value.vehiculo.propietario.direccion || null }
      const rPersona = await api.createPersona(personaPayload); personaObj = rPersona.data; await loadPersonas()
    } else if (vehiculoMode.value === 'new' && propietarioMode.value === 'existing') {
      personaObj = personas.value.find(p => String(p.dni) === String(selectedPersonaDni.value))
      if (!personaObj) throw new Error('Persona seleccionada no encontrada')
    }
    let vehiculoObj = null
    if (vehiculoMode.value === 'new') {
      const vehPayload = { patente: (form.value.vehiculo.patente || '').toString().toUpperCase(), marca: form.value.vehiculo.marca, propietario: personaObj }
      const rVeh = await api.createVehiculo(vehPayload); vehiculoObj = rVeh.data; await loadVehiculos()
    } else {
      vehiculoObj = vehiculos.value.find(v => v.patente === selectedVehiculoPatente.value)
      if (!vehiculoObj) throw new Error('Vehículo seleccionado no encontrado')
    }
    const multaPayload = { numero: parseInt(form.value.numero), fecha: form.value.fecha, lugar: form.value.lugar, juez: form.value.juez }
    if (editingMulta.value) await api.updateMulta(multaPayload.numero, multaPayload)
    else await api.createMulta(multaPayload)
    const detallePayload = { multa: { numero: multaPayload.numero }, vehiculo: { patente: vehiculoObj.patente }, descripcion: form.value.detalleDescripcion || `Asociada desde UI por ${vehiculoObj.patente}` }
    await api.createDetalleMulta(detallePayload)
    await loadMultas(); closeModal()
  } catch (error) {
    apiError.value = error.response?.data?.message || error.message || 'Error al guardar la multa. Verifique los datos.'
  }
}

const deleteMulta = async (numero) => {
  if (confirm('¿Está seguro de eliminar esta multa?')) {
    try { await api.deleteMulta(numero); loadMultas() } catch (e) { alert('Error al eliminar la multa') }
  }
}

const viewDetalles = async (multa) => { selectedMulta.value = multa; await loadDetallesMulta(multa.numero); showDetallesModal.value = true }
const closeDetallesModal = () => { showDetallesModal.value = false; selectedMulta.value = null; detallesMulta.value = [] }

const onVehiculoSearchInput = () => {
  clearTimeout(vehDebounce)
  vehDebounce = setTimeout(async () => {
    const q = vehiculoSearch.value.trim().toLowerCase()
    if (!q) { vehiculoResults.value = []; return }
    // first try server-side search if backend supports query param
    try {
      const r = await api.getVehiculos(q)
      if (Array.isArray(r.data) && r.data.length) { vehiculoResults.value = r.data; return }
      // if server returned empty, fall back to client-side filter
    } catch (_) {}
    // client-side filter by patente, marca or propietario name
    vehiculoResults.value = vehiculos.value.filter(v => {
      const patente = (v.patente || '').toString().toLowerCase()
      const marca = (v.marca || '').toString().toLowerCase()
      const propName = ((v.propietario?.nombre || '') + ' ' + (v.propietario?.apellido || '')).toLowerCase()
      return patente.includes(q) || marca.includes(q) || propName.includes(q)
    }).slice(0, 50)
  }, 250)
}

const onPersonaSearchInput = () => {
  clearTimeout(persDebounce)
  persDebounce = setTimeout(async () => {
    const q = personaSearch.value.trim().toLowerCase()
    if (!q) { personaResults.value = []; return }
    try {
      const r = await api.getPersonas(q)
      if (Array.isArray(r.data) && r.data.length) { personaResults.value = r.data; return }
    } catch (_) {}
    personaResults.value = personas.value.filter(p => {
      const dni = (p.dni || '').toString().toLowerCase()
      const name = ((p.nombre || '') + ' ' + (p.apellido || '')).toLowerCase()
      return dni.includes(q) || name.includes(q)
    }).slice(0, 50)
  }, 250)
}

const getVehiculoPreviewSync = (patente) => { const v = vehiculos.value.find(x => x.patente === patente); return v ? `${v.patente} — ${v.marca}` : null }
const getVehiculoOwnerSync = (patente) => { const v = vehiculos.value.find(x => x.patente === patente); return v && v.propietario ? `${v.propietario.nombre} ${v.propietario.apellido || ''} (DNI: ${v.propietario.dni})` : '—' }

onMounted(async () => { await Promise.all([loadMultas(), loadJueces(), loadVehiculos(), loadPersonas()]) })
</script>
