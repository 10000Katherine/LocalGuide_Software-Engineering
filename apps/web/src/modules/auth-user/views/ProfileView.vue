<template>
  <main class="profile-layout">
    <section class="profile-card">
      <header class="profile-header">
        <h1>Tourist Profile</h1>
        <el-button plain @click="logout">Logout</el-button>
      </header>

      <el-skeleton :loading="loading" animated :rows="4">
        <template #default>
          <el-form :model="form" label-position="top" @submit.prevent="saveProfile">
            <el-form-item label="Email">
              <el-input :model-value="authStore.user?.email || ''" disabled />
            </el-form-item>
            <el-form-item label="First Name">
              <el-input v-model="form.firstName" />
            </el-form-item>
            <el-form-item label="Last Name">
              <el-input v-model="form.lastName" />
            </el-form-item>
            <el-form-item label="Phone">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-button type="primary" @click="saveProfile">Save</el-button>
          </el-form>
        </template>
      </el-skeleton>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../../shared/stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(true);
const form = reactive({ firstName: "", lastName: "", phone: "" });

const syncForm = () => {
  form.firstName = authStore.user?.firstName || "";
  form.lastName = authStore.user?.lastName || "";
  form.phone = authStore.user?.phone || "";
};

const saveProfile = async () => {
  try {
    await authStore.updateProfile(form);
    ElMessage.success("Profile updated");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to update profile");
  }
};

const logout = async () => {
  await authStore.logout();
  router.push("/login");
};

onMounted(async () => {
  try {
    await authStore.loadProfile();
    syncForm();
  } catch {
    await authStore.clearAuth();
    router.push("/login");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.profile-layout {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #f8fafc 0%, #eef2ff 100%);
  padding: 24px;
}

.profile-card {
  width: 100%;
  max-width: 560px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #d9e2ef;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.12);
  padding: 28px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

h1 {
  margin: 0;
  color: #0f172a;
}
</style>
