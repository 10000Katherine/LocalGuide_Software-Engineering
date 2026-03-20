<template>
  <div class="page">
    <SectionCard title="Guide verification" subtitle="Submit identity or license documents for admin review.">
      <form class="stack" @submit.prevent="submitVerification">
        <input v-model="form.documentUrl" placeholder="Document URL" />
        <textarea v-model="form.submissionNote" rows="4" placeholder="Submission note"></textarea>
        <button>Submit for review</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
    </SectionCard>

    <SectionCard title="Current verification status" subtitle="Latest admin decision for this guide account.">
      <div v-if="status" class="status-box">
        <strong>{{ status.status }}</strong>
        <p>Document: {{ status.documentUrl }}</p>
        <p>Submission note: {{ status.submissionNote || '—' }}</p>
        <p>Review note: {{ status.reviewNote || '—' }}</p>
      </div>
      <p v-else class="empty">No submission found yet.</p>
    </SectionCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import { reviewAdminApi } from "../api/reviewAdminApi";

const form = ref({ documentUrl: "", submissionNote: "" });
const status = ref(null);
const message = ref("");

const loadStatus = async () => {
  try {
    const { data } = await reviewAdminApi.getMyVerification();
    status.value = data;
  } catch {
    status.value = null;
  }
};

const submitVerification = async () => {
  try {
    const { data } = await reviewAdminApi.submitVerification(form.value);
    status.value = data;
    message.value = "Verification request submitted.";
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to submit verification.";
  }
};

onMounted(loadStatus);
</script>

<style scoped>
.page { padding: 32px; display: grid; gap: 24px; }
.stack { display: grid; gap: 12px; }
input, textarea, button { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; }
button { background: #0f172a; color: white; cursor: pointer; }
.status-box { padding: 16px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; }
.message { color: #0f766e; }
.empty { color: #64748b; }
</style>
