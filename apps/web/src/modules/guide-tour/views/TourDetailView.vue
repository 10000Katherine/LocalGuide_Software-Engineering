<template>
  <main class="tour-detail-page">
    <el-skeleton :loading="loading" animated :rows="6">
      <template #default>
        <el-result
          v-if="errorMessage"
          icon="error"
          title="Unable to load tour"
          :sub-title="errorMessage"
        />

        <section v-else-if="tour" class="tour-detail-card">
          <p class="kicker">Tour Detail</p>
          <h1>{{ tour.title }}</h1>
          <p class="byline">Hosted by {{ tour.guideName }}</p>

          <div class="fact-grid">
            <div>
              <span>Category</span>
              <strong>{{ tour.category }}</strong>
            </div>
            <div>
              <span>Province</span>
              <strong>{{ tour.province || "Not set" }}</strong>
            </div>
            <div>
              <span>City</span>
              <strong>{{ tour.city || "Not set" }}</strong>
            </div>
            <div>
              <span>Language</span>
              <strong>{{ tour.language || "Not set" }}</strong>
            </div>
            <div>
              <span>Duration</span>
              <strong>{{ tour.durationHours }} hours</strong>
            </div>
            <div>
              <span>Group Size</span>
              <strong>Up to {{ tour.groupSize }}</strong>
            </div>
            <div>
              <span>Price</span>
              <strong>${{ Number(tour.price).toFixed(2) }} per person</strong>
            </div>
          </div>

          <section class="description">
            <h2>Description</h2>
            <p>{{ tour.description }}</p>
          </section>

          <section class="pricing-box">
            <h2>Price Calculator</h2>
            <p class="calc-note">Total cost = price per person x number of people.</p>
            <div class="calc-controls">
              <span>People</span>
              <el-input-number v-model="peopleCount" :min="1" :max="maxPeople" />
              <span class="total">Total: ${{ totalPrice.toFixed(2) }}</span>
            </div>
          </section>

          <section class="booking-box">
            <h2>Request This Tour</h2>
            <p class="calc-note">Select a date and time from this guide's availability.</p>
            <el-skeleton :loading="availabilityLoading" animated :rows="2">
              <template #default>
                <div v-if="dateOptions.length" class="booking-grid">
                  <el-form-item label="Date">
                    <el-select v-model="selectedDate" placeholder="Select date">
                      <el-option v-for="date in dateOptions" :key="date" :label="date" :value="date" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="Time">
                    <el-select v-model="selectedTime" placeholder="Select time">
                      <el-option v-for="time in timeOptions" :key="time" :label="time" :value="time" />
                    </el-select>
                  </el-form-item>
                </div>
                <el-empty v-else description="Guide has not posted availability slots yet" />
              </template>
            </el-skeleton>
          </section>

          <div class="actions">
            <router-link class="secondary" :to="'/guides/' + tour.guideId">View Guide Profile</router-link>
            <el-button type="primary" :loading="requesting" @click="submitBookingRequest">
              Request Booking
            </el-button>
          </div>
        </section>
      </template>
    </el-skeleton>
  </main>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../../../shared/stores/auth";
import {
  createBookingRequest,
  getTourDetail,
  listGuideAvailability
} from "../api/guideTourApi";
import { extractApiErrorMessage } from "../../../shared/utils/apiError";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const loading = ref(true);
const availabilityLoading = ref(false);
const requesting = ref(false);
const tour = ref(null);
const errorMessage = ref("");
const peopleCount = ref(1);
const selectedDate = ref("");
const selectedTime = ref("");
const guideAvailability = ref([]);

const maxPeople = computed(() => {
  const raw = Number(tour.value?.groupSize || 1);
  return raw > 0 ? raw : 1;
});

const totalPrice = computed(() => {
  const perPerson = Number(tour.value?.price || 0);
  return perPerson * Number(peopleCount.value || 1);
});

const dateOptions = computed(() => {
  return [...new Set((guideAvailability.value || []).map((slot) => slot.slotDate).filter(Boolean))]
    .sort((a, b) => String(a).localeCompare(String(b)));
});

const toMinutes = (timeValue) => {
  const [hours, minutes] = String(timeValue || "00:00").slice(0, 5).split(":").map(Number);
  return hours * 60 + minutes;
};

const formatMinutes = (minutes) => {
  const hours = String(Math.floor(minutes / 60)).padStart(2, "0");
  const mins = String(minutes % 60).padStart(2, "0");
  return hours + ":" + mins;
};

const normalizeTimeForApi = (value) => {
  const text = String(value || "").trim();
  if (!text) {
    return text;
  }
  if (/^\d{2}:\d{2}$/.test(text)) {
    return `${text}:00`;
  }
  return text;
};

