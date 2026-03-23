<template>
  <main class="requests-page">
    <section class="requests-wrap">
      <header class="hero">
        <div>
          <p class="eyebrow">Guide Requests</p>
          <h1>Booking Requests</h1>
          <p class="sub">Review incoming requests, accept for payment, or decline.</p>
        </div>
        <el-button :loading="loading" @click="loadRequests">Refresh</el-button>
      </header>

      <section class="panel">
        <div class="toolbar">
          <el-radio-group v-model="statusFilter" size="large">
            <el-radio-button label="ALL">All</el-radio-button>
            <el-radio-button label="CREATED">Needs Decision</el-radio-button>
            <el-radio-button label="PENDING_PAYMENT">Waiting Payment</el-radio-button>
            <el-radio-button label="CONFIRMED">Confirmed</el-radio-button>
            <el-radio-button label="CANCELLED_BY_GUIDE">Declined</el-radio-button>
          </el-radio-group>
        </div>

        <el-skeleton v-if="loading" animated :rows="6" />

        <el-empty
          v-else-if="filteredRequests.length === 0"
          description="No booking requests for this filter." />

        <el-table v-else :data="filteredRequests" stripe>
          <el-table-column prop="id" label="Booking ID" width="100" />
          <el-table-column prop="tourId" label="Tour ID" width="90" />
          <el-table-column label="Date" min-width="130">
            <template #default="scope">{{ scope.row.bookingDate || "-" }}</template>
          </el-table-column>
          <el-table-column label="Time" min-width="110">
            <template #default="scope">{{ formatTime(scope.row.startTime) }}</template>
          </el-table-column>
          <el-table-column label="People" width="90">
            <template #default="scope">{{ scope.row.numPeople || 1 }}</template>
          </el-table-column>
          <el-table-column label="Total" min-width="120">
            <template #default="scope">{{ formatMoney(scope.row.totalPrice) }}</template>
          </el-table-column>
          <el-table-column label="Status" min-width="170">
            <template #default="scope">
              <BookingStatusTag :status="scope.row.status" />
            </template>
          </el-table-column>
          <el-table-column label="Actions" min-width="230" fixed="right">
            <template #default="scope">
              <div class="actions">
                <el-button
                  type="success"
                  plain
                  size="small"
                  :disabled="!canAccept(scope.row.status)"
                  :loading="actionLoadingId === scope.row.id && actionType === 'accept'"
                  @click="acceptRequest(scope.row)">
                  Accept
                </el-button>
                <el-button
                  type="danger"
                  plain
                  size="small"
                  :disabled="!canDecline(scope.row.status)"
                  :loading="actionLoadingId === scope.row.id && actionType === 'decline'"
                  @click="declineRequest(scope.row)">
                  Decline
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import BookingStatusTag from "../../booking-payment/components/BookingStatusTag.vue";
import {
  acceptGuideBookingRequest,
  declineGuideBookingRequest,
  listGuideBookingRequests
} from "../api/guideTourApi";
import { useAuthStore } from "../../../shared/stores/auth";

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const requests = ref([]);
const statusFilter = ref("ALL");
const actionLoadingId = ref(null);
const actionType = ref("");

const canAccept = (status) => String(status || "").toUpperCase() === "CREATED";
const canDecline = (status) => {
  const normalized = String(status || "").toUpperCase();
  return normalized === "CREATED" || normalized === "PENDING_PAYMENT" || normalized === "PAYMENT_FAILED";
};

const formatTime = (value) => (value ? String(value).slice(0, 5) : "-");

const formatMoney = (value) => {
  const amount = Number(value || 0);
  return new Intl.NumberFormat("en-CA", {
    style: "currency",
    currency: "CAD"
  }).format(amount);
};

const filteredRequests = computed(() => {
  if (statusFilter.value === "ALL") {
    return requests.value;
  }
  return requests.value.filter(
    (request) => String(request.status || "").toUpperCase() === statusFilter.value
  );
});

const ensureGuide = async () => {
  if (!authStore.accessToken) {
    router.push("/login");
    return false;
  }

  if (!authStore.user) {
    try {
      await authStore.loadProfile();
    } catch {
      await authStore.logout();
      router.push("/login");
      return false;
    }
  }

  if (authStore.user?.role !== "GUIDE") {
    router.push("/profile");
    return false;
  }

  return true;
};

const loadRequests = async () => {
  if (!(await ensureGuide())) {
    return;
  }

  loading.value = true;
  try {
    const data = await listGuideBookingRequests();
    requests.value = Array.isArray(data) ? data : [];
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load booking requests");
  } finally {
    loading.value = false;
  }
};

const acceptRequest = async (booking) => {
  actionLoadingId.value = booking.id;
  actionType.value = "accept";
  try {
    await acceptGuideBookingRequest(booking.id);
    ElMessage.success("Booking request accepted. Tourist can pay now.");
    await loadRequests();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to accept booking request");
  } finally {
    actionLoadingId.value = null;
    actionType.value = "";
  }
};

const declineRequest = async (booking) => {
  try {
    await ElMessageBox.confirm(
      "Decline this booking request? This action updates the request status for the tourist.",
      "Decline Booking Request",
      {
        confirmButtonText: "Decline",
        cancelButtonText: "Keep",
        type: "warning"
      }
    );
  } catch {
    return;
  }

  actionLoadingId.value = booking.id;
  actionType.value = "decline";
  try {
    await declineGuideBookingRequest(booking.id);
    ElMessage.success("Booking request declined.");
    await loadRequests();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to decline booking request");
  } finally {
    actionLoadingId.value = null;
    actionType.value = "";
  }
};

onMounted(loadRequests);
</script>

<style scoped>
.requests-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(170deg, #f4f8ff 0%, #fff8ef 100%);
}

.requests-wrap {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  gap: 14px;
}

.hero {
  background: #ffffff;
  border: 1px solid #dfe8f5;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.eyebrow {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: #64748b;
}

.hero h1 {
  margin: 6px 0;
}

.sub {
  margin: 0;
  color: #475569;
}

.panel {
  background: #ffffff;
  border: 1px solid #dfe8f5;
  border-radius: 14px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
  padding: 14px;
}

.toolbar {
  margin-bottom: 14px;
}

.actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 900px) {
  .requests-page {
    padding: 12px;
  }

  .hero {
    flex-direction: column;
    align-items: stretch;
  }

  .actions {
    flex-wrap: wrap;
  }
}
</style>
