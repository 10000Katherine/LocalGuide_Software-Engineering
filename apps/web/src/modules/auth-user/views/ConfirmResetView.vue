<template>
  <AuthCard title="Confirm Password Reset" subtitle="Use your reset token and new password">
    <el-form :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Reset Token">
        <el-input v-model="form.resetToken" />
      </el-form-item>
      <el-form-item label="New Password">
        <el-input v-model="form.newPassword" type="password" show-password />
      </el-form-item>
      <el-button type="primary" class="full" @click="handleSubmit">Confirm</el-button>
    </el-form>
    <div class="links">
      <router-link to="/login">Back to login</router-link>
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
const form = reactive({ resetToken: "", newPassword: "" });

const handleSubmit = async () => {
  try {
    const data = await authStore.confirmReset(form.resetToken, form.newPassword);
    ElMessage.success(data.message || "Password updated");
    router.push("/login");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Reset confirmation failed");
  }
};
</script>

<style scoped>
.full { width: 100%; }
.links { margin-top: 16px; text-align: center; }
</style>
