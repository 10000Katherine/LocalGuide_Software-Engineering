<template>
  <main class="availability-page">
    <section class="layout">
      <article class="card">
        <h1>Availability Calendar</h1>

        <el-form :model="form" label-position="top" @submit.prevent="saveSlot">
          <el-date-picker
            v-model="form.slotDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="Date"
            :disabled-date="isPastDate"
            style="width: 100%;"
          />
          <div class="time-grid">
            <el-time-select
              v-model="form.startTime"
              :start="startTimeLowerBound"
              step="00:30"
              end="23:30"
              placeholder="Start"
            />
            <el-time-select
              v-model="form.endTime"
              :start="endTimeLowerBound"
              :min-time="form.startTime || undefined"
              step="00:30"
              end="23:59"
              placeholder="End"
            />
          </div>

          <div class="actions">
            <el-button type="primary" @click="saveSlot">{{ form.id ? "Update" : "Add Slot" }}</el-button>
            <el-button v-if="form.id" @click="resetForm">Cancel Edit</el-button>
          </div>
        </el-form>

        <el-divider />

        <el-table :data="slots" empty-text="No availability slots set yet">
          <el-table-column prop="slotDate" label="Date" width="140" />
          <el-table-column prop="startTime" label="Start" width="120" />
          <el-table-column prop="endTime" label="End" width="120" />
          <el-table-column label="Actions" width="170">
            <template #default="scope">
              <el-button link type="primary" @click="editSlot(scope.row)">Edit</el-button>
              <el-button link type="danger" @click="removeSlot(scope.row)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </article>

      <article class="card">
        <h2>Calendar View</h2>
        <el-calendar>
          <template #date-cell="cell">
            <div class="date-cell">
              <span>{{ dayNumber(cell.data.day) }}</span>
              <small v-if="slotCounts[cell.data.day]">{{ slotCounts[cell.data.day] }} slot(s)</small>
            </div>
          </template>
        </el-calendar>
      </article>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../../../shared/stores/auth";
import {
  createAvailability,
  deleteAvailability,
  listMyAvailability,
  updateAvailability
} from "../api/guideTourApi";

const router = useRouter();
const authStore = useAuthStore();

const slots = ref([]);
const form = reactive({ id: null, slotDate: "", startTime: "", endTime: "" });

const slotCounts = computed(() => {
  return slots.value.reduce((acc, slot) => {
    acc[slot.slotDate] = (acc[slot.slotDate] || 0) + 1;
    return acc;
  }, {});
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

const normalizeTime = (value) => {
  if (!value) return value;
  if (value.length === 5) return value + ":00";
  return value;
};

const formatDateLocal = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return year + "-" + month + "-" + day;
};

const formatTimeLocal = (date) => {
  const hour = String(date.getHours()).padStart(2, "0");
  const minute = String(date.getMinutes()).padStart(2, "0");
  return hour + ":" + minute;
};

const roundUpToNextHalfHour = (date) => {
  const value = new Date(date);
  value.setSeconds(0, 0);
  const minutes = value.getMinutes();
  const roundedMinutes = minutes <= 30 ? 30 : 60;
  value.setMinutes(roundedMinutes);
  return value;
};

const todayIso = () => formatDateLocal(new Date());

const currentTimeRounded = () => {
  const rounded = roundUpToNextHalfHour(new Date());
  const text = formatTimeLocal(rounded);
  return text > "23:30" ? "23:30" : text;
};

const startTimeLowerBound = computed(() => {
  if (form.slotDate && form.slotDate === todayIso()) {
    return currentTimeRounded();
  }
  return "06:00";
});

const endTimeLowerBound = computed(() => {
  if (form.slotDate && form.slotDate === todayIso()) {
    const startBound = currentTimeRounded();
    return startBound < "23:30" ? startBound : "23:30";
  }
  return "06:30";
});

const isPastDate = (date) => {
  const candidate = new Date(date);
  candidate.setHours(0, 0, 0, 0);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return candidate.getTime() < today.getTime();
};

const resetForm = () => {
  form.id = null;
  form.slotDate = "";
  form.startTime = "";
  form.endTime = "";
};

const loadSlots = async () => {
  if (!(await ensureGuide())) return;
  try {
    slots.value = await listMyAvailability();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load availability");
  }
};

const saveSlot = async () => {
  if (!form.slotDate || !form.startTime || !form.endTime) {
    ElMessage.warning("Please select date, start time, and end time.");
    return;
  }

  if (form.slotDate < todayIso()) {
    ElMessage.warning("Past dates are not allowed.");
    return;
  }

  if (form.slotDate === todayIso() && String(form.startTime).slice(0, 5) < formatTimeLocal(new Date())) {
    ElMessage.warning("Past times are not allowed for today.");
    return;
  }

  const payload = {
    slotDate: form.slotDate,
    startTime: normalizeTime(form.startTime),
    endTime: normalizeTime(form.endTime)
  };

  try {
    if (form.id) {
      await updateAvailability(form.id, payload);
      ElMessage.success("Availability updated");
    } else {
      await createAvailability(payload);
      ElMessage.success("Availability added");
    }
    resetForm();
    await loadSlots();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to save availability");
  }
};

const editSlot = (slot) => {
  form.id = slot.id;
  form.slotDate = slot.slotDate;
  form.startTime = String(slot.startTime).slice(0, 5);
  form.endTime = String(slot.endTime).slice(0, 5);
};

const removeSlot = async (slot) => {
  if (!window.confirm("Delete this availability slot?")) return;
  try {
    await deleteAvailability(slot.id);
    ElMessage.success("Availability deleted");
    await loadSlots();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to delete availability");
  }
};

const dayNumber = (day) => String(day || "").slice(-2);

onMounted(loadSlots);
</script>

<style scoped>
.availability-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(165deg, #f7fbff 0%, #fff7ef 100%);
}

.layout {
  max-width: 1160px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.card {
  background: #fff;
  border: 1px solid #dfe6f4;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.card h1,
.card h2 {
  margin-top: 0;
}

.time-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.date-cell {
  display: grid;
  gap: 4px;
}

.date-cell small {
  font-size: 10px;
  color: #0f766e;
}

@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
}
</style>
