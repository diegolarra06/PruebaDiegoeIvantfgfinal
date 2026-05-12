<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { solicitudService } from '@/services/solicitudService'

const route = useRoute()
const router = useRouter()

const idSolicitud = route.params.idSolicitud
const historial = ref([])
const cargando = ref(true)
const error = ref('')

const cargarHistorial = async () => {
  cargando.value = true
  error.value = ''

  try {
    const resp = await solicitudService.historial(idSolicitud)
    historial.value = Array.isArray(resp.data) ? resp.data : []
  } catch (e) {
    console.error(e)
    error.value = 'No se pudo cargar el historial de la solicitud.'
  } finally {
    cargando.value = false
  }
}

const volver = () => {
  router.push('/mis-solicitudes')
}

onMounted(cargarHistorial)
</script>

<template>
  <div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="text-success mb-0">
        <i class="bi bi-clock-history"></i> Historial de solicitud
      </h1>

      <button class="btn btn-outline-secondary" @click="volver">
        <i class="bi bi-arrow-left"></i> Volver
      </button>
    </div>

    <div v-if="cargando" class="text-center my-4">
      <div class="spinner-border text-success"></div>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      {{ error }}
    </div>

    <div v-else-if="historial.length === 0" class="alert alert-info">
      Esta solicitud todavía no tiene historial.
    </div>

    <div v-else class="card shadow-sm">
      <div class="card-body">
        <table class="table table-hover align-middle">
          <thead class="table-success">
            <tr>
              <th>Fecha</th>
              <th>Estado anterior</th>
              <th>Estado nuevo</th>
              <th>Comentario</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="item in historial" :key="item.id">
              <td>{{ item.fechaCambio || item.fecha || '-' }}</td>
              <td>
                <span class="badge bg-secondary">
                  {{ item.estadoAnterior || item.estadoAnteriorNombre || '-' }}
                </span>
              </td>
              <td>
                <span class="badge bg-success">
                  {{ item.estadoNuevo || item.estadoNuevoNombre || '-' }}
                </span>
              </td>
              <td>{{ item.comentarioAdmin || item.comentario || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>