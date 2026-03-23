<template>
  <main class="page">
    <section class="panel">
      <header class="panel-header">
        <div>
          <h1>My Bookings</h1>
          <p>Manage your schedule, payments, and booking statuses.</p>
        </div>
      </header>

      <el-skeleton v-if="store.loading" animated :rows="6" />

      <el-empty
        v-else-if="store.sortedBookings.length === 0"
        description="No bookings yet. Find a tour from Search and request booking to continue." />

      <div v-else class="booking-list">
        <BookingCard
          v-for="booking in store.sortedBookings"
          :key="booking.id"
          :booking="booking"
          @open="goDetail"
          @pay="goPay"
          @cancel="cancelOne" />
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import BookingCard from "../components/BookingCard.vue";
import { useBookingPaymentStore } from "../stores/bookingPayment";

const router = useRouter();
const store = useBookingPaymentStore();

const goDetail = (id) => router.push(`/bookings/${id}`);
const goPay = (id) => router.push(`/bookings/${id}/pay`);

const cancelOne = async (id) => {
  try {
    await ElMessageBox.confirm(
      "Cancel this booking? If payment exists, refund flow will be triggered by backend logic.",
      "Cancel Booking",
      { confirmButtonText: "Cancel Booking", cancelButtonText: "Keep", type: "warning" }
    );
    await store.requestCancelBooking(id);
    ElMessage.success("Booking cancelled");
  } catch (error) {
    if (error === "cancel") {
      return;
    }
    ElMessage.error(error?.response?.data?.message || error?.message || "Failed to cancel booking");
  }
};

onMounted(async () => {
  try {
    await store.fetchBookings();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load bookings");
  }
});
</script>

<style scoped>
.page {
  min-height: calc(100vh - 112px);
  padding: 24px;
  background:
    radial-gradient(circle at 0% 0%, rgba(56, 189, 248, 0.14), transparent 36%),
    radial-gradient(circle at 100% 100%, rgba(14, 116, 144, 0.18), transparent 38%),
    #f7fbff;
}

.panel {
  max-width: 1060px;
  margin: 0 auto;
  background: #ffffff;
  border: 1px solid #d4e3f5;
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 16px 36px rgba(12, 74, 110, 0.12);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
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

.booking-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
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
}
</style>
