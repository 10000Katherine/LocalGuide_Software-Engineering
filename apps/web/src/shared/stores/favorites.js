import { defineStore } from "pinia";
import { reviewAdminApi } from "../../modules/review-admin/api/reviewAdminApi";

export const useFavoritesStore = defineStore("favorites", {
  state: () => ({
    favoriteGuideIds: [],
    loading: false
  }),
  getters: {
    isFavorite: (state) => (guideId) => state.favoriteGuideIds.includes(Number(guideId))
  },
  actions: {
    setFavoriteIds(ids = []) {
      const normalized = ids
        .map((value) => Number(value))
        .filter((value) => Number.isFinite(value));
      this.favoriteGuideIds = [...new Set(normalized)];
    },
    async loadFavorites() {
      this.loading = true;
      try {
        const { data } = await reviewAdminApi.listFavorites();
        const ids = Array.isArray(data) ? data.map((item) => item.guideId) : [];
        this.setFavoriteIds(ids);
      } finally {
        this.loading = false;
      }
    },
    async addFavorite(guideId) {
      await reviewAdminApi.addFavorite(Number(guideId));
      if (!this.isFavorite(guideId)) {
        this.favoriteGuideIds = [...this.favoriteGuideIds, Number(guideId)];
      }
    },
    async removeFavorite(guideId) {
      await reviewAdminApi.removeFavorite(Number(guideId));
      this.favoriteGuideIds = this.favoriteGuideIds.filter((id) => id !== Number(guideId));
    },
    async toggleFavorite(guideId) {
      if (this.isFavorite(guideId)) {
        await this.removeFavorite(guideId);
        return false;
      }

      await this.addFavorite(guideId);
      return true;
    }
  }
});
