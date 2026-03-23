<template>
  <main class="workspace-page">
    <section class="workspace-wrap">
      <header class="hero">
        <p class="eyebrow">Guide Workspace</p>
        <h1>Guide Control Center</h1>
        <p class="sub">
          Access all guide features from one place: tours, availability, dashboard, and your public profile.
        </p>
      </header>

      <div class="grid">
        <article class="card">
          <h2>Guide Dashboard</h2>
          <p>Bookings, earnings snapshot, profile edit, and upcoming tours.</p>
          <router-link class="action" to="/guide/dashboard">Open Dashboard</router-link>
        </article>

        <article class="card">
          <h2>Tour Management</h2>
          <p>Create, edit, delete, and review your published tours.</p>
          <router-link class="action" to="/guide/tours">Manage Tours</router-link>
        </article>

        <article class="card">
          <h2>Availability Calendar</h2>
          <p>Set available dates and time slots for tourist bookings.</p>
          <router-link class="action" to="/guide/availability">Manage Availability</router-link>
        </article>

        <article class="card">
          <h2>Booking Requests</h2>
          <p>Review incoming booking requests and decide to accept or decline.</p>
          <router-link class="action" to="/guide/booking-requests">Manage Requests</router-link>
        </article>

        <article class="card">
          <h2>Guide Profile</h2>
          <p>Open your public profile and verify bio, language, city, and listed tours.</p>
          <router-link v-if="guideProfileId" class="action" :to="'/guides/' + guideProfileId">Open Profile</router-link>
          <span v-else class="hint">Profile link will appear once guide profile loads.</span>
        </article>

        <article class="card">
          <h2>Tour Detail Preview</h2>
          <p>Open a tour detail page with pricing, duration, and booking CTA.</p>
          <router-link v-if="firstTourId" class="action" :to="'/tours/' + firstTourId">Open Tour Detail</router-link>
          <span v-else class="hint">Create a tour first in Tour Management to preview detail view.</span>
        </article>
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getMyGuideProfile, listMyTours } from "../api/guideTourApi";

const guideProfileId = ref(null);
const firstTourId = ref(null);

onMounted(async () => {
  try {
    const [profile, tours] = await Promise.all([getMyGuideProfile(), listMyTours()]);
    guideProfileId.value = profile?.id || null;
    firstTourId.value = tours?.length ? tours[0].id : null;
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Unable to load guide workspace shortcuts");
  }
});
</script>

<style scoped>
.workspace-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(170deg, #f4f8ff 0%, #fff8ef 100%);
}

.workspace-wrap {
  max-width: 1180px;
  margin: 0 auto;
}

.hero {
  background: #ffffff;
  border: 1px solid #dfe8f5;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.eyebrow {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: #64748b;
}

.hero h1 {
  margin: 6px 0;
}

.sub {
  margin: 0;
  color: #475569;
}

.grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.card {
  background: #fff;
  border: 1px solid #dfe8f5;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.card h2 {
  margin: 0 0 8px;
  font-size: 1.1rem;
}

.card p {
  margin: 0 0 12px;
  color: #475569;
  min-height: 44px;
}

.action {
  color: #0f766e;
  text-decoration: none;
  font-weight: 700;
}

.hint {
  font-size: 0.9rem;
  color: #64748b;
}
</style>
