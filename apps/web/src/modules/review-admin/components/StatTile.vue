<template>
  <div class="tile">
    <span class="label">{{ label }}</span>
    <strong>{{ value }}</strong>
  </div>
</template>

<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true }
});
</script>

<style scoped>
.tile {
  background: #f8fafc;
  border: 1px solid #dbeafe;
  border-radius: 16px;
  padding: 18px;
}
.label {
  display: block;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 8px;
}
strong {
  font-size: 28px;
  color: #0f172a;
}
</style>
