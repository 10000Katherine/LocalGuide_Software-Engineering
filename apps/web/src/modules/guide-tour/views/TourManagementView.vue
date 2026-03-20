<template>
  <main class="manage-page">
    <section class="manage-wrap">
      <header class="header">
        <h1>Tour Management</h1>
        <el-button type="primary" @click="openCreate">Create Tour</el-button>
      </header>

      <el-table :data="tours" stripe empty-text="No tours yet">
        <el-table-column prop="title" label="Title" />
        <el-table-column prop="province" label="Province" />
        <el-table-column prop="city" label="City" />
        <el-table-column prop="category" label="Category" />
        <el-table-column prop="language" label="Language" />
        <el-table-column prop="price" label="Price">
          <template #default="scope">${{ Number(scope.row.price).toFixed(2) }} per person</template>
        </el-table-column>
        <el-table-column label="Actions" width="190">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">Edit</el-button>
            <el-button link type="danger" @click="removeTour(scope.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? 'Edit Tour' : 'Create Tour'" width="680px">
      <el-form :model="form" label-position="top" @submit.prevent="saveTour">
        <el-form-item label="Title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="Description"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <div class="grid-two">
          <el-form-item label="Category">
            <el-select v-model="form.category" placeholder="Select category">
              <el-option v-for="category in CATEGORIES" :key="category" :label="category" :value="category" />
            </el-select>
          </el-form-item>
          <el-form-item label="Province">
            <el-select v-model="form.province" placeholder="Select province" @change="onProvinceChange">
              <el-option v-for="province in PROVINCES" :key="province" :label="province" :value="province" />
            </el-select>
          </el-form-item>
        </div>
        <div class="grid-two">
          <el-form-item label="City">
            <el-select v-model="form.city" placeholder="Select city">
              <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
            </el-select>
          </el-form-item>
          <el-form-item label="Language">
            <el-select v-model="form.language" placeholder="Select language">
              <el-option v-for="language in LANGUAGES" :key="language" :label="language" :value="language" />
            </el-select>
          </el-form-item>
        </div>
        <div class="grid-two">
          <el-form-item label="Group Size"><el-input-number v-model="form.groupSize" :min="1" /></el-form-item>
          <el-form-item label="Duration (hours)"><el-input-number v-model="form.durationHours" :min="0.5" :step="0.5" /></el-form-item>
        </div>
        <div class="grid-two">
          <el-form-item label="Price (per person)"><el-input-number v-model="form.price" :min="0" :step="10" /></el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="saveTour">Save</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../../../shared/stores/auth";
import { createTour, deleteTour, listMyTours, updateTour } from "../api/guideTourApi";
import {
  CATEGORIES,
  DEFAULT_PROVINCE,
  LANGUAGES,
  PROVINCES,
  citiesForProvince
} from "../../../shared/constants/canadaOptions";

const router = useRouter();
const authStore = useAuthStore();

const tours = ref([]);
const dialogVisible = ref(false);
const editingId = ref(null);

const form = reactive({
  title: "",
  description: "",
  category: "",
  province: DEFAULT_PROVINCE,
  city: "",
  language: "",
  price: 0,
  durationHours: 1,
  groupSize: 1
});

const cityOptions = computed(() => citiesForProvince(form.province));

const onProvinceChange = () => {
  if (!cityOptions.value.includes(form.city)) {
    form.city = "";
  }
};

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

const resetForm = () => {
  form.title = "";
  form.description = "";
  form.category = "";
  form.province = DEFAULT_PROVINCE;
  form.city = "";
  form.language = "";
  form.price = 0;
  form.durationHours = 1;
  form.groupSize = 1;
};

const loadTours = async () => {
  if (!(await ensureGuide())) return;
  try {
    tours.value = await listMyTours();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load tours");
  }
};

const openCreate = () => {
  editingId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (tour) => {
  editingId.value = tour.id;
  form.title = tour.title;
  form.description = tour.description;
  form.category = tour.category || "";
  form.province = tour.province || DEFAULT_PROVINCE;
  form.city = tour.city || "";
  form.language = tour.language || "";
  form.price = Number(tour.price);
  form.durationHours = Number(tour.durationHours);
  form.groupSize = Number(tour.groupSize);
  onProvinceChange();
  dialogVisible.value = true;
};

const saveTour = async () => {
  if (!form.title.trim() || !form.description.trim() || !form.category || !form.province || !form.city || !form.language) {
    ElMessage.warning("Please fill all required fields including category, province, city, and language.");
    return;
  }

  const payload = {
    title: form.title,
    description: form.description,
    category: form.category,
    province: form.province,
    city: form.city,
    language: form.language,
    price: Number(form.price),
    durationHours: Number(form.durationHours),
    groupSize: Number(form.groupSize)
  };

  try {
    if (editingId.value) {
      await updateTour(editingId.value, payload);
      ElMessage.success("Tour updated");
    } else {
      await createTour(payload);
      ElMessage.success("Tour created");
    }
    dialogVisible.value = false;
    await loadTours();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to save tour");
  }
};

const removeTour = async (tour) => {
  if (!window.confirm("Delete this tour?")) return;
  try {
    await deleteTour(tour.id);
    ElMessage.success("Tour deleted");
    await loadTours();
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to delete tour");
  }
};

onMounted(loadTours);
</script>

<style scoped>
.manage-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(170deg, #f7fbff 0%, #fff7ef 100%);
}

.manage-wrap {
  max-width: 1100px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid #dfe6f4;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.header h1 {
  margin: 0;
}

.grid-two {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

@media (max-width: 760px) {
  .grid-two {
    grid-template-columns: 1fr;
  }
}
</style>
