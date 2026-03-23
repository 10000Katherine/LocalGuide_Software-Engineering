<template>
  <el-card class="booking-card" shadow="hover">
    <div class="card-header">
      <div>
        <h3>Booking ID #{{ booking.id }}</h3>
        <p>Tour ID {{ booking.tourId }} · {{ booking.bookingDate }} {{ timeDisplay }}</p>
      </div>
      <BookingStatusTag :status="booking.status" />
    </div>

    <div class="booking-grid">
      <div>
        <span class="label">People</span>
        <strong>{{ booking.numPeople }}</strong>
      </div>
      <div>
        <span class="label">Total</span>
        <strong>{{ priceDisplay }}</strong>
      </div>
      <div>
        <span class="label">Payment Intent</span>
        <strong>{{ booking.stripePaymentIntentId || "-" }}</strong>
      </div>
    </div>

    <div class="actions">
      <el-button plain @click="$emit('open', booking.id)">View</el-button>
      <el-button v-if="canPay" type="primary" @click="$emit('pay', booking.id)">Pay</el-button>
      <el-button
        v-if="canCancel"
        type="danger"
        plain
        @click="$emit('cancel', booking.id)">
        Cancel
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from "vue";
import BookingStatusTag from "./BookingStatusTag.vue";
import {
  CANCELLABLE_BOOKING_STATUSES,
  PAYMENT_ENTRY_STATUSES
} from "../constants/bookingStatus";

const props = defineProps({
  booking: { type: Object, required: true }
});

defineEmits(["open", "pay", "cancel"]);

const canPay = computed(() => PAYMENT_ENTRY_STATUSES.has(props.booking.status));
const canCancel = computed(() => CANCELLABLE_BOOKING_STATUSES.has(props.booking.status));

const timeDisplay = computed(() => (props.booking.startTime || "").slice(0, 5));
const priceDisplay = computed(() => {
  const value = Number(props.booking.totalPrice || 0);
  return new Intl.NumberFormat("en-CA", {
    style: "currency",
    currency: "CAD"
  }).format(value);
});
</script>

<style scoped>
.booking-card {
  border-radius: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}

.card-header h3 {
  margin: 0;
  font-size: 1.05rem;
  color: #0f172a;
}

.card-header p {
  margin: 6px 0 0;
  color: #475569;
  font-size: 0.92rem;
}

.booking-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.booking-grid > div {
  background: #f8fafc;
  border: 1px solid #dbe4f0;
  border-radius: 10px;
  padding: 10px;
  min-width: 0;
}

.label {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 4px;
}

strong {
  color: #0f172a;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 760px) {
  .booking-grid {
    grid-template-columns: 1fr;
  }

  .actions {
    justify-content: stretch;
  }

  .actions :deep(button) {
    flex: 1;
  }
}
</style>
