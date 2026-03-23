<template>
  <div class="page two-col">
    <SectionCard title="Leave a review" subtitle="Tourists can review guides after a booking.">
      <form class="stack" @submit.prevent="submitReview">
        <label class="field-label" for="review-booking-select">Booking reference</label>
        <select id="review-booking-select" v-model="selectedBookingId" @change="onBookingSelection">
          <option disabled value="">Select from your booking history</option>
          <option v-for="option in bookingOptions" :key="option.bookingId" :value="String(option.bookingId)">
            {{ option.label }}
          </option>
        </select>
        <p v-if="bookingsLoading" class="hint">Loading your bookings...</p>
        <p v-else-if="bookingOptions.length === 0" class="hint">
          No eligible bookings found yet. Complete or confirm a booking first.
        </p>

        <input :value="form.bookingReference" placeholder="Booking reference" readonly />
        <input :value="form.guideId || ''" placeholder="Guide ID" readonly />
        <input :value="form.tourId || ''" placeholder="Tour ID" readonly />
        <input v-model.number="form.rating" type="number" min="1" max="5" placeholder="Rating 1-5" />
        <textarea v-model="form.comment" rows="5" placeholder="Share your experience"></textarea>
        <button :disabled="!form.bookingReference || !form.guideId">Submit review</button>
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
import { onMounted, ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import StatTile from "../components/StatTile.vue";
import { listBookings } from "../../booking-payment/api/bookingPaymentApi";
import { getTourDetail } from "../../guide-tour/api/guideTourApi";
import { reviewAdminApi } from "../api/reviewAdminApi";

const form = ref({ bookingReference: "", guideId: null, tourId: null, rating: 5, comment: "" });
const selectedBookingId = ref("");
const bookingOptions = ref([]);
const bookingsLoading = ref(false);
const selectedGuideId = ref(null);
const reviews = ref([]);
const summary = ref(null);
const replyMap = ref({});
const message = ref("");

const REVIEWABLE_STATUSES = new Set(["CONFIRMED", "IN_PROGRESS", "COMPLETED"]);

const loadBookingOptions = async () => {
  bookingsLoading.value = true;
  message.value = "";
  try {
    const bookings = await listBookings();
    const reviewableBookings = (Array.isArray(bookings) ? bookings : []).filter((booking) =>
      REVIEWABLE_STATUSES.has(String(booking.status || "").toUpperCase())
    );

    const options = await Promise.all(reviewableBookings.map(async (booking) => {
      try {
        const tour = await getTourDetail(booking.tourId);
        return {
          bookingId: booking.id,
          bookingReference: `BOOKING-${booking.id}`,
          guideId: tour?.guideId || null,
          tourId: booking.tourId,
          label: `Booking ID #${booking.id} | Tour ${tour?.title || booking.tourId} | ${booking.bookingDate || "No date"}`
        };
      } catch {
        return {
          bookingId: booking.id,
          bookingReference: `BOOKING-${booking.id}`,
          guideId: null,
          tourId: booking.tourId,
          label: `Booking ID #${booking.id} | Tour ID ${booking.tourId} | ${booking.bookingDate || "No date"}`
        };
      }
    }));

    bookingOptions.value = options.filter((option) => Number(option.bookingId));
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to load booking history.";
    bookingOptions.value = [];
  } finally {
    bookingsLoading.value = false;
  }
};

const onBookingSelection = () => {
  const option = bookingOptions.value.find((item) => String(item.bookingId) === String(selectedBookingId.value));
  if (!option) {
    form.value.bookingReference = "";
    form.value.guideId = null;
    form.value.tourId = null;
    return;
  }

  form.value.bookingReference = option.bookingReference;
  form.value.guideId = option.guideId;
  form.value.tourId = option.tourId;
};

const submitReview = async () => {
  if (!form.value.bookingReference || !form.value.guideId) {
    message.value = "Please select a valid booking from your history.";
    return;
  }

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

onMounted(loadBookingOptions);
</script>

<style scoped>
.page { padding: 32px; }
.two-col { display: grid; grid-template-columns: 1fr 1.2fr; gap: 24px; }
.stack { display: grid; gap: 12px; }
.compact { gap: 10px; }
.inline-form { display: flex; gap: 10px; }
input, select, textarea, button { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; }
button { background: #0f172a; color: white; cursor: pointer; }
button:disabled { cursor: not-allowed; opacity: 0.65; }
.review-card { padding: 14px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; }
.reply { color: #0f766e; }
.summary-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.message { color: #0f766e; }
.empty { color: #64748b; }
.field-label { font-size: 0.9rem; color: #334155; font-weight: 600; }
.hint { color: #64748b; margin: -4px 0 4px; }
@media (max-width: 900px) { .two-col { grid-template-columns: 1fr; } }
</style>
