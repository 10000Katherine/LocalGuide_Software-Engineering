<template>
  <AuthCard title="Reset Password" subtitle="Request a secure reset link by email">
    <el-form :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Email">
        <el-input v-model="form.email" type="email" />
      </el-form-item>
      <el-button type="primary" class="full" @click="handleSubmit">Request Reset</el-button>
    </el-form>

    <el-alert v-if="submitted" class="result" type="success" :closable="false"
              title="If your account exists, reset instructions will be sent." />
  </AuthCard>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import AuthCard from "../components/AuthCard.vue";
import { useAuthStore } from "../../../shared/stores/auth";
import { extractApiErrorMessage } from "../../../shared/utils/apiError";

const authStore = useAuthStore();
const form = reactive({ email: "" });
const submitted = ref(false);

const handleSubmit = async () => {
  try {
    const data = await authStore.requestReset(form.email);
    submitted.value = true;
    ElMessage.success(data.message || "Reset request submitted");
  } catch (error) {
    ElMessage.error(extractApiErrorMessage(error, "Reset request failed"));
  }
};
</script>

<style scoped>
.full { width: 100%; }
.result { margin-top: 16px; }
</style>
