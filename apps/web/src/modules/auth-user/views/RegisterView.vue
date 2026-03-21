<template>
  <AuthCard title="Create Account" subtitle="Start as a tourist and discover local guides">
    <el-form :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Email">
        <el-input v-model="form.email" type="email" autocomplete="email" />
      </el-form-item>
      <el-form-item label="First Name">
        <el-input v-model="form.firstName" autocomplete="given-name" />
      </el-form-item>
      <el-form-item label="Last Name">
        <el-input v-model="form.lastName" autocomplete="family-name" />
      </el-form-item>
      <el-form-item label="Phone">
        <el-input v-model="form.phone" autocomplete="tel" />
      </el-form-item>
      <el-form-item label="Password">
        <el-input v-model="form.password" type="password" show-password autocomplete="new-password" />
      </el-form-item>
      <el-button type="primary" :loading="authStore.loading" class="full" @click="handleSubmit">Register</el-button>
    </el-form>

    <div class="links">
      <router-link to="/login">Already have an account?</router-link>
    </div>
  </AuthCard>
</template>

<script setup>
import { reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import AuthCard from "../components/AuthCard.vue";
import { useAuthStore } from "../../../shared/stores/auth";
import { extractApiErrorMessage } from "../../../shared/utils/apiError";

const router = useRouter();
const authStore = useAuthStore();
const form = reactive({ email: "", firstName: "", lastName: "", phone: "", password: "" });

const handleSubmit = async () => {
  try {
    await authStore.register(form);
    ElMessage.success("Registration successful");
    router.push("/profile");
  } catch (error) {
    ElMessage.error(extractApiErrorMessage(error, "Registration failed"));
  }
};
</script>

<style scoped>
.full { width: 100%; }
.links { margin-top: 16px; text-align: center; }
</style>
