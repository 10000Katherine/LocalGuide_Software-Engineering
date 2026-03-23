<template>
  <main class="guide-profile-page">
    <el-skeleton :loading="loading" animated :rows="6">
      <template #default>
        <el-result
          v-if="errorMessage"
          icon="error"
          title="Unable to load guide profile"
          :sub-title="errorMessage"
        />

        <section v-else-if="guide" class="guide-profile-card">
          <header class="profile-header">
            <img v-if="guide.photoUrl" :src="guide.photoUrl" alt="Guide photo" class="avatar-photo" />
            <div v-else class="avatar">{{ initials }}</div>
            <div class="title-wrap">
              <h1>{{ guide.firstName }} {{ guide.lastName }}</h1>
              <p>{{ guide.province || "Province not set" }} | {{ guide.city || "City not set" }} | {{ guide.language || "Language not set" }}</p>
              <div class="pill-row">
                <span class="pill">{{ (guide.tours && guide.tours.length) || 0 }} tours</span>
                <span class="pill" v-if="lowestPrice !== null">From ${{ Number(lowestPrice).toFixed(2) }} per person</span>
              </div>
            </div>
            <div v-if="isOwnGuideProfile" class="header-action">
              <el-button type="primary" @click="goToEditProfile">Edit Profile</el-button>
            </div>
            <div v-else-if="canFavorite" class="header-action">
              <el-button
                plain
                :type="favoritesStore.isFavorite(guide.id) ? 'danger' : 'info'"
                @click="toggleFavorite(guide.id)">
                {{ favoritesStore.isFavorite(guide.id) ? "Unfavorite" : "Add to Favorites" }}
              </el-button>
            </div>
          </header>

          <section class="bio">
            <h2>About</h2>
            <p>{{ guide.bio || "No bio provided yet." }}</p>
          </section>

          <section>
            <h2>Tours ({{ (guide.tours && guide.tours.length) || 0 }})</h2>
            <div class="tour-grid" v-if="guide.tours && guide.tours.length">
              <article v-for="tour in guide.tours" :key="tour.id" class="tour-card">
                <h3>{{ tour.title }}</h3>
                <p>{{ tour.province || "Province not set" }} | {{ tour.city || "City not set" }} | {{ tour.category || "Category not set" }} | {{ tour.language || "Language not set" }}</p>
                <p class="desc">{{ tour.description }}</p>
                <p class="meta">${{ Number(tour.price).toFixed(2) }} per person | {{ tour.durationHours }}h | max {{ tour.groupSize }} people</p>
                <router-link :to="'/tours/' + tour.id">View details</router-link>
              </article>
            </div>
            <el-empty v-else description="No tours published yet" />
          </section>

          <section class="reviews">
            <h2>Reviews</h2>
            <p class="reviews-note">Traveler feedback appears here after completed tours.</p>
            <div v-if="reviews.length" class="reviews-list">
              <article v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-head">
                  <strong>{{ review.touristName }}</strong>
                  <span>{{ formatReviewDate(review.createdAt) }}</span>
                </div>
                <p class="review-meta">Rating: {{ review.rating }}/5 · Booking {{ review.bookingReference }}</p>
                <p>{{ review.comment }}</p>
              </article>
            </div>
            <el-empty v-else description="No reviews yet" />
          </section>
        </section>
      </template>
    </el-skeleton>
  </main>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../../../shared/stores/auth";
import { useFavoritesStore } from "../../../shared/stores/favorites";
import { getGuideProfile, getMyGuideProfile } from "../api/guideTourApi";
import { reviewAdminApi } from "../../review-admin/api/reviewAdminApi";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const favoritesStore = useFavoritesStore();

const guide = ref(null);
const myGuideId = ref(null);
const reviews = ref([]);
const loading = ref(true);
const errorMessage = ref("");

const initials = computed(() => {
  if (!guide.value) return "LG";
  const first = (guide.value.firstName || "?").charAt(0).toUpperCase();
  const last = (guide.value.lastName || "?").charAt(0).toUpperCase();
  return first + last;
});

