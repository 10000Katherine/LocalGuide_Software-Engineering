<template>
  <AuthCard title="Welcome Back" subtitle="Login to your LocalGuide account">
    <el-form :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Email">
        <el-input v-model="form.email" type="email" autocomplete="email" />
      </el-form-item>
      <el-form-item label="Password">
        <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
      </el-form-item>
      <el-button type="primary" :loading="authStore.loading" class="full" @click="handleSubmit">Login</el-button>
    </el-form>

    <div class="links">
      <router-link to="/register">Create account</router-link>
      <router-link to="/reset-password/request">Forgot password</router-link>
    </div>
  </AuthCard>
</template>

<script setup>
import { reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import AuthCard from "../components/AuthCard.vue";
import { useAuthStore } from "../../../shared/stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const form = reactive({ email: "", password: "" });

const handleSubmit = async () => {
  try {
    await authStore.login(form);
    await authStore.loadProfile();
    ElMessage.success("Login successful");
    router.push("/profile");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Login failed");
  }
};
</script>

<style scoped>
.full { width: 100%; }
.links {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
}
</style>
