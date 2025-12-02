<template>
  <div class="personas">
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center">
          <h2 class="text-primary mb-3 mb-md-0">Gestión de Personas</h2>
          <button class="btn btn-primary btn-lg" @click="openNew">
            <i class="bi bi-plus-circle me-2"></i> Nueva Persona
          </button>
        </div>
      </div>
    </div>

    <div class="row mb-3">
      <div class="col-12">
        <div class="card p-3">
          <div class="row g-2 align-items-end">
            <div class="col-12 col-md-3">
              <label class="form-label small mb-1">DNI</label>
              <input class="form-control" type="text" v-model="filters.dni" @input="onFilterInput" placeholder="Buscar por DNI (exacto o parcial)">
            </div>
            <div class="col-12 col-md-4">
              <label class="form-label small mb-1">Nombre</label>
              <input class="form-control" type="text" v-model="filters.nombre" @input="onFilterInput" placeholder="Buscar por nombre">
            </div>
            <div class="col-12 col-md-4">
              <label class="form-label small mb-1">Dirección</label>
              <input class="form-control" type="text" v-model="filters.direccion" @input="onFilterInput" placeholder="Buscar por dirección">
            </div>
            <div class="col-12 col-md-1 d-grid">
              <button class="btn btn-outline-secondary" @click="clearFilters" title="Limpiar filtros">Limpiar</button>
            </div>
          </div>
          <div class="small text-muted mt-2">Resultados: {{ filteredPersonas.length }}</div>
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
                  <tr v-for="persona in filteredPersonas" :key="persona.dni">
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
                  <tr v-if="!filteredPersonas.length">
                    <td colspan="4" class="text-center py-4 text-muted">No se encontraron personas</td>
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
                  type="text"
                  class="form-control"
                  :class="{ 'is-invalid': errors.dni }"
                  v-model="form.dni"
                  :disabled="editingPersona"
                  @input="validateDni"
                  @blur="validateDni"
                  placeholder="Ej: 12345678"
                  maxlength="8"
                  required
                >
                <small v-if="errors.dni" class="text-danger">{{ errors.dni }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Nombre *</label>
                <input
                  type="text"
                  class="form-control"
                  :class="{ 'is-invalid': errors.nombre }"
                  v-model="form.nombre"
                  @input="validateNombre"
                  @blur="validateNombre"
                  placeholder="Ej: Juan Pérez"
                  maxlength="50"
                  required
                >
                <small v-if="errors.nombre" class="text-danger">{{ errors.nombre }}</small>
              </div>
              <div class="mb-4">
                <label class="form-label">Dirección *</label>
                <input
                  type="text"
                  class="form-control"
                  :class="{ 'is-invalid': errors.direccion }"
                  v-model="form.direccion"
                  @input="validateDireccion"
                  @blur="validateDireccion"
                  placeholder="Ej: Av. Corrientes 1234"
                  maxlength="100"
                  required
                >
                <small v-if="errors.direccion" class="text-danger">{{ errors.direccion }}</small>
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

const filters = ref({
  dni: '',
  nombre: '',
  direccion: ''
})

let filterDebounce = null

const isFormValid = computed(() => {
  return form.value.dni &&
         form.value.nombre &&
         form.value.direccion &&
         !errors.value.dni &&
         !errors.value.nombre &&
         !errors.value.direccion
})

const validateDni = () => {
  const dni = form.value.dni?.toString() || ''
  if (!dni) errors.value.dni = 'El DNI es obligatorio'
  else if (!/^\d+$/.test(dni)) errors.value.dni = 'El DNI debe ser numérico'
  else if (dni.length < 7 || dni.length > 8) errors.value.dni = 'El DNI debe tener entre 7 y 8 dígitos'
  else errors.value.dni = ''
}

const validateNombre = () => {
  const nombre = form.value.nombre?.trim() || ''
  if (!nombre) errors.value.nombre = 'El nombre es obligatorio'
  else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(nombre)) errors.value.nombre = 'El nombre solo puede contener letras y espacios'
  else if (nombre.length < 3 || nombre.length > 50) errors.value.nombre = 'El nombre debe tener entre 3 y 50 caracteres'
  else errors.value.nombre = ''
}

const validateDireccion = () => {
  const direccion = form.value.direccion?.trim() || ''
  if (!direccion) errors.value.direccion = 'La dirección es obligatoria'
  else if (direccion.length < 5 || direccion.length > 100) errors.value.direccion = 'La dirección debe tener entre 5 y 100 caracteres'
  else errors.value.direccion = ''
}

const loadPersonas = async () => {
  try {
    const response = await api.getPersonas()
    personas.value = response.data || []
  } catch (error) {
    console.error('Error loading personas:', error)
  }
}

const openNew = () => {
  form.value = { dni: '', nombre: '', direccion: '' }
  errors.value = { dni: '', nombre: '', direccion: '' }
  editingPersona.value = false
  showModal.value = true
}

const editPersona = (persona) => {
  form.value = { ...persona }
  editingPersona.value = true
  showModal.value = true
}

const savePersona = async () => {
  validateDni()
  validateNombre()
  validateDireccion()
  if (!isFormValid.value) return
  try {
    apiError.value = ''
    if (editingPersona.value) {
      await api.updatePersona(form.value.dni, form.value)
    } else {
      await api.createPersona(form.value)
    }
    closeModal()
    await loadPersonas()
  } catch (error) {
    console.error('Error saving persona:', error)
    apiError.value = error.response?.data?.message || 'Error al guardar la persona. Verifique los datos.'
  }
}

const deletePersona = async (dni) => {
  if (confirm('¿Está seguro de eliminar esta persona?')) {
    try {
      await api.deletePersona(dni)
      await loadPersonas()
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

/* Filtering logic */
const onFilterInput = () => {
  clearTimeout(filterDebounce)
  filterDebounce = setTimeout(() => {
    // If you want server-side filtering, call an endpoint with query params here.
    // For now we filter client-side using the loaded array (works well for moderately sized datasets).
    // If dataset is huge, we should implement: api.getPersonas({ params: { dni, nombre, direccion } })
    // and the backend should support those query params.
  }, 250)
}

const clearFilters = () => {
  filters.value = { dni: '', nombre: '', direccion: '' }
}

/* computed filtered list */
const filteredPersonas = computed(() => {
  const fDni = (filters.value.dni || '').toString().trim().toLowerCase()
  const fNombre = (filters.value.nombre || '').trim().toLowerCase()
  const fDireccion = (filters.value.direccion || '').trim().toLowerCase()

  if (!fDni && !fNombre && !fDireccion) return personas.value

  return personas.value.filter(p => {
    const dniStr = (p.dni || '').toString().toLowerCase()
    const nombreStr = (p.nombre || '').toLowerCase()
    const direccionStr = (p.direccion || '').toLowerCase()

    const dniMatch = fDni ? dniStr.includes(fDni) : true
    const nombreMatch = fNombre ? nombreStr.includes(fNombre) : true
    const direccionMatch = fDireccion ? direccionStr.includes(fDireccion) : true

    return dniMatch && nombreMatch && direccionMatch
  })
})

onMounted(() => {
  loadPersonas()
})
</script>

<style scoped>
/* conservé tus estilos previos y añadí pequeños ajustes para el área de filtros */
.card.p-3 { padding: 1rem; }
.small.text-muted.mt-2 { margin-top: 0.5rem; }
</style>
