import { defineStore } from "pinia";
import apiClient from "../api/client";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    accessToken: localStorage.getItem("accessToken") || "",
    refreshToken: localStorage.getItem("refreshToken") || "",
    user: null,
    loading: false
  }),
  actions: {
    persistTokens(accessToken, refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
    },
    clearAuth() {
      this.accessToken = "";
      this.refreshToken = "";
      this.user = null;
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    },
    async register(payload) {
      this.loading = true;
      try {
        const { data } = await apiClient.post("/auth/register", payload);
        this.persistTokens(data.accessToken, data.refreshToken);
        this.user = data.user;
      } finally {
        this.loading = false;
      }
    },
    async login(payload) {
      this.loading = true;
      try {
        const { data } = await apiClient.post("/auth/login", payload);
        this.persistTokens(data.accessToken, data.refreshToken);
        this.user = data.user;
      } finally {
        this.loading = false;
      }
    },
    async refresh() {
      if (!this.refreshToken) {
        return;
      }
      const { data } = await apiClient.post("/auth/refresh", { refreshToken: this.refreshToken });
      this.persistTokens(data.accessToken, data.refreshToken);
      this.user = data.user;
    },
    async requestReset(email) {
      const { data } = await apiClient.post("/auth/reset-password/request", { email });
      return data;
    },
    async confirmReset(resetToken, newPassword) {
      const { data } = await apiClient.post("/auth/reset-password/confirm", { resetToken, newPassword });
      return data;
    },
    async loadProfile() {
      if (!this.accessToken) {
        return;
      }
      const { data } = await apiClient.get("/tourists/me");
      this.user = data;
    },
    async updateProfile(payload) {
      const { data } = await apiClient.put("/tourists/me", payload);
      this.user = data;
    },
    async logout() {
      this.clearAuth();
    }
  }
});
