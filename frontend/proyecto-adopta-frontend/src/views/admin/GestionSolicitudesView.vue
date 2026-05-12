<script setup>
import { ref, onMounted } from 'vue'
import TablaSolicitudes from '@/components/TablaSolicitudes.vue'
import { solicitudService } from '@/services/solicitudService'

const solicitudes = ref([])
const cargando = ref(true)
const mostrarModal = ref(false)
const solicitudSeleccionada = ref(null)
const nuevoEstado = ref(1)
const comentario = ref('')

const ESTADOS = [
  { id: 1, nombre: 'EN_REVISION' },
  { id: 2, nombre: 'APROBADA' },
  { id: 3, nombre: 'RECHAZADA' },
  { id: 4, nombre: 'EN_PROCESO' },
  { id: 5, nombre: 'FINALIZADA' }
]

// =====================================================
// CARGAR SOLICITUDES
// =====================================================

const cargar = async () => {

  cargando.value = true

  try {

    const resp = await solicitudService.listarAdmin()

    solicitudes.value =
      Array.isArray(resp.data)
        ? resp.data
        : []

  } finally {

    cargando.value = false
  }
}

onMounted(cargar)

// =====================================================
// ABRIR MODAL
// =====================================================

const abrirModalCambioEstado = (s) => {

  solicitudSeleccionada.value = s

  nuevoEstado.value = s.idEstado

  comentario.value = ''

  mostrarModal.value = true
}

// =====================================================
// CONFIRMAR CAMBIO
// =====================================================

const confirmarCambio = async () => {

  try {

    await solicitudService.cambiarEstado(
      solicitudSeleccionada.value.idSolicitud,
      nuevoEstado.value,
      comentario.value
    )

    mostrarModal.value = false

    await cargar()

  } catch (e) {

    alert('Error al cambiar el estado.')
  }
}

// =====================================================
// VER HISTORIAL
// =====================================================

const verHistorial = (id) => {

  alert('Historial de la solicitud ' + id)
}

// =====================================================
// DESCARGAR PDF
// =====================================================

const descargarPdf = async (idSolicitud) => {

  try {

    const response = await fetch(
      `http://localhost:8080/solicitudes/pdf/${idSolicitud}`,
      {
        method: 'GET',

        credentials: 'include'
      }
    )

    if (!response.ok) {

      throw new Error('Error descargando PDF')
    }

    const blob = await response.blob()

    const url = window.URL.createObjectURL(blob)

    const a = document.createElement('a')

    a.href = url

    a.download = `solicitud_${idSolicitud}.pdf`

    document.body.appendChild(a)

    a.click()

    a.remove()

    window.URL.revokeObjectURL(url)

  } catch (error) {

    console.error(error)

    alert('No se pudo descargar el PDF')
  }
}
</script>

<template>

  <div class="container my-4">

    <!-- ================================================= -->
    <!-- TÍTULO -->
    <!-- ================================================= -->

    <h1 class="text-success">

      <i class="bi bi-clipboard-heart"></i>

      Gestión de solicitudes

    </h1>

    <!-- ================================================= -->
    <!-- SPINNER -->
    <!-- ================================================= -->

    <div
      v-if="cargando"
      class="text-center my-4"
    >

      <div class="spinner-border text-success"></div>

    </div>

    <!-- ================================================= -->
    <!-- CONTENIDO -->
    <!-- ================================================= -->

    <div v-else>

      <!-- ============================================= -->
      <!-- TABLA -->
      <!-- ============================================= -->

      <TablaSolicitudes
        :solicitudes="solicitudes"
        :modo-admin="true"
        @cambiar-estado="abrirModalCambioEstado"
        @ver-historial="verHistorial"
      />

      <!-- ============================================= -->
      <!-- PDFS -->
      <!-- ============================================= -->

      <div class="mt-4">

        <h4 class="mb-3">

          Descargar PDFs de solicitudes

        </h4>

        <div
          v-for="s in solicitudes"
          :key="s.idSolicitud"
          class="pdf-card"
        >

          <div>

            <strong>

              Solicitud #{{ s.idSolicitud }}

            </strong>

            <div>

              {{ s.nombreUsuario }}

              →

              {{ s.nombreAnimal }}

            </div>

          </div>

          <button
            class="btn-pdf"
            @click="descargarPdf(s.idSolicitud)"
          >

            <i class="bi bi-file-earmark-pdf"></i>

            Descargar PDF

          </button>

        </div>

      </div>

    </div>

    <!-- ================================================= -->
    <!-- MODAL -->
    <!-- ================================================= -->

    <div
      v-if="mostrarModal"
      class="modal-overlay"
      @click.self="mostrarModal = false"
    >

      <div class="modal-card">

        <h3>

          Cambiar estado de la solicitud

          #{{ solicitudSeleccionada.idSolicitud }}

        </h3>

        <p>

          {{ solicitudSeleccionada.nombreUsuario }}

          →

          {{ solicitudSeleccionada.nombreAnimal }}

        </p>

        <!-- =========================================== -->
        <!-- SELECT -->
        <!-- =========================================== -->

        <label class="form-label">

          Nuevo estado

        </label>

        <select
          v-model="nuevoEstado"
          class="form-select mb-3"
        >

          <option
            v-for="e in ESTADOS"
            :key="e.id"
            :value="e.id"
          >

            {{ e.nombre }}

          </option>

        </select>

        <!-- =========================================== -->
        <!-- COMENTARIO -->
        <!-- =========================================== -->

        <label class="form-label">

          Comentario para el adoptante

        </label>

        <textarea
          v-model="comentario"
          class="form-control"
          rows="3"
          placeholder="Este comentario llegará por email"
        ></textarea>

        <!-- =========================================== -->
        <!-- BOTONES -->
        <!-- =========================================== -->

        <div class="d-flex gap-2 mt-3">

          <button
            class="btn btn-secondary flex-fill"
            @click="mostrarModal = false"
          >

            Cancelar

          </button>

          <button
            class="btn-primario flex-fill"
            @click="confirmarCambio"
          >

            Confirmar cambio

          </button>

        </div>

      </div>

    </div>

  </div>

</template>

<style scoped>

/* ===================================================== */
/* MODAL */
/* ===================================================== */

.modal-overlay {

  position: fixed;

  inset: 0;

  background: rgba(0,0,0,0.5);

  display: flex;

  justify-content: center;

  align-items: center;

  z-index: 2000;
}

.modal-card {

  background: white;

  padding: 30px;

  border-radius: var(--radius);

  max-width: 500px;

  width: 90%;
}

/* ===================================================== */
/* PDF */
/* ===================================================== */

.pdf-card {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 15px;

  border: 1px solid #ddd;

  border-radius: 10px;

  margin-bottom: 10px;

  background: #fff;
}

.btn-pdf {

  background-color: #d32f2f;

  color: white;

  border: none;

  padding: 10px 14px;

  border-radius: 8px;

  cursor: pointer;

  font-weight: bold;

  transition: 0.2s;
}

.btn-pdf:hover {

  opacity: 0.9;

  transform: scale(1.03);
}

</style>