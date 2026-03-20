<template>
  <div class="shell">
    <header class="topbar">
      <div class="brand" @click="goHome">LocalGuide</div>
      <nav class="nav-links">
        <router-link v-if="showTouristNav" to="/">Home</router-link>
        <router-link v-if="showTouristNav" to="/search">Search</router-link>

        <router-link v-if="isTourist" to="/profile">Profile</router-link>
        <router-link v-if="authStore.accessToken && showTouristNav" to="/bookings">My Bookings</router-link>
        <router-link v-if="authStore.accessToken && showTouristNav" to="/favorites">Favorites</router-link>
        <router-link v-if="authStore.accessToken && showTouristNav" to="/reviews">Reviews</router-link>

        <router-link v-if="isGuide" to="/guide/dashboard">Dashboard</router-link>
        <router-link v-if="isGuide" to="/guide/tours">My Tours</router-link>
        <router-link v-if="isGuide" to="/guide/availability">Availability</router-link>
        <router-link v-if="authStore.user?.role === 'GUIDE'" to="/guide-verification">Guide Verification</router-link>

        <router-link v-if="authStore.user?.role === 'ADMIN'" to="/admin/dashboard">Admin Dashboard</router-link>
        <router-link v-if="authStore.user?.role === 'ADMIN'" to="/admin/verifications">Verifications</router-link>
        <router-link v-if="authStore.user?.role === 'ADMIN'" to="/admin/users">Users</router-link>

        <router-link v-if="isGuest" to="/login">Login</router-link>
        <router-link v-if="isGuest" to="/register">Register</router-link>
        <span v-if="authStore.user" class="role-tag">{{ authStore.user.role }}</span>
        <button v-if="authStore.accessToken" class="logout" @click="logout">Logout</button>
      </nav>
    </header>

    <main class="content">
      <slot />
    </main>

    <footer class="footer">
      <span>LocalGuide Connect</span>
      <span>Dev1 auth-user · Dev2 guide-tour · Dev3 booking-payment · Dev4 review-admin</span>
    </footer>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();

const isGuide = computed(() => authStore.user?.role === "GUIDE");
const isLoggedIn = computed(() => Boolean(authStore.accessToken));
const isGuest = computed(() => !isLoggedIn.value);
const isTourist = computed(() => isLoggedIn.value && authStore.user?.role !== "GUIDE");
const showTouristNav = computed(() => !isGuide.value);

const goHome = () => {
  if (authStore.user?.role === "ADMIN") {
    router.push("/admin/dashboard");
  } else if (isGuide.value) {
    router.push("/guide/dashboard");
  } else if (authStore.accessToken) {
    router.push("/");
  } else {
    router.push("/login");
  }
};

const logout = async () => {
  await authStore.logout();
  router.push("/login");
};
</script>

<style scoped>
.shell {
  min-height: 100vh;
  display: grid;
  grid-template-rows: auto 1fr auto;
  background: linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%);
}

.topbar {
  min-height: 64px;
  background: #0f172a;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  gap: 20px;
}

.brand {
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 0.3px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.nav-links :deep(a) {
  color: #e2e8f0;
  text-decoration: none;
}

.nav-links :deep(a.router-link-active) {
  color: #93c5fd;
}

.role-tag {
  font-size: 12px;
  background: #1e293b;
  border: 1px solid #334155;
  padding: 4px 8px;
  border-radius: 999px;
}

.logout {
  background: #ef4444;
  color: #fff;
  border: 0;
  border-radius: 8px;
  padding: 7px 12px;
  cursor: pointer;
}

.content {
  padding: 0;
}

.footer {
  min-height: 48px;
  border-top: 1px solid #dbe3f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  color: #475569;
  font-size: 13px;
  background: #fff;
}

@media (max-width: 900px) {
  .topbar,
  .footer {
    padding: 12px;
  }

  .nav-links {
    gap: 8px;
    font-size: 13px;
  }
}
</style>
