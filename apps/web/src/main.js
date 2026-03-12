import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import App from "./App.vue";
import router from "./router";
import { useAuthStore } from "./shared/stores/auth";

const app = createApp(App);
const pinia = createPinia();

app.use(pinia).use(router).use(ElementPlus);

const authStore = useAuthStore(pinia);
window.addEventListener("auth:logout", () => {
  authStore.clearAuth();
  if (router.currentRoute.value.name !== "login") {
    router.push("/login");
  }
});

app.mount("#app");
