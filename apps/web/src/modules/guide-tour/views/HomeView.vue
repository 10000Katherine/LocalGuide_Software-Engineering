<template>
  <main class="home-page">
    <section class="hero">
      <div class="hero-overlay">
        <p class="eyebrow">LocalGuide Connect</p>
        <h1>Discover trusted local guides in minutes</h1>
        <p class="sub">
          Search by province, city, language, date, and category to find tours tailored to your travel style.
        </p>

        <el-form class="search-box" :model="searchForm" @submit.prevent="submitSearch">
          <el-select v-model="searchForm.province" placeholder="Province" @change="onProvinceChange">
            <el-option v-for="province in PROVINCES" :key="province" :label="province" :value="province" />
          </el-select>
          <el-select v-model="searchForm.city" placeholder="City" clearable>
            <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
          </el-select>
          <el-select v-model="searchForm.language" placeholder="Language" clearable>
            <el-option v-for="language in LANGUAGES" :key="language" :label="language" :value="language" />
          </el-select>
          <el-select v-model="searchForm.category" placeholder="Category" clearable>
            <el-option v-for="category in CATEGORIES" :key="category" :label="category" :value="category" />
          </el-select>
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="Date"
            value-format="YYYY-MM-DD"
            clearable
          />
          <el-button type="primary" @click="submitSearch">Search</el-button>
        </el-form>
      </div>
    </section>

    <section class="featured">
      <div class="section-header">
        <h2>Featured Guides</h2>
        <router-link to="/search?type=guides">View all</router-link>
      </div>

      <el-skeleton :loading="loading" animated :rows="3">
        <template #default>
          <div class="guide-grid">
            <article v-for="guide in featuredGuides" :key="guide.id" class="guide-card">
              <div class="avatar">{{ initials(guide.firstName, guide.lastName) }}</div>
              <h3>{{ guide.firstName }} {{ guide.lastName }}</h3>
              <p>{{ guide.province || "Province not set" }} | {{ guide.city || "City not set" }} | {{ guide.language || "Language not set" }}</p>
              <p class="bio">{{ guide.bio || "Local expert ready to host unique tours." }}</p>
              <router-link class="profile-link" :to="'/guides/' + guide.id">View Profile</router-link>
            </article>
          </div>
          <el-empty v-if="featuredGuides.length === 0" description="No guides found yet" />
        </template>
      </el-skeleton>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { searchGuides } from "../api/guideTourApi";
import {
  CATEGORIES,
  DEFAULT_PROVINCE,
  LANGUAGES,
  PROVINCES,
  citiesForProvince
} from "../../../shared/constants/canadaOptions";

const router = useRouter();
const loading = ref(true);
const featuredGuides = ref([]);
const searchForm = reactive({
  province: DEFAULT_PROVINCE,
  city: "",
  language: "",
  date: "",
  category: ""
});

const cityOptions = computed(() => citiesForProvince(searchForm.province));

const onProvinceChange = () => {
  if (!cityOptions.value.includes(searchForm.city)) {
    searchForm.city = "";
  }
};

const submitSearch = () => {
  const query = { type: "guides" };
  if (searchForm.province) query.province = searchForm.province;
  if (searchForm.city) query.city = searchForm.city;
  if (searchForm.language) query.language = searchForm.language;
  if (searchForm.date) query.date = searchForm.date;
  if (searchForm.category) query.category = searchForm.category;
  router.push({ path: "/search", query });
};

const initials = (firstName, lastName) => {
  const a = (firstName || "?").charAt(0).toUpperCase();
  const b = (lastName || "?").charAt(0).toUpperCase();
  return a + b;
};

onMounted(async () => {
  try {
    const data = await searchGuides({ page: 0, size: 4 });
    featuredGuides.value = data.items || [];
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load featured guides");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: radial-gradient(circle at 15% 20%, #f4f9ff 0, #fff8ef 45%, #f9f5ef 100%);
}

.hero {
  position: relative;
  min-height: 460px;
  background:
    linear-gradient(rgba(15, 23, 42, 0.55), rgba(15, 23, 42, 0.55)),
    url("https://images.unsplash.com/photo-1467269204594-9661b134dd2b?auto=format&fit=crop&w=1600&q=80") center/cover;
  display: grid;
  align-items: center;
  padding: 48px 24px;
}

.hero-overlay {
  max-width: 1080px;
  margin: 0 auto;
  color: #fff;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.2em;
  font-size: 12px;
  margin-bottom: 10px;
}

h1 {
  font-size: clamp(2rem, 4vw, 3rem);
  margin: 0;
}

.sub {
  max-width: 700px;
  margin: 12px 0 24px;
  line-height: 1.55;
}

.search-box {
  display: grid;
  grid-template-columns: repeat(6, minmax(120px, 1fr));
  gap: 10px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 14px;
  padding: 10px;
}

.featured {
  max-width: 1100px;
  margin: 0 auto;
  padding: 28px 24px 44px;
}

.section-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 14px;
}

.section-header h2 {
  margin: 0;
  color: #0f172a;
}

.section-header a {
  color: #1d4ed8;
  text-decoration: none;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  gap: 14px;
}

.guide-card {
  background: #ffffff;
  border: 1px solid #dce4f0;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
}

.avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-weight: 700;
  background: #ecfeff;
  color: #0e7490;
}

.guide-card h3 {
  margin: 10px 0 4px;
  color: #111827;
}

.bio {
  color: #475569;
  min-height: 42px;
}

.profile-link {
  color: #0f766e;
  text-decoration: none;
  font-weight: 600;
}

@media (max-width: 1100px) {
  .search-box {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 620px) {
  .search-box {
    grid-template-columns: 1fr;
  }
}
</style>
