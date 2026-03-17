<template>
  <div class="page">
    <SectionCard title="Pending guide verifications" subtitle="Approve or reject documents submitted by guides.">
      <p v-if="message" class="message">{{ message }}</p>
      <div v-if="requests.length" class="stack">
        <article v-for="request in requests" :key="request.id" class="card-item">
          <strong>{{ request.guideName }}</strong>
          <p>{{ request.guideEmail }}</p>
          <p>Document: {{ request.documentUrl }}</p>
          <p>Note: {{ request.submissionNote || '—' }}</p>
          <textarea v-model="reviewNoteMap[request.id]" rows="3" placeholder="Admin note"></textarea>
          <div class="inline-form">
            <button @click="decide(request.id, 'APPROVE')">Approve</button>
            <button class="danger" @click="decide(request.id, 'REJECT')">Reject</button>
          </div>
        </article>
      </div>
      <p v-else class="empty">No pending requests.</p>
    </SectionCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import { reviewAdminApi } from "../api/reviewAdminApi";

const requests = ref([]);
const reviewNoteMap = ref({});
const message = ref("");

const loadRequests = async () => {
  try {
    const { data } = await reviewAdminApi.listPendingVerifications();
    requests.value = data;
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to load verification requests.";
  }
};

const decide = async (id, decision) => {
  try {
    await reviewAdminApi.reviewVerification(id, { decision, reviewNote: reviewNoteMap.value[id] || "" });
    message.value = `Verification ${decision.toLowerCase()}d.`;
    await loadRequests();
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to update request.";
  }
};

onMounted(loadRequests);
</script>

<style scoped>
.page { padding: 32px; }
.stack { display: grid; gap: 16px; }
.card-item { padding: 16px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; }
textarea, button { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; }
.inline-form { display: flex; gap: 10px; margin-top: 12px; }
button { background: #0f172a; color: white; cursor: pointer; }
.danger { background: #dc2626; }
.message { color: #0f766e; }
.empty { color: #64748b; }
</style>
