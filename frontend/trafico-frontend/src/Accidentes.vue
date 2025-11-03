<template>
  <div class="accidentes">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="text-primary">Gestión de Accidentes</h2>
      <button class="btn btn-primary" @click="showModal = true">
        <i class="bi bi-plus-circle"></i> Nuevo Accidente
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
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="accidente in accidentes" :key="accidente.numero">
                <td>{{ accidente.numero }}</td>
                <td>{{ formatDate(accidente.fecha) }}</td>
                <td>{{ accidente.lugar }}</td>
                <td>
                  <button class="btn btn-sm btn-outline-primary me-2" @click="editAccidente(accidente)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger" @click="deleteAccidente(accidente.numero)">
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
            <h5 class="modal-title">{{ editingAccidente ? 'Editar' : 'Nuevo' }} Accidente</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveAccidente">
              <div class="mb-3">
                <label class="form-label">Número</label>
                <input type="number" class="form-control" v-model="form.numero" :disabled="editingAccidente" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Fecha</label>
                <input type="date" class="form-control" v-model="form.fecha" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Lugar</label>
                <input type="text" class="form-control" v-model="form.lugar" required>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="saveAccidente">Guardar</button>
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

const accidentes = ref([])
const showModal = ref(false)
const editingAccidente = ref(false)
const form = ref({
  numero: '',
  fecha: '',
  lugar: ''
})

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

const editAccidente = (accidente) => {
  form.value = { ...accidente }
  editingAccidente.value = true
  showModal.value = true
}

const saveAccidente = async () => {
  try {
    if (editingAccidente.value) {
      await api.updateAccidente(form.value.numero, form.value)
    } else {
      await api.createAccidente(form.value)
    }
    closeModal()
    loadAccidentes()
  } catch (error) {
    console.error('Error saving accidente:', error)
  }
}

const deleteAccidente = async (numero) => {
  if (confirm('¿Está seguro de eliminar este accidente?')) {
    try {
      await api.deleteAccidente(numero)
      loadAccidentes()
    } catch (error) {
      console.error('Error deleting accidente:', error)
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingAccidente.value = false
  form.value = { numero: '', fecha: '', lugar: '' }
}

onMounted(() => {
  loadAccidentes()
})
</script>