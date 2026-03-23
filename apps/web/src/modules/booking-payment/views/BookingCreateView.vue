<template>
  <main class="page">
    <section class="panel">
      <header class="panel-header">
        <div>
          <h1>Create Booking</h1>
          <p>Provide booking details to reserve a tour slot.</p>
        </div>
        <el-button plain @click="goBack">Back</el-button>
      </header>

      <el-form label-position="top" @submit.prevent="submit">
        <div class="form-grid">
          <el-form-item label="Tour ID" required>
            <el-input-number v-model="form.tourId" :min="1" :controls="false" class="full" />
          </el-form-item>

          <el-form-item label="Booking Date" required>
            <el-date-picker
              v-model="form.bookingDate"
              type="date"
              value-format="YYYY-MM-DD"
              :disabled-date="disablePastDates"
              placeholder="Select date"
              class="full" />
          </el-form-item>

          <el-form-item label="Start Time" required>
            <el-time-picker
              v-model="form.startTime"
              value-format="HH:mm:ss"
              placeholder="Select time"
              class="full" />
          </el-form-item>

          <el-form-item label="People" required>
            <el-input-number v-model="form.numPeople" :min="1" :max="20" class="full" />
          </el-form-item>

          <el-form-item label="Unit Price (CAD)">
            <el-input-number v-model="form.unitPrice" :min="0.01" :step="1" :precision="2" class="full" />
          </el-form-item>

          <el-form-item label="Total Price (CAD)">
            <el-input :model-value="totalPriceDisplay" disabled />
          </el-form-item>
        </div>

        <div class="actions">
          <el-button @click="goBack">Cancel</el-button>
          <el-button type="primary" :loading="store.actionLoading" @click="submit">Create Booking</el-button>
        </div>
      </el-form>
    </section>
  </main>
</template>

<script setup>
import { computed, reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { useBookingPaymentStore } from "../stores/bookingPayment";
import { extractApiErrorMessage } from "../../../shared/utils/apiError";

const route = useRoute();
const router = useRouter();
const store = useBookingPaymentStore();

const initialTourId = Number(route.query.tourId || 0);
const initialUnitPrice = Number(route.query.unitPrice || 80);

const form = reactive({
  tourId: Number.isFinite(initialTourId) && initialTourId > 0 ? initialTourId : 1,
  bookingDate: "",
  startTime: "09:00:00",
  numPeople: 1,
  unitPrice: Number.isFinite(initialUnitPrice) && initialUnitPrice > 0 ? initialUnitPrice : 80
});

const totalPrice = computed(() => Number(form.numPeople) * Number(form.unitPrice));
const totalPriceDisplay = computed(() => totalPrice.value.toFixed(2));

const goBack = () => {
  router.push("/bookings");
};

const disablePastDates = (date) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return date.getTime() < today.getTime();
};

const normalizeTime = (timeValue) => {
  if (!timeValue) {
    return "";
  }
  const value = String(timeValue).trim();
  if (/^\d{2}:\d{2}$/.test(value)) {
    return `${value}:00`;
  }
  return value;
};

const submit = async () => {
  if (!form.tourId || form.tourId <= 0) {
    ElMessage.warning("Tour ID is required");
    return;
  }
  if (!form.bookingDate) {
    ElMessage.warning("Booking date is required");
    return;
  }
  if (!form.startTime) {
    ElMessage.warning("Start time is required");
    return;
  }
  if (!form.numPeople || form.numPeople < 1 || form.numPeople > 20) {
    ElMessage.warning("People count must be between 1 and 20");
    return;
  }
  if (!Number.isFinite(totalPrice.value) || totalPrice.value <= 0) {
    ElMessage.warning("Total price must be greater than 0");
    return;
  }

  try {
    const booking = await store.submitBooking({
      tourId: Number(form.tourId),
      bookingDate: form.bookingDate,
      startTime: normalizeTime(form.startTime),
      numPeople: Number(form.numPeople),
      totalPrice: Number(totalPriceDisplay.value)
    });
    ElMessage.success("Booking created");
    router.push(`/bookings/${booking.id}`);
  } catch (error) {
    ElMessage.error(extractApiErrorMessage(error, "Failed to create booking"));
  }
};
</script>

<style scoped>
.page {
  min-height: calc(100vh - 112px);
  padding: 24px;
  background:
    radial-gradient(circle at 100% 0%, rgba(2, 132, 199, 0.14), transparent 42%),
    radial-gradient(circle at 0% 100%, rgba(14, 116, 144, 0.16), transparent 46%),
    #f3f9ff;
}

.panel {
  max-width: 920px;
  margin: 0 auto;
  border-radius: 18px;
  border: 1px solid #d7e6f7;
  background: #ffffff;
  box-shadow: 0 16px 36px rgba(14, 116, 144, 0.12);
  padding: 22px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 18px;
}

.panel-header h1 {
  margin: 0;
  color: #0f172a;
}

.panel-header p {
  margin: 8px 0 0;
  color: #475569;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.full {
  width: 100%;
}

.actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 760px) {
  .page {
    padding: 12px;
  }

  .panel {
    padding: 14px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
