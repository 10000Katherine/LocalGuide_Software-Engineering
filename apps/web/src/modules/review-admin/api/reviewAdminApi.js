import apiClient from "../../../shared/api/client";

export const reviewAdminApi = {
  listFavorites() {
    return apiClient.get("/tourists/me/favorites");
  },
  addFavorite(guideId) {
    return apiClient.post("/tourists/me/favorites", { guideId });
  },
  removeFavorite(guideId) {
    return apiClient.delete(`/tourists/me/favorites/${guideId}`);
  },
  createReview(payload) {
    return apiClient.post("/reviews", payload);
  },
  listGuideReviews(guideId) {
    return apiClient.get(`/guides/${guideId}/reviews`);
  },
  getGuideRatingSummary(guideId) {
    return apiClient.get(`/guides/${guideId}/ratings/summary`);
  },
  replyToReview(reviewId, reply) {
    return apiClient.put(`/reviews/${reviewId}/reply`, { reply });
  },
  submitVerification(payload) {
    return apiClient.post("/guide-verifications/me", payload);
  },
  getMyVerification() {
    return apiClient.get("/guide-verifications/me");
  },
  listPendingVerifications() {
    return apiClient.get("/admin/verifications");
  },
  reviewVerification(requestId, payload) {
    return apiClient.put(`/admin/verifications/${requestId}`, payload);
  },
  listUsers() {
    return apiClient.get("/admin/users");
  },
  updateUserStatus(userId, active) {
    return apiClient.put(`/admin/users/${userId}`, { active });
  },
  getAnalytics() {
    return apiClient.get("/admin/analytics");
  },
  getGuideEarnings(guideId) {
    return apiClient.get(`/admin/guides/${guideId}/earnings`);
  }
};
