<template>
  <div class="page two-col">
    <SectionCard title="Leave a review" subtitle="Tourists can review guides after a booking.">
      <form class="stack" @submit.prevent="submitReview">
        <input v-model="form.bookingReference" placeholder="Booking reference" />
        <input v-model.number="form.guideId" type="number" min="1" placeholder="Guide ID" />
        <input v-model.number="form.tourId" type="number" min="1" placeholder="Tour ID (optional)" />
        <input v-model.number="form.rating" type="number" min="1" max="5" placeholder="Rating 1-5" />
        <textarea v-model="form.comment" rows="5" placeholder="Share your experience"></textarea>
        <button>Submit review</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
    </SectionCard>

    <SectionCard title="Guide reviews" subtitle="Guides can inspect feedback and post a reply.">
      <div class="stack compact">
        <div class="inline-form">
          <input v-model.number="selectedGuideId" type="number" min="1" placeholder="Guide ID" />
          <button @click="loadGuideReviews">Load</button>
        </div>
        <div v-if="summary" class="summary-grid">
          <StatTile label="Average rating" :value="summary.averageRating" />
          <StatTile label="Total reviews" :value="summary.totalReviews" />
          <StatTile label="Favorites" :value="summary.favoriteCount" />
        </div>
        <div v-if="reviews.length" class="stack compact">
          <article v-for="item in reviews" :key="item.id" class="review-card">
            <strong>{{ item.touristName }}</strong>
            <p>Rating: {{ item.rating }}/5</p>
            <p>{{ item.comment }}</p>
            <small>Booking: {{ item.bookingReference }}</small>
            <p v-if="item.guideReply" class="reply">Guide reply: {{ item.guideReply }}</p>
            <div class="inline-form">
              <input v-model="replyMap[item.id]" placeholder="Write a reply" />
              <button @click="reply(item.id)">Reply</button>
            </div>
          </article>
        </div>
        <p v-else class="empty">No reviews loaded yet.</p>
      </div>
    </SectionCard>
  </div>
</template>

<script setup>
import { ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import StatTile from "../components/StatTile.vue";
import { reviewAdminApi } from "../api/reviewAdminApi";

const form = ref({ bookingReference: "", guideId: null, tourId: null, rating: 5, comment: "" });
const selectedGuideId = ref(null);
const reviews = ref([]);
const summary = ref(null);
const replyMap = ref({});
const message = ref("");

const submitReview = async () => {
  try {
    await reviewAdminApi.createReview(form.value);
    message.value = "Review submitted.";
    if (form.value.guideId) {
      selectedGuideId.value = form.value.guideId;
      await loadGuideReviews();
    }
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to submit review.";
  }
};

const loadGuideReviews = async () => {
  if (!selectedGuideId.value) return;
  try {
    const [{ data: reviewData }, { data: summaryData }] = await Promise.all([
      reviewAdminApi.listGuideReviews(selectedGuideId.value),
      reviewAdminApi.getGuideRatingSummary(selectedGuideId.value)
    ]);
    reviews.value = reviewData;
    summary.value = summaryData;
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to load reviews.";
  }
};

const reply = async (reviewId) => {
  try {
    await reviewAdminApi.replyToReview(reviewId, replyMap.value[reviewId] || "");
    message.value = "Reply saved.";
    await loadGuideReviews();
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to save reply.";
  }
};
</script>

<style scoped>
.page { padding: 32px; }
.two-col { display: grid; grid-template-columns: 1fr 1.2fr; gap: 24px; }
.stack { display: grid; gap: 12px; }
.compact { gap: 10px; }
.inline-form { display: flex; gap: 10px; }
input, textarea, button { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; }
button { background: #0f172a; color: white; cursor: pointer; }
.review-card { padding: 14px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; }
.reply { color: #0f766e; }
.summary-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.message { color: #0f766e; }
.empty { color: #64748b; }
@media (max-width: 900px) { .two-col { grid-template-columns: 1fr; } }
</style>
