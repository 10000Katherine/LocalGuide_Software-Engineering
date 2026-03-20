<template>
  <section class="card">
    <div class="header">
      <div>
        <h2>{{ title }}</h2>
        <p v-if="subtitle">{{ subtitle }}</p>
      </div>
      <slot name="actions" />
    </div>
    <slot />
  </section>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: "" }
});
</script>

<style scoped>
.card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
  border: 1px solid #e2e8f0;
}
.header {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}
.header h2 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}
.header p {
  margin: 6px 0 0;
  color: #64748b;
}
</style>
