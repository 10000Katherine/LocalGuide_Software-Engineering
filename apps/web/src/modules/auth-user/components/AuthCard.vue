<template>
  <main class="layout">
    <section class="card">
      <header>
        <h1>{{ title }}</h1>
        <p>{{ subtitle }}</p>
      </header>
      <slot />
    </section>
  </main>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: "" }
});
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #edf3ff 0%, #f7fafc 100%);
  padding: 24px;
}

.card {
  width: 100%;
  max-width: 460px;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #d8e1f0;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.1);
  padding: 28px;
}

h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #0f172a;
}

p {
  margin: 8px 0 20px;
  color: #334155;
}
</style>
