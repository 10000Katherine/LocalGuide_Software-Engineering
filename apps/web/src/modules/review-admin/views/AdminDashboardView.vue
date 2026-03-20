<template>
  <div class="page stack">
    <SectionCard title="Admin analytics" subtitle="Overview of the Dev 4 module KPIs.">
      <div v-if="analytics" class="grid">
        <StatTile label="Total users" :value="analytics.totalUsers" />
        <StatTile label="Active users" :value="analytics.activeUsers" />
        <StatTile label="Guides" :value="analytics.totalGuides" />
        <StatTile label="Tourists" :value="analytics.totalTourists" />
        <StatTile label="Verified guides" :value="analytics.verifiedGuides" />
        <StatTile label="Pending verifications" :value="analytics.pendingVerifications" />
        <StatTile label="Reviews" :value="analytics.totalReviews" />
        <StatTile label="Favorites" :value="analytics.totalFavorites" />
      </div>
      <p v-else class="empty">Analytics unavailable.</p>
    </SectionCard>

    <SectionCard title="Guide engagement earnings view" subtitle="Temporary proxy until payment totals from Module C are merged.">
      <div class="inline-form">
        <input v-model.number="guideId" type="number" min="1" placeholder="Guide ID" />
        <button @click="loadGuideEarnings">Load</button>
      </div>
      <div v-if="earnings" class="status-box">
        <p><strong>{{ earnings.guideName }}</strong></p>
        <p>Average rating: {{ earnings.averageRating }}</p>
        <p>Total reviews: {{ earnings.totalReviews }}</p>
        <p>Total favorites: {{ earnings.totalFavorites }}</p>
        <p>{{ earnings.note }}</p>
      </div>
    </SectionCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import StatTile from "../components/StatTile.vue";
import { reviewAdminApi } from "../api/reviewAdminApi";

const analytics = ref(null);
const earnings = ref(null);
const guideId = ref(null);

const loadAnalytics = async () => {
  try {
    const { data } = await reviewAdminApi.getAnalytics();
    analytics.value = data;
  } catch {
    analytics.value = null;
  }
};

const loadGuideEarnings = async () => {
  if (!guideId.value) return;
  const { data } = await reviewAdminApi.getGuideEarnings(guideId.value);
  earnings.value = data;
};

onMounted(loadAnalytics);
</script>

<style scoped>
.page { padding: 32px; }
.stack { display: grid; gap: 24px; }
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; }
.inline-form { display: flex; gap: 10px; margin-bottom: 12px; }
input, button { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; }
button { background: #0f172a; color: white; cursor: pointer; }
.status-box { padding: 16px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; }
.empty { color: #64748b; }
</style>
