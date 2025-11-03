import { createRouter, createWebHistory } from 'vue-router'
import Login from '../Login.vue'
import Dashboard from '../Dashboard.vue'
import Personas from '../Personas.vue'
import Vehiculos from '../Vehiculos.vue'
import Accidentes from '../Accidentes.vue'
import Multas from '../Multas.vue'
import Jueces from '../Jueces.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/dashboard', component: Dashboard },
    { path: '/personas', component: Personas },
    { path: '/vehiculos', component: Vehiculos },
    { path: '/accidentes', component: Accidentes },
    { path: '/multas', component: Multas },
    { path: '/jueces', component: Jueces }
  ]
})

export default router
