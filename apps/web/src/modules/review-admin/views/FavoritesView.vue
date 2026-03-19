<template>
  <div class="page">
    <SectionCard title="Favorites" subtitle="Save guides so tourists can return to them later.">
      <template #actions>
        <form class="inline-form" @submit.prevent="addFavorite">
          <input v-model="guideId" type="number" min="1" placeholder="Guide ID" />
          <button :disabled="loading">Add</button>
        </form>
      </template>

      <p v-if="message" class="message">{{ message }}</p>

      <div v-if="favorites.length" class="grid">
        <article v-for="item in favorites" :key="item.id" class="favorite-card">
          <h3>{{ item.guideName }}</h3>
          <p>{{ item.guideEmail }}</p>
          <small>Saved on {{ formatDate(item.createdAt) }}</small>
          <button class="danger" @click="removeFavorite(item.guideId)">Remove</button>
        </article>
      </div>
      <p v-else class="empty">No favorite guides yet.</p>
    </SectionCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import { reviewAdminApi } from "../api/reviewAdminApi";

const favorites = ref([]);
const guideId = ref("");
const message = ref("");
const loading = ref(false);

const loadFavorites = async () => {
  loading.value = true;
  message.value = "";
  try {
    const { data } = await reviewAdminApi.listFavorites();
    favorites.value = data;
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to load favorites.";
  } finally {
    loading.value = false;
  }
};

const addFavorite = async () => {
  if (!guideId.value) return;
  try {
    await reviewAdminApi.addFavorite(Number(guideId.value));
    guideId.value = "";
    message.value = "Favorite added.";
    await loadFavorites();
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to add favorite.";
  }
};

const removeFavorite = async (id) => {
  try {
    await reviewAdminApi.removeFavorite(id);
    message.value = "Favorite removed.";
    await loadFavorites();
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to remove favorite.";
  }
};

const formatDate = (value) => new Date(value).toLocaleString();

onMounted(loadFavorites);
</script>

<style scoped>
.page { padding: 32px; }
.inline-form { display: flex; gap: 10px; }
input, button { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; }
button { background: #0f172a; color: white; cursor: pointer; }
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; }
.favorite-card { border: 1px solid #e2e8f0; border-radius: 16px; padding: 16px; background: #f8fafc; }
.favorite-card h3 { margin: 0 0 6px; }
.message { color: #0f766e; }
.empty { color: #64748b; }
.danger { margin-top: 12px; background: #dc2626; }
</style>
