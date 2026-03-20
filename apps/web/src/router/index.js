import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../shared/stores/auth";
import LoginView from "../modules/auth-user/views/LoginView.vue";
import RegisterView from "../modules/auth-user/views/RegisterView.vue";
import RequestResetView from "../modules/auth-user/views/RequestResetView.vue";
import ConfirmResetView from "../modules/auth-user/views/ConfirmResetView.vue";
import ProfileView from "../modules/auth-user/views/ProfileView.vue";
import HomeView from "../modules/guide-tour/views/HomeView.vue";
import SearchResultsView from "../modules/guide-tour/views/SearchResultsView.vue";
import GuideProfileView from "../modules/guide-tour/views/GuideProfileView.vue";
import TourDetailView from "../modules/guide-tour/views/TourDetailView.vue";
import GuideDashboardView from "../modules/guide-tour/views/GuideDashboardView.vue";
import TourManagementView from "../modules/guide-tour/views/TourManagementView.vue";
import AvailabilityCalendarView from "../modules/guide-tour/views/AvailabilityCalendarView.vue";
import GuideWorkspaceView from "../modules/guide-tour/views/GuideWorkspaceView.vue";
import FavoritesView from "../modules/review-admin/views/FavoritesView.vue";
import ReviewsView from "../modules/review-admin/views/ReviewsView.vue";
import GuideVerificationView from "../modules/review-admin/views/GuideVerificationView.vue";
import AdminDashboardView from "../modules/review-admin/views/AdminDashboardView.vue";
import AdminVerificationsView from "../modules/review-admin/views/AdminVerificationsView.vue";
import AdminUsersView from "../modules/review-admin/views/AdminUsersView.vue";

const routes = [
  { path: "/", name: "home", component: HomeView },
  { path: "/search", name: "search-results", component: SearchResultsView },
  { path: "/guides/:id", name: "guide-profile", component: GuideProfileView },
  { path: "/tours/:id", name: "tour-detail", component: TourDetailView },
  { path: "/login", name: "login", component: LoginView },
  { path: "/register", name: "register", component: RegisterView },
  { path: "/reset-password/request", name: "request-reset", component: RequestResetView },
  { path: "/reset-password/confirm", name: "confirm-reset", component: ConfirmResetView },
  { path: "/profile", name: "profile", component: ProfileView, meta: { requiresAuth: true } },
  {
    path: "/guide/workspace",
    name: "guide-workspace",
    component: GuideWorkspaceView,
    meta: { requiresAuth: true, requiresGuide: true }
  },
  {
    path: "/guide/dashboard",
    name: "guide-dashboard",
    component: GuideDashboardView,
    meta: { requiresAuth: true, requiresGuide: true }
  },
  {
    path: "/guide/tours",
    name: "guide-tours",
    component: TourManagementView,
    meta: { requiresAuth: true, requiresGuide: true }
  },
  {
    path: "/guide/availability",
    name: "guide-availability",
    component: AvailabilityCalendarView,
    meta: { requiresAuth: true, requiresGuide: true }
  },
  {
    path: "/bookings",
    name: "booking-list",
    component: () => import("../modules/booking-payment/views/BookingListView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/bookings/new",
    name: "booking-create",
    component: () => import("../modules/booking-payment/views/BookingCreateView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/bookings/:id",
    name: "booking-detail",
    component: () => import("../modules/booking-payment/views/BookingDetailView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/bookings/:id/pay",
    name: "booking-pay",
    component: () => import("../modules/booking-payment/views/PaymentView.vue"),
    meta: { requiresAuth: true }
  },
  { path: "/favorites", name: "favorites", component: FavoritesView, meta: { requiresAuth: true } },
  { path: "/reviews", name: "reviews", component: ReviewsView, meta: { requiresAuth: true } },
  { path: "/guide-verification", name: "guide-verification", component: GuideVerificationView, meta: { requiresAuth: true } },
  { path: "/admin/dashboard", name: "admin-dashboard", component: AdminDashboardView, meta: { requiresAuth: true } },
  { path: "/admin/verifications", name: "admin-verifications", component: AdminVerificationsView, meta: { requiresAuth: true } },
  { path: "/admin/users", name: "admin-users", component: AdminUsersView, meta: { requiresAuth: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach(async (to) => {
  const authStore = useAuthStore();
  if (authStore.accessToken && !authStore.user) {
    try {
      await authStore.loadProfile();
    } catch {
      authStore.clearAuth();
    }
  }

  const isLoggedIn = Boolean(authStore.accessToken);
  const isGuide = authStore.user?.role === "GUIDE";

  if ((to.meta.requiresAuth || to.meta.requiresGuide) && !isLoggedIn) {
    return { name: "login" };
  }

  if (to.meta.requiresGuide && !isGuide) {
    return { name: "profile" };
  }

  if ((to.name === "login" || to.name === "register") && isLoggedIn) {
    return isGuide ? { name: "guide-dashboard" } : { name: "profile" };
  }

  return true;
});

export default router;
