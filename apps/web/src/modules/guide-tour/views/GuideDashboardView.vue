<template>
  <main class="dashboard-page">
    <el-skeleton :loading="loading" animated :rows="8">
      <template #default>
        <section class="dashboard-wrap">
          <header class="header">
            <div>
              <h1>Guide Dashboard</h1>
              <p>Welcome back, {{ authStore.user?.firstName || "Guide" }}</p>
            </div>
            <el-tag type="info">{{ profileSnapshot.verificationStatus || "PENDING" }}</el-tag>
          </header>

          <div class="stats-grid">
            <article class="stat-card stat-bookings">
              <span>Bookings This Month</span>
              <strong>{{ stats.bookingsThisMonth }}</strong>
            </article>
            <article class="stat-card stat-earnings">
              <span>Earnings</span>
              <strong>${{ stats.earnings.toFixed(2) }}</strong>
            </article>
            <article class="stat-card stat-rating">
              <span>Avg. Rating</span>
              <strong>{{ stats.avgRating }}</strong>
            </article>
            <article class="stat-card stat-reviews">
              <span>Reviews</span>
              <strong>{{ stats.reviews }}</strong>
            </article>
          </div>

          <section class="card">
            <h2>Upcoming Bookings</h2>
            <el-table :data="upcomingBookings" empty-text="No upcoming bookings yet">
              <el-table-column prop="touristName" label="Tourist Name" min-width="170" />
              <el-table-column prop="tour" label="Tour" min-width="170" />
              <el-table-column prop="date" label="Date" width="130" />
              <el-table-column prop="people" label="People" width="90" />
              <el-table-column prop="status" label="Status" width="130">
                <template #default="scope">
                  <span :class="['status-text', isConfirmedStatus(scope.row.status) ? 'ok' : 'warn']">
                    {{ scope.row.status }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="Action" width="130">
                <template #default="scope">
                  <el-button link type="primary" @click="openBookingDetails(scope.row)">View</el-button>
                </template>
              </el-table-column>
            </el-table>
          </section>

          <section class="card">
            <h2>Recent Reviews</h2>
            <div v-if="recentReviews.length" class="review-list">
              <article v-for="review in recentReviews" :key="review.id" class="review-item">
                <div class="review-head">
                  <strong>{{ review.touristName }}</strong>
                  <span>{{ review.date }}</span>
                </div>
                <p class="review-meta">{{ review.tour }} | {{ review.rating }}/5</p>
                <p>{{ review.comment }}</p>
              </article>
            </div>
            <el-empty v-else description="No reviews yet" />
          </section>

          <section class="card profile-card">
            <div class="profile-head">
              <h2>Guide Profile</h2>
              <el-button type="primary" @click="profileDialogVisible = true">Edit Profile</el-button>
            </div>

            <div class="profile-summary">
              <img v-if="profileSnapshot.photoUrl" :src="profileSnapshot.photoUrl" alt="Guide profile" class="profile-photo" />
              <div v-else class="photo-placeholder">No photo</div>
              <div>
                <p><strong>Province:</strong> {{ profileSnapshot.province || "Not set" }}</p>
                <p><strong>City:</strong> {{ profileSnapshot.city || "Not set" }}</p>
                <p><strong>Language:</strong> {{ profileSnapshot.language || "Not set" }}</p>
                <p><strong>Bio:</strong> {{ profileSnapshot.bio || "No bio yet" }}</p>
              </div>
            </div>
          </section>
        </section>
      </template>
    </el-skeleton>

    <el-dialog v-model="bookingDialogVisible" title="Confirmed Booking Detail" width="720px">
      <div v-if="selectedBooking" class="booking-detail-grid">
        <p><strong>Tourist:</strong> {{ selectedBooking.touristName }}</p>
        <p><strong>Tour:</strong> {{ selectedBooking.tour }}</p>
        <p><strong>Date:</strong> {{ selectedBooking.date || "N/A" }}</p>
        <p><strong>Time:</strong> {{ selectedBooking.timeLabel || "N/A" }}</p>
        <p><strong>People:</strong> {{ selectedBooking.people }}</p>
        <p><strong>Status:</strong> {{ selectedBooking.status }}</p>
        <p><strong>Province:</strong> {{ selectedBooking.province || "N/A" }}</p>
        <p><strong>City:</strong> {{ selectedBooking.city || "N/A" }}</p>
        <p><strong>Category:</strong> {{ selectedBooking.category || "N/A" }}</p>
        <p><strong>Language:</strong> {{ selectedBooking.language || "N/A" }}</p>
        <p><strong>Duration:</strong> {{ selectedBooking.durationHours || "N/A" }} hours</p>
        <p><strong>Price:</strong> ${{ selectedBooking.pricePerPerson.toFixed(2) }} per person</p>
        <p><strong>Total:</strong> ${{ selectedBooking.totalPrice.toFixed(2) }}</p>
        <p class="full-row"><strong>Description:</strong> {{ selectedBooking.description || "N/A" }}</p>
      </div>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">Close</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="profileDialogVisible" title="Edit Guide Profile" width="680px">
      <el-form :model="profileForm" label-position="top" @submit.prevent="saveProfile">
        <el-form-item label="Profile Photo">
          <div class="photo-row">
            <img v-if="profileForm.photoUrl" :src="profileForm.photoUrl" alt="Guide profile" class="profile-photo" />
            <div v-else class="photo-placeholder">No photo</div>
            <input type="file" accept="image/*" @change="onPhotoChange" />
          </div>
          <small class="hint">Accepted: JPG, PNG, GIF, WEBP (max 5MB)</small>
        </el-form-item>

        <div class="grid-two">
          <el-form-item label="Province">
            <el-select v-model="profileForm.province" placeholder="Select province" @change="onProvinceChange">
              <el-option v-for="province in PROVINCES" :key="province" :label="province" :value="province" />
            </el-select>
          </el-form-item>

          <el-form-item label="City">
            <el-select v-model="profileForm.city" placeholder="Select city">
              <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="Language">
          <el-select v-model="profileForm.language" placeholder="Select language">
            <el-option v-for="language in LANGUAGES" :key="language" :label="language" :value="language" />
          </el-select>
        </el-form-item>

        <el-form-item label="Bio">
          <el-input v-model="profileForm.bio" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="profileDialogVisible = false">Cancel</el-button>
        <el-button v-if="uploadingPhoto" type="info" plain disabled>Uploading photo...</el-button>
        <el-button type="primary" @click="saveProfile">Save Profile</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../../../shared/stores/auth";
import {
  LANGUAGES,
  PROVINCES,
  citiesForProvince
} from "../../../shared/constants/canadaOptions";
import {
  getMyGuideProfile,
  listMyConfirmedBookingRequests,
  listMyTours,
  uploadGuidePhoto,
  updateGuideProfile
} from "../api/guideTourApi";
import { reviewAdminApi } from "../../review-admin/api/reviewAdminApi";

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(true);
const uploadingPhoto = ref(false);
const profileDialogVisible = ref(false);
const bookingDialogVisible = ref(false);

const tours = ref([]);
const bookingRequests = ref([]);
const recentReviews = ref([]);
const selectedBooking = ref(null);

const profileSnapshot = reactive({
  province: "",
  city: "",
  language: "",
  bio: "",
  photoUrl: "",
  avgRating: 0,
  totalReviews: 0,
  verificationStatus: "PENDING"
});

const profileForm = reactive({
  province: "",
  city: "",
  language: "",
  bio: "",
  photoUrl: ""
});

const cityOptions = computed(() => citiesForProvince(profileForm.province));

const onProvinceChange = () => {
  if (!cityOptions.value.includes(profileForm.city)) {
    profileForm.city = "";
  }
};

const ensureGuide = async () => {
  if (!authStore.accessToken) {
    router.push("/login");
    return false;
  }
  if (!authStore.user) {
    try {
      await authStore.loadProfile();
    } catch {
      await authStore.logout();
      router.push("/login");
      return false;
    }
  }
  if (authStore.user?.role !== "GUIDE") {
    router.push("/profile");
    return false;
  }
  return true;
};

const isConfirmedStatus = (status) => String(status || "").toUpperCase() === "CONFIRMED";

const safeNumber = (value, fallback = 0) => {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : fallback;
};

const normalizeDate = (value) => {
  if (!value) return "";
  const text = String(value);
  return text.includes("T") ? text.slice(0, 10) : text.slice(0, 10);
};

const normalizeTime = (value) => {
  if (!value) return "";
  return String(value).slice(0, 5);
};

const touristLabel = (booking) => {
  if (booking?.touristName) return booking.touristName;
  const firstName = booking?.tourist?.firstName || booking?.touristUser?.firstName || "";
  const lastName = booking?.tourist?.lastName || booking?.touristUser?.lastName || "";
  const fullName = (firstName + " " + lastName).trim();
  return fullName || "Tourist";
};

const mapBooking = (booking) => {
  const tourNode = booking?.tour || {};
  const tourId = booking?.tourId || tourNode?.id || null;
  const fallbackTour = tours.value.find((tour) => Number(tour.id) === Number(tourId));
  const startTime = normalizeTime(booking?.startTime || booking?.selectedTime || booking?.tourStartTime);
  const endTime = normalizeTime(booking?.endTime || booking?.tourEndTime);
  const date = normalizeDate(booking?.bookingDate || booking?.tourDate || booking?.date);
  const people = safeNumber(booking?.numPeople || booking?.people || booking?.peopleCount, 1);
  const pricePerPerson = safeNumber(booking?.pricePerPerson || tourNode?.price || fallbackTour?.price, 0);
  const totalPrice = safeNumber(booking?.totalPrice, pricePerPerson * people);

  return {
    id: booking?.id || booking?.bookingId || null,
    tourId: tourId || fallbackTour?.id || null,
    touristName: touristLabel(booking),
    tour: tourNode?.title || booking?.tourTitle || fallbackTour?.title || "Tour",
    date,
    people,
    status: String(booking?.status || "CONFIRMED").toUpperCase(),
    startTime,
    endTime,
    timeLabel: startTime && endTime ? startTime + " - " + endTime : startTime || endTime || "",
    province: tourNode?.province || fallbackTour?.province || "",
    city: tourNode?.city || fallbackTour?.city || "",
    category: tourNode?.category || fallbackTour?.category || "",
    language: tourNode?.language || fallbackTour?.language || "",
    durationHours: safeNumber(tourNode?.durationHours || fallbackTour?.durationHours, 0),
    pricePerPerson,
    totalPrice,
    description: tourNode?.description || fallbackTour?.description || ""
  };
};

const realUpcomingBookings = computed(() => {
  return (bookingRequests.value || [])
    .map(mapBooking)
    .filter((booking) => isConfirmedStatus(booking.status))
    .sort((a, b) => {
      const left = (a.date || "9999-12-31") + " " + (a.startTime || "23:59");
      const right = (b.date || "9999-12-31") + " " + (b.startTime || "23:59");
      return left.localeCompare(right);
    })
    .slice(0, 20);
});

const upcomingBookings = computed(() => realUpcomingBookings.value);

const stats = computed(() => {
  const now = new Date();
  const month = now.getMonth() + 1;
  const year = now.getFullYear();

  const thisMonth = upcomingBookings.value.filter((booking) => {
    if (!booking.date || booking.date === "TBD") return false;
    const [y, m] = String(booking.date).split("-").map(Number);
    return y === year && m === month;
  });

  const earnings = thisMonth.reduce((total, booking) => total + safeNumber(booking.totalPrice, 0), 0);

  const avgRating = Number(profileSnapshot.avgRating || 0);

  return {
    bookingsThisMonth: thisMonth.length,
    earnings,
    avgRating: avgRating > 0 ? avgRating.toFixed(2) : "0.00",
    reviews: Number(profileSnapshot.totalReviews || 0)
  };
});

const mapRecentReview = (review) => {
  const createdAt = review?.createdAt ? String(review.createdAt).slice(0, 10) : "Recent";
  return {
    id: review?.id,
    touristName: review?.touristName || "Tourist",
    tour: review?.tourId ? `Tour ID ${review.tourId}` : "Tour",
    rating: Number(review?.rating || 0).toFixed(1),
    date: createdAt,
    comment: review?.comment || ""
  };
};

const openBookingDetails = (booking) => {
  selectedBooking.value = booking || null;
  bookingDialogVisible.value = Boolean(booking);
};

const applyProfileToState = (profile) => {
  profileSnapshot.province = profile?.province || "";
  profileSnapshot.city = profile?.city || "";
  profileSnapshot.language = profile?.language || "";
  profileSnapshot.bio = profile?.bio || "";
  profileSnapshot.photoUrl = profile?.photoUrl || "";
  profileSnapshot.avgRating = profile?.avgRating || 0;
  profileSnapshot.totalReviews = profile?.totalReviews || 0;
  profileSnapshot.verificationStatus = profile?.verificationStatus || "PENDING";

  profileForm.province = profileSnapshot.province;
  profileForm.city = profileSnapshot.city;
  profileForm.language = profileSnapshot.language;
  profileForm.bio = profileSnapshot.bio;
  profileForm.photoUrl = profileSnapshot.photoUrl;
  onProvinceChange();
};

const loadDashboard = async () => {
  if (!(await ensureGuide())) {
    loading.value = false;
    return;
  }

  loading.value = true;
  try {
    const profile = await getMyGuideProfile();

    const [myTours, confirmedBookings, guideReviewsResponse] = await Promise.all([
      listMyTours(),
      listMyConfirmedBookingRequests(),
      reviewAdminApi.listGuideReviews(profile?.id)
    ]);

    applyProfileToState(profile || {});
    tours.value = myTours || [];
    bookingRequests.value = Array.isArray(confirmedBookings) ? confirmedBookings : [];
    recentReviews.value = Array.isArray(guideReviewsResponse?.data)
      ? guideReviewsResponse.data.map(mapRecentReview).slice(0, 5)
      : [];
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load dashboard");
  } finally {
    loading.value = false;
  }
};

const saveProfile = async () => {
  try {
    const payload = {
      province: profileForm.province,
      city: profileForm.city,
      language: profileForm.language,
      bio: profileForm.bio,
      photoUrl: profileForm.photoUrl
    };
    const updated = await updateGuideProfile(payload);
    applyProfileToState(updated || payload);
    profileDialogVisible.value = false;
    ElMessage.success("Profile updated");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to update profile");
  }
};

const onPhotoChange = async (event) => {
  const target = event.target;
  const file = target?.files?.[0];
  if (!file) return;

  uploadingPhoto.value = true;
  try {
    const response = await uploadGuidePhoto(file);
    profileForm.photoUrl = response.photoUrl;
    ElMessage.success("Profile photo uploaded");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to upload photo");
  } finally {
    uploadingPhoto.value = false;
    if (target) target.value = "";
  }
};

onMounted(loadDashboard);
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(165deg, #f8fbff 0%, #fff7ef 100%);
}

.dashboard-wrap {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  gap: 14px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  margin: 0;
}

.header p {
  margin: 4px 0 0;
  color: #475569;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
  gap: 12px;
}

.stat-card {
  border: 1px solid #dde5f3;
  border-radius: 12px;
  padding: 14px;
  display: grid;
  gap: 8px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.stat-card span {
  color: #334155;
  font-weight: 600;
}

.stat-card strong {
  font-size: 1.6rem;
}

.stat-bookings {
  background: #dbeafe;
  border-color: #93c5fd;
}

.stat-bookings strong {
  color: #1d4ed8;
}

.stat-earnings {
  background: #dcfce7;
  border-color: #86efac;
}

.stat-earnings strong {
  color: #15803d;
}

.stat-rating {
  background: #ffedd5;
  border-color: #fdba74;
}

.stat-rating strong {
  color: #c2410c;
}

.stat-reviews {
  background: #f3e8ff;
  border-color: #d8b4fe;
}

.stat-reviews strong {
  color: #7e22ce;
}

.card {
  background: #ffffff;
  border: 1px solid #dde5f3;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.card h2 {
  margin-top: 0;
}

.status-text {
  font-weight: 700;
}

.status-text.ok {
  color: #15803d;
}

.status-text.warn {
  color: #dc2626;
}

.booking-detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 14px;
}

.booking-detail-grid p {
  margin: 0;
}

.full-row {
  grid-column: 1 / -1;
}

.review-list {
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

.profile-card {
  display: grid;
  gap: 10px;
}

.profile-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-summary {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.photo-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.profile-photo,
.photo-placeholder {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  border: 1px solid #dbe5f3;
  object-fit: cover;
}

.photo-placeholder {
  display: grid;
  place-items: center;
  color: #64748b;
  font-size: 12px;
  background: #f8fafc;
}

.grid-two {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.hint {
  color: #64748b;
}

@media (max-width: 760px) {
  .grid-two {
    grid-template-columns: 1fr;
  }

  .booking-detail-grid {
    grid-template-columns: 1fr;
  }

  .profile-summary {
    flex-direction: column;
  }
}
</style>