const lowestPrice = computed(() => {
  const tours = guide.value?.tours || [];
  if (!tours.length) return null;
  return tours.reduce((min, tour) => {
    const value = Number(tour.price);
    if (Number.isNaN(value)) return min;
    return min === null ? value : Math.min(min, value);
  }, null);
});

const isOwnGuideProfile = computed(() => {
  if (!guide.value || !myGuideId.value) return false;
  return Number(guide.value.id) === Number(myGuideId.value);
});

const canFavorite = computed(() => authStore.user?.role === "TOURIST");

const goToEditProfile = () => {
  router.push("/guide/dashboard");
};

const toggleFavorite = async (guideId) => {
  try {
    const becameFavorite = await favoritesStore.toggleFavorite(guideId);
    ElMessage.success(becameFavorite ? "Guide added to favorites" : "Guide removed from favorites");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to update favorite");
  }
};

const loadViewerContext = async () => {
  if (!authStore.accessToken) {
    myGuideId.value = null;
    return;
  }

  try {
    const profile = await getMyGuideProfile();
    myGuideId.value = profile?.id || null;
  } catch (error) {
    void error;
    myGuideId.value = null;
  }
};

const loadGuide = async () => {
  loading.value = true;
  errorMessage.value = "";
  guide.value = null;
  reviews.value = [];

  try {
    const guideProfile = await getGuideProfile(route.params.id);
    guide.value = guideProfile;

    try {
      const reviewResponse = await reviewAdminApi.listGuideReviews(route.params.id);
      reviews.value = Array.isArray(reviewResponse?.data) ? reviewResponse.data : [];
    } catch (reviewError) {
      void reviewError;
      reviews.value = [];
    }
  } catch (error) {
    const message = error?.response?.data?.message || "Failed to load guide profile";
    errorMessage.value = message;
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
};

const formatReviewDate = (value) => {
  if (!value) {
    return "Recent";
  }
  return String(value).slice(0, 10);
};

watch(() => route.params.id, loadGuide, { immediate: true });
onMounted(loadViewerContext);
onMounted(async () => {
  if (authStore.accessToken && !authStore.user) {
    try {
      await authStore.loadProfile();
    } catch {
      return;
    }
  }

  if (canFavorite.value) {
    try {
      await favoritesStore.loadFavorites();
    } catch (error) {
      ElMessage.warning(error?.response?.data?.message || "Favorites are temporarily unavailable");
    }
  }
});
</script>

<style scoped>
.guide-profile-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(160deg, #f3f8ff 0%, #fff8ef 100%);
}

.guide-profile-card {
  max-width: 980px;
  margin: 0 auto;
  background: #ffffff;
  border: 1px solid #dce6f3;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
}

.profile-header {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 14px;
  border-bottom: 1px solid #e5eaf4;
  padding-bottom: 12px;
}

.title-wrap h1 {
  margin: 0;
}

.title-wrap p {
  margin: 4px 0 0;
  color: #475569;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-weight: 700;
  background: #e0f2fe;
  color: #0369a1;
}

.avatar-photo {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #dce6f3;
}

.pill-row {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  flex-wrap: wrap;
}

.pill {
  border: 1px solid #c6d7ee;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  color: #1e3a8a;
  background: #eff6ff;
}

.bio,
.reviews {
  margin-top: 20px;
}

.bio h2,
.reviews h2 {
  margin: 0 0 8px;
}

.tour-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 12px;
}

.tour-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px;
}

.tour-card h3 {
  margin: 0 0 8px;
}

.desc {
  color: #475569;
  min-height: 42px;
}

.meta {
  color: #0f766e;
  font-weight: 600;
}

.reviews-note {
  color: #64748b;
  margin-bottom: 8px;
}

.reviews-list {
  display: grid;
  gap: 10px;
}

.review-item {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 10px;
}

.review-head {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.review-meta {
  color: #475569;
  margin: 6px 0;
}

@media (max-width: 720px) {
  .profile-header {
    grid-template-columns: 1fr;
    justify-items: start;
  }
}
</style>
