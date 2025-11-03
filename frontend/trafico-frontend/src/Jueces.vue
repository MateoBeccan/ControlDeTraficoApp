<template>
  <div class="jueces">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="text-primary">Gestión de Jueces</h2>
      <button class="btn btn-primary" @click="showModal = true">
        <i class="bi bi-plus-circle"></i> Nuevo Juez
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead class="table-light">
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="juez in jueces" :key="juez.codigo">
                <td>{{ juez.codigo }}</td>
                <td>{{ juez.nombre }}</td>
                <td>
                  <button class="btn btn-sm btn-outline-primary me-2" @click="editJuez(juez)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger" @click="deleteJuez(juez.codigo)">
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
            <h5 class="modal-title">{{ editingJuez ? 'Editar' : 'Nuevo' }} Juez</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveJuez">
              <div class="mb-3">
                <label class="form-label">Código</label>
                <input type="number" class="form-control" v-model="form.codigo" :disabled="editingJuez" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Nombre</label>
                <input type="text" class="form-control" v-model="form.nombre" required>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="saveJuez">Guardar</button>
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

const jueces = ref([])
const showModal = ref(false)
const editingJuez = ref(false)
const form = ref({
  codigo: '',
  nombre: ''
})

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
  try {
    if (editingJuez.value) {
      await api.updateJuez(form.value.codigo, form.value)
    } else {
      await api.createJuez(form.value)
    }
    closeModal()
    loadJueces()
  } catch (error) {
    console.error('Error saving juez:', error)
  }
}

const deleteJuez = async (codigo) => {
  if (confirm('¿Está seguro de eliminar este juez?')) {
    try {
      await api.deleteJuez(codigo)
      loadJueces()
    } catch (error) {
      console.error('Error deleting juez:', error)
    }
  }
}

const closeModal = () => {
  showModal.value = false
  editingJuez.value = false
  form.value = { codigo: '', nombre: '' }
}

onMounted(() => {
  loadJueces()
})
</script>