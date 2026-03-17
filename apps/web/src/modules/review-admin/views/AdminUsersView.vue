<template>
  <div class="page">
    <SectionCard title="Admin user management" subtitle="Suspend or reactivate user accounts.">
      <p v-if="message" class="message">{{ message }}</p>
      <table v-if="users.length">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Active</th>
            <th>Verification</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.firstName }} {{ user.lastName }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.role }}</td>
            <td>{{ user.active ? 'Yes' : 'No' }}</td>
            <td>{{ user.verificationStatus }}</td>
            <td>
              <button @click="toggle(user)">{{ user.active ? 'Suspend' : 'Activate' }}</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">No users found.</p>
    </SectionCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import SectionCard from "../components/SectionCard.vue";
import { reviewAdminApi } from "../api/reviewAdminApi";

const users = ref([]);
const message = ref("");

const loadUsers = async () => {
  try {
    const { data } = await reviewAdminApi.listUsers();
    users.value = data;
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to load users.";
  }
};

const toggle = async (user) => {
  try {
    await reviewAdminApi.updateUserStatus(user.id, !user.active);
    message.value = "User status updated.";
    await loadUsers();
  } catch (error) {
    message.value = error?.response?.data?.message || "Unable to update user.";
  }
};

onMounted(loadUsers);
</script>

<style scoped>
.page { padding: 32px; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 12px; border-bottom: 1px solid #e2e8f0; text-align: left; }
button { padding: 8px 12px; border-radius: 8px; border: 1px solid #cbd5e1; background: #0f172a; color: white; cursor: pointer; }
.message { color: #0f766e; }
.empty { color: #64748b; }
</style>
