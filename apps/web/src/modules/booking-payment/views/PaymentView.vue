<template>
  <main class="page">
    <section class="panel">
      <header class="panel-header">
        <div>
          <h1>Payment</h1>
          <p>Confirm payment for Booking ID #{{ route.params.id }}.</p>
        </div>
        <el-button plain @click="goDetail()">Back to Detail</el-button>
      </header>

      <el-alert
        title="Development mode"
        type="info"
        show-icon
        :closable="false"
        description="This page simulates a successful Stripe callback by calling the webhook endpoint." />

      <el-skeleton v-if="store.loading" animated :rows="5" class="skeleton" />

      <template v-else-if="booking">
        <div class="payment-summary">
          <div>
            <span class="label">Current Status</span>
            <BookingStatusTag :status="booking.status" />
          </div>
          <div>
            <span class="label">Total</span>
            <strong>{{ totalPriceDisplay }}</strong>
          </div>
          <div>
            <span class="label">Payment Intent ID</span>
            <strong>{{ paymentIntentId || "-" }}</strong>
          </div>
          <div>
            <span class="label">Client Secret</span>
            <strong>{{ maskedClientSecret }}</strong>
          </div>
        </div>

        <div class="actions">
          <el-button @click="goDetail()">Back</el-button>
          <el-button
            type="primary"
            :loading="store.actionLoading"
            :disabled="!paymentIntentId"
            @click="confirmPayment">
            Confirm Payment (Simulate Webhook)
          </el-button>
        </div>
      </template>

      <el-empty v-else description="Booking data is unavailable" />
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import BookingStatusTag from "../components/BookingStatusTag.vue";
import {
  PAYMENT_ENTRY_STATUSES
} from "../constants/bookingStatus";
import { useBookingPaymentStore } from "../stores/bookingPayment";

const route = useRoute();
const router = useRouter();
const store = useBookingPaymentStore();

const booking = computed(() => store.currentBooking);
const paymentIntentId = computed(() => store.paymentIntent?.paymentIntentId || booking.value?.stripePaymentIntentId || "");

const totalPriceDisplay = computed(() => {
  const value = Number(booking.value?.totalPrice || 0);
  return new Intl.NumberFormat("en-CA", {
    style: "currency",
    currency: "CAD"
  }).format(value);
});

const maskedClientSecret = computed(() => {
  const clientSecret = store.paymentIntent?.clientSecret;
  if (!clientSecret) {
    return "-";
  }
  if (clientSecret.length <= 8) {
    return clientSecret;
  }
  return `${clientSecret.slice(0, 4)}...${clientSecret.slice(-4)}`;
});

const goDetail = (paymentSuccess = false) => {
  if (paymentSuccess) {
    router.push({ path: `/bookings/${route.params.id}`, query: { payment: "success" } });
    return;
  }
  router.push(`/bookings/${route.params.id}`);
};

const canEnterPaymentFlow = (loadedBooking) => {
  if (!loadedBooking?.status) {
    return false;
  }
  if (!PAYMENT_ENTRY_STATUSES.has(loadedBooking.status)) {
    return false;
  }
  return true;
};

const prepare = async () => {
  try {
    const loaded = await store.fetchBooking(route.params.id);
    if (!canEnterPaymentFlow(loaded)) {
      ElMessage.warning("This booking cannot enter payment flow in current status");
      goDetail();
      return;
    }
    await store.preparePayment(loaded);
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "Unable to prepare payment");
  }
};

const confirmPayment = async () => {
  if (!paymentIntentId.value) {
    ElMessage.warning("Missing payment intent");
    return;
  }

  try {
    await store.confirmPaymentSuccess(paymentIntentId.value);
    await store.fetchBooking(route.params.id);
    goDetail(true);
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Payment confirmation failed");
  }
};

onMounted(prepare);
</script>

<style scoped>
.page {
  min-height: calc(100vh - 112px);
  padding: 24px;
  background:
    radial-gradient(circle at 5% 5%, rgba(56, 189, 248, 0.14), transparent 38%),
    radial-gradient(circle at 95% 90%, rgba(14, 116, 144, 0.18), transparent 40%),
    #f5fbff;
}

.panel {
  max-width: 860px;
  margin: 0 auto;
  border-radius: 18px;
  border: 1px solid #d7e8f8;
  background: #fff;
  box-shadow: 0 16px 36px rgba(8, 47, 73, 0.12);
  padding: 22px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-header h1 {
  margin: 0;
  color: #0f172a;
}

.panel-header p {
  margin: 8px 0 0;
  color: #475569;
}

.skeleton {
  margin-top: 16px;
}

.payment-summary {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.payment-summary > div {
  background: #f8fbff;
  border: 1px solid #dce8f6;
  border-radius: 10px;
  padding: 12px;
}

.label {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
}

strong {
  color: #0f172a;
  word-break: break-all;
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

  .payment-summary {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-wrap: wrap;
  }
}
</style>
