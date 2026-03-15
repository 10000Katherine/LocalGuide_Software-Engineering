<template>
  <main class="page">
    <section class="panel">
      <header class="panel-header">
        <div>
          <h1>Booking #{{ booking?.id || route.params.id }}</h1>
          <p>Check current status, payment intent, and actions.</p>
        </div>
        <el-button plain @click="goBack">Back to List</el-button>
      </header>

      <el-skeleton v-if="store.loading" animated :rows="6" />

      <el-empty v-else-if="!booking" description="Booking not found" />

      <template v-else>
        <div class="status-row">
          <span>Status</span>
          <BookingStatusTag :status="booking.status" />
        </div>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="Tour ID">{{ booking.tourId }}</el-descriptions-item>
          <el-descriptions-item label="Booking Date">{{ booking.bookingDate }}</el-descriptions-item>
          <el-descriptions-item label="Start Time">{{ booking.startTime }}</el-descriptions-item>
          <el-descriptions-item label="People">{{ booking.numPeople }}</el-descriptions-item>
          <el-descriptions-item label="Total Price">{{ totalPriceDisplay }}</el-descriptions-item>
          <el-descriptions-item label="Payment Intent">
            {{ booking.stripePaymentIntentId || "-" }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="actions">
          <el-button @click="goBack">Back</el-button>
          <el-button v-if="canPay" type="primary" @click="goPay">Go to Payment</el-button>
          <el-button
            v-if="canCancel"
            type="danger"
            plain
            :loading="store.actionLoading"
            @click="cancelBookingAction">
            Cancel Booking
          </el-button>
        </div>
      </template>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import BookingStatusTag from "../components/BookingStatusTag.vue";
import {
  CANCELLABLE_BOOKING_STATUSES,
  PAYMENT_ENTRY_STATUSES
} from "../constants/bookingStatus";
import { useBookingPaymentStore } from "../stores/bookingPayment";

const route = useRoute();
const router = useRouter();
const store = useBookingPaymentStore();

const booking = computed(() => store.currentBooking);
const canPay = computed(() => PAYMENT_ENTRY_STATUSES.has(booking.value?.status));
const canCancel = computed(() => CANCELLABLE_BOOKING_STATUSES.has(booking.value?.status));
const totalPriceDisplay = computed(() => {
  const value = Number(booking.value?.totalPrice || 0);
  return new Intl.NumberFormat("en-CA", {
    style: "currency",
    currency: "CAD"
  }).format(value);
});

const loadBooking = async () => {
  try {
    await store.fetchBooking(route.params.id);
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load booking");
  }
};

const goBack = () => router.push("/bookings");
const goPay = () => router.push(`/bookings/${route.params.id}/pay`);

const cancelBookingAction = async () => {
  try {
    await ElMessageBox.confirm(
      "Are you sure you want to cancel this booking?",
      "Confirm",
      { confirmButtonText: "Cancel Booking", cancelButtonText: "Keep", type: "warning" }
    );
    await store.requestCancelBooking(Number(route.params.id));
    ElMessage.success("Booking cancelled");
    await loadBooking();
  } catch (error) {
    if (error === "cancel") {
      return;
    }
    ElMessage.error(error?.response?.data?.message || error?.message || "Failed to cancel booking");
  }
};

onMounted(loadBooking);
</script>

<style scoped>
.page {
  min-height: calc(100vh - 112px);
  padding: 24px;
  background:
    radial-gradient(circle at 100% 0%, rgba(56, 189, 248, 0.14), transparent 40%),
    radial-gradient(circle at 0% 100%, rgba(14, 116, 144, 0.16), transparent 42%),
    #f4fbff;
}

.panel {
  max-width: 860px;
  margin: 0 auto;
  border-radius: 18px;
  border: 1px solid #d7e7f5;
  background: #fff;
  box-shadow: 0 16px 36px rgba(12, 74, 110, 0.12);
  padding: 22px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.panel-header h1 {
  margin: 0;
  color: #0f172a;
}

.panel-header p {
  margin: 8px 0 0;
  color: #475569;
}

.status-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  color: #334155;
  font-weight: 600;
}

.actions {
  margin-top: 16px;
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

  .panel-header {
    flex-direction: column;
    align-items: stretch;
  }

  .actions {
    flex-wrap: wrap;
  }
}
</style>
