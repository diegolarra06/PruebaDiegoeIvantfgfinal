<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { authStore } from '@/stores/auth'
import { authService } from '@/services/authService'

const router = useRouter()
const menuAbierto = ref(false)

const toggleMenu = () => {
  menuAbierto.value = !menuAbierto.value
}

const cerrarMenu = () => {
  menuAbierto.value = false
}

const cerrarSesion = async () => {
  try {
    await authService.logout()
  } catch (e) {

  } finally {
    cerrarMenu()
    authStore.logout()
    router.push('/')
  }
}
</script>

<template>
  <header class="cabecera">
    <div class="logo">
      <RouterLink to="/" class="logo-link" @click="cerrarMenu">
        <i class="bi bi-heart-fill icon-logo"></i>
        <span>AdoptaUnCompañero</span>
      </RouterLink>
    </div>

    <button class="menu-toggle" :class="{ 'is-active': menuAbierto }" @click="toggleMenu" aria-label="Menú">
      <span class="line"></span>
      <span class="line"></span>
      <span class="line"></span>
    </button>

    <div class="nav-container" :class="{ 'is-open': menuAbierto }">
      <nav class="nav-principal">
        <RouterLink to="/" @click="cerrarMenu">Inicio</RouterLink>
        <RouterLink to="/catalogo" @click="cerrarMenu">Catálogo</RouterLink>
        <RouterLink to="/noticias" @click="cerrarMenu">Noticias</RouterLink>

        <template v-if="authStore.state.autenticado && authStore.esCliente()">
          <RouterLink to="/area-personal" @click="cerrarMenu">Mi Área</RouterLink>
          <RouterLink to="/favoritos" @click="cerrarMenu">Favoritos</RouterLink>
          <RouterLink to="/mis-solicitudes" @click="cerrarMenu">Mis Solicitudes</RouterLink>
        </template>

        <template v-if="authStore.esAdmin()">
          <RouterLink to="/admin" class="admin-link" @click="cerrarMenu">Panel Admin</RouterLink>
        </template>
      </nav>

      <div class="acciones">
        <template v-if="!authStore.state.autenticado">
          <RouterLink to="/login" class="btn-login" @click="cerrarMenu">Iniciar sesión</RouterLink>
          <RouterLink to="/registro" class="btn-registro" @click="cerrarMenu">Registrarse</RouterLink>
        </template>
        <template v-else>
          <div class="usuario-info">
            <span class="usuario-actual">
              <i class="bi bi-person-circle"></i>
              {{ authStore.state.email }}
            </span>
            <button @click="cerrarSesion" class="btn-logout">
              <i class="bi bi-box-arrow-right"></i> Salir
            </button>
          </div>
        </template>
      </div>
    </div>
  </header>

  <div v-if="menuAbierto" class="menu-overlay" @click="cerrarMenu"></div>
</template>

<style scoped>
.cabecera {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: var(--color-primario);
  color: white;
  padding: 0.8rem 5%;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  height: 70px;
}

.logo-link {
  color: white;
  font-size: 1.4rem;
  font-weight: 800;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 10px;
  letter-spacing: -0.5px;
}

.icon-logo {
  color: #ffeb3b;
  filter: drop-shadow(0 0 5px rgba(255, 235, 59, 0.5));
}

.nav-container {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.nav-principal {
  display: flex;
  gap: 8px;
}

.nav-principal a {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 0.95rem;
}

.nav-principal a:hover,
.nav-principal a.router-link-exact-active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  transform: translateY(-1px);
}

.admin-link {
  background-color: rgba(0, 0, 0, 0.2);
}


.acciones {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-login,
.btn-registro {
  padding: 8px 18px;
  border-radius: 50px;
  font-weight: 700;
  font-size: 0.9rem;
  transition: var(--transition);
}

.btn-login {
  background-color: white;
  color: var(--color-primario);
}

.btn-registro {
  background-color: #1e293b;
  color: white;
}

.btn-login:hover,
.btn-registro:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.usuario-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.usuario-actual {
  font-size: 0.85rem;
  background: rgba(0, 0, 0, 0.1);
  padding: 5px 12px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-logout {
  background: transparent;
  color: white;
  border: 1.5px solid rgba(255, 255, 255, 0.6);
  padding: 5px 15px;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: white;
  color: var(--color-primario);
}

.menu-toggle {
  display: none;
  flex-direction: column;
  gap: 6px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
}

.line {
  width: 28px;
  height: 3px;
  background-color: white;
  border-radius: 2px;
  transition: 0.3s;
}

@media (max-width: 1024px) {
  .menu-toggle {
    display: flex;
  }

  .nav-container {
    position: fixed;
    top: 0;
    right: -100%;
    width: 280px;
    height: 100vh;
    background-color: var(--color-primario);
    flex-direction: column;
    padding: 100px 30px;
    transition: 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: -10px 0 20px rgba(0, 0, 0, 0.1);
  }

  .nav-container.is-open {
    right: 0;
  }

  .nav-principal {
    flex-direction: column;
    width: 100%;
    gap: 15px;
  }

  .acciones {
    flex-direction: column;
    width: 100%;
    margin-top: 40px;
  }

  .btn-login,
  .btn-registro {
    width: 100%;
    text-align: center;
  }

  .is-active .line:nth-child(1) {
    transform: translateY(9px) rotate(45deg);
  }

  .is-active .line:nth-child(2) {
    opacity: 0;
  }

  .is-active .line:nth-child(3) {
    transform: translateY(-9px) rotate(-45deg);
  }
}

.menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  z-index: 999;
}
</style>