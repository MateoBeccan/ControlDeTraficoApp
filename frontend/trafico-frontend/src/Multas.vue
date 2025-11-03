<template>
  <div class="multas">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="text-primary">Gestión de Multas</h2>
      <button class="btn btn-primary" @click="showModal = true">
        <i class="bi bi-plus-circle"></i> Nueva Multa
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead class="table-light">
              <tr>
                <th>Número</th>
                <th>Fecha</th>
                <th>Lugar</th>
                <th>Juez</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="multa in multas" :key="multa.numero">
                <td>{{ multa.numero }}</td>
                <td>{{ formatDate(multa.fecha) }}</td>
                <td>{{ multa.lugar }}</td>
                <td>{{ multa.juez?.nombre || 'Sin juez' }}</td>
                <td>
                  <button class="btn btn-sm btn-outline-primary me-2" @click="editMulta(multa)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger" @click="deleteMulta(multa.numero)">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" :class="{ show: showModal }" :style="{ display: showModal ? 'block' : 'none' }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editingMulta ? 'Editar' : 'Nueva' }} Multa</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveMulta">
              <div class="mb-3">
                <label class="form-label">Número</label>
                <input type="number" class="form-control" v-model="form.numero" :disabled="editingMulta" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Fecha</label>
                <input type="date" class="form-control" v-model="form.fecha" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Lugar</label>
                <input type="text" class="form-control" v-model="form.lugar" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Juez</label>
                <select class="form-select" v-model="form.juez" required>
                  <option value="">Seleccione un juez</option>
                  <option v-for="juez in jueces" :key="juez.codigo" :value="juez">
                    {{ juez.nombre }}
                  </option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="saveMulta">Guardar</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from './api.js'

const multas = ref([])
const jueces = ref([])
const showModal = ref(false)
const editingMulta = ref(false)
const form = ref({
  numero: '',
  fecha: '',
  lugar: '',
  juez: null
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('es-ES')
}

const loadMultas = async () => {
  try {
    const response = await api.getMultas()
    multas.value = response.data
  } catch (error) {
    console.error('Error loading multas:', error)
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

const editMulta = (multa) => {
  form.value = { ...multa }
  editingMulta.value = true
  showModal.value = true
}

const saveMulta = async () => {
  try {
    if (editingMulta.value) {
      await api.updateMulta(form.value.numero, form.value)
    } else {
      await api.createMulta(form.value)
    }
    closeModal()
    loadMultas()
  } catch (error) {
    console.error('Error saving multa:', error)
  }
}

const deleteMulta = async (numero) => {
  if (confirm('¿Está seguro de eliminar esta multa?')) {
    try {
      await api.deleteMulta(numero)
      loadMultas()
    } catch (error) {
      console.error('Error deleting multa:', error)
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingMulta.value = false
  form.value = { numero: '', fecha: '', lugar: '', juez: null }
}

onMounted(() => {
  loadMultas()
  loadJueces()
})
</script>