const timeOptions = computed(() => {
  if (!selectedDate.value) return [];
  const slotsOnDate = (guideAvailability.value || []).filter((slot) => slot.slotDate === selectedDate.value);
  const values = new Set();

  for (const slot of slotsOnDate) {
    const start = toMinutes(slot.startTime);
    const end = toMinutes(slot.endTime);
    for (let current = start; current < end; current += 15) {
      values.add(formatMinutes(current));
    }
  }

  return [...values].sort((a, b) => a.localeCompare(b));
});

const loadAvailability = async (guideId) => {
  availabilityLoading.value = true;
  try {
    guideAvailability.value = await listGuideAvailability(guideId);
    selectedDate.value = dateOptions.value[0] || "";
    selectedTime.value = "";
  } catch (error) {
    guideAvailability.value = [];
    selectedDate.value = "";
    selectedTime.value = "";
    ElMessage.error(error?.response?.data?.message || "Failed to load guide availability");
  } finally {
    availabilityLoading.value = false;
  }
};

const loadTour = async () => {
  loading.value = true;
  errorMessage.value = "";
  tour.value = null;

  try {
    tour.value = await getTourDetail(route.params.id);
    peopleCount.value = 1;
    await loadAvailability(tour.value.guideId);
  } catch (error) {
    const message = error?.response?.data?.message || "Failed to load tour";
    errorMessage.value = message;
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
};

const submitBookingRequest = async () => {
  if (!authStore.accessToken) {
    ElMessage.info("Please log in first to continue booking.");
    router.push({ name: "login" });
    return;
  }

  if (!authStore.user) {
    try {
      await authStore.loadProfile();
    } catch {
      ElMessage.error("Unable to verify account.");
      return;
    }
  }

  if (authStore.user?.role !== "TOURIST") {
    ElMessage.warning("Only tourist accounts can request tour bookings.");
    return;
  }

  if (!selectedDate.value || !selectedTime.value) {
    ElMessage.warning("Select a date and time from the guide's available slots.");
    return;
  }

  requesting.value = true;
  try {
    const calculatedTotal = Number(totalPrice.value.toFixed(2));
    await createBookingRequest({
      tourId: Number(route.params.id),
      bookingDate: selectedDate.value,
      startTime: normalizeTimeForApi(selectedTime.value),
      numPeople: Number(peopleCount.value),
      totalPrice: calculatedTotal
    });
    ElMessage.success("Booking request submitted.");
  } catch (error) {
    const status = error?.response?.status || error?.status;
    if (status === 404 || status === 405 || status === 501) {
      ElMessage.warning("Booking request service is temporarily unavailable. Please try again later.");
      return;
    }
    ElMessage.error(extractApiErrorMessage(error, "Failed to submit booking request"));
  } finally {
    requesting.value = false;
  }
};

watch(() => route.params.id, loadTour, { immediate: true });
watch(maxPeople, (value) => {
  if (peopleCount.value > value) {
    peopleCount.value = value;
  }
});
watch(selectedDate, () => {
  if (!timeOptions.value.includes(selectedTime.value)) {
    selectedTime.value = "";
  }
});
</script>

<style scoped>
.tour-detail-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(160deg, #fff9f0 0%, #edf5ff 100%);
}

.tour-detail-card {
  max-width: 920px;
  margin: 0 auto;
  background: #ffffff;
  border: 1px solid #dce6f3;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
}

.kicker {
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: #64748b;
  font-size: 12px;
}

h1 {
  margin: 4px 0;
}

.byline {
  color: #475569;
}

.fact-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  gap: 12px;
}

.fact-grid div {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 4px;
}

.fact-grid span {
  font-size: 12px;
  color: #64748b;
}

.description {
  margin-top: 18px;
}

.description h2 {
  margin-bottom: 8px;
}

.pricing-box {
  margin-top: 16px;
  border: 1px solid #dbeafe;
  background: #f8fbff;
  border-radius: 12px;
  padding: 12px;
}

.pricing-box h2 {
  margin: 0;
}

.booking-box {
  margin-top: 16px;
  border: 1px solid #d1fae5;
  background: #f0fdf4;
  border-radius: 12px;
  padding: 12px;
}

.booking-box h2 {
  margin: 0;
}

.calc-note {
  margin: 6px 0 12px;
  color: #475569;
}

.calc-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.total {
  font-weight: 700;
  color: #0f766e;
}

.booking-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.secondary {
  color: #0f766e;
  text-decoration: none;
  font-weight: 600;
}

@media (max-width: 620px) {
  .booking-grid {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}
</style>
