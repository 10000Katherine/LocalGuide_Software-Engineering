<template>
  <main class="search-page">
    <aside class="filters">
      <h2>Filters</h2>
      <el-form label-position="top">
        <el-form-item label="Result Type">
          <el-segmented v-model="state.type" :options="typeOptions" @change="applyFilters" />
        </el-form-item>

        <el-form-item label="Province">
          <el-select v-model="state.province" placeholder="Province" @change="onProvinceChange">
            <el-option v-for="province in PROVINCES" :key="province" :label="province" :value="province" />
          </el-select>
        </el-form-item>

        <el-form-item label="City">
          <el-select v-model="state.city" clearable placeholder="City" @change="applyFilters">
            <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
          </el-select>
        </el-form-item>

        <el-form-item label="Date">
          <el-date-picker
            v-model="state.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="Any"
            clearable
            @change="applyFilters"
          />
        </el-form-item>

        <el-form-item label="Language">
          <el-select v-model="state.language" clearable placeholder="Language" @change="applyFilters">
            <el-option v-for="language in LANGUAGES" :key="language" :label="language" :value="language" />
          </el-select>
        </el-form-item>

        <el-form-item label="Category">
          <el-select v-model="state.category" clearable placeholder="Category" @change="applyFilters">
            <el-option v-for="category in CATEGORIES" :key="category" :label="category" :value="category" />
          </el-select>
        </el-form-item>

        <el-form-item label="Min Price (per person)">
          <el-input-number v-model="state.minPrice" :min="0" :step="10" @change="applyFilters" />
        </el-form-item>

        <el-form-item label="Max Price (per person)">
          <el-input-number v-model="state.maxPrice" :min="0" :step="10" @change="applyFilters" />
        </el-form-item>

        <el-button @click="resetFilters">Reset</el-button>
      </el-form>
    </aside>

    <section class="results">
      <header class="results-header">
        <h1>Search Results</h1>
        <p>{{ subtitle }}</p>
      </header>

      <el-skeleton :loading="loading" animated :rows="4">
        <template #default>
          <div v-if="state.type === 'guides'" class="card-grid">
            <article v-for="guide in guides" :key="guide.id" class="result-card">
              <h3>{{ guide.firstName }} {{ guide.lastName }}</h3>
              <p>{{ guide.province || "Province TBD" }} | {{ guide.city || "City TBD" }} | {{ guide.language || "Language TBD" }}</p>
              <p class="desc">{{ guide.bio || "Local guide profile" }}</p>
              <div class="card-actions">
                <router-link :to="'/guides/' + guide.id">View Profile</router-link>
                <el-button
                  v-if="canFavorite"
                  class="favorite-btn"
                  size="small"
                  plain
                  :type="favoritesStore.isFavorite(guide.id) ? 'danger' : 'info'"
                  @click="toggleFavorite(guide.id)">
                  {{ favoritesStore.isFavorite(guide.id) ? "Unfavorite" : "Add to Favorites" }}
                </el-button>
              </div>
            </article>
            <el-empty v-if="guides.length === 0" description="No guides found" />
          </div>

          <div v-else class="card-grid">
            <article v-for="tour in tours" :key="tour.id" class="result-card">
              <h3>{{ tour.title }}</h3>
              <p>{{ tour.province }} | {{ tour.city }} | {{ tour.category }} | {{ tour.language }}</p>
              <p class="desc">{{ tour.description }}</p>
              <p class="price">${{ Number(tour.price).toFixed(2) }} per person</p>
              <router-link :to="'/tours/' + tour.id">View Tour</router-link>
            </article>
            <el-empty v-if="tours.length === 0" description="No tours found" />
          </div>

          <div class="pager">
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="state.page"
              :page-size="12"
              :total="totalItems"
              @current-change="onPageChange"
            />
          </div>
        </template>
      </el-skeleton>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../../../shared/stores/auth";
import { useFavoritesStore } from "../../../shared/stores/favorites";
import { searchGuides, searchTours } from "../api/guideTourApi";
import {
  CATEGORIES,
  DEFAULT_PROVINCE,
  LANGUAGES,
  PROVINCES,
  citiesForProvince
} from "../../../shared/constants/canadaOptions";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const favoritesStore = useFavoritesStore();

const typeOptions = [
  { label: "Guides", value: "guides" },
  { label: "Tours", value: "tours" }
];

const state = reactive({
  type: "guides",
  province: DEFAULT_PROVINCE,
  city: "",
  date: "",
  language: "",
  category: "",
  minPrice: null,
  maxPrice: null,
  page: 1
});

const loading = ref(false);
const guides = ref([]);
const tours = ref([]);
const totalItems = ref(0);

const cityOptions = computed(() => citiesForProvince(state.province));
const canFavorite = computed(() => authStore.user?.role === "TOURIST");

const subtitle = computed(() => {
  const total = totalItems.value;
  return state.type === "guides" ? total + " guides matched" : total + " tours matched";
});

const parseNumberQuery = (value) => {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
};

const syncFromQuery = () => {
  state.type = route.query.type === "tours" ? "tours" : "guides";
  state.province = route.query.province || DEFAULT_PROVINCE;
  state.city = route.query.city || "";
  state.date = route.query.date || "";
  state.language = route.query.language || "";
  state.category = route.query.category || "";
  const minPrice = route.query.minPrice ? parseNumberQuery(route.query.minPrice) : null;
  const maxPrice = route.query.maxPrice ? parseNumberQuery(route.query.maxPrice) : null;
  const page = route.query.page ? parseNumberQuery(route.query.page) : null;

  state.minPrice = minPrice === null ? null : Math.max(0, minPrice);
  state.maxPrice = maxPrice === null ? null : Math.max(0, maxPrice);
  state.page = page === null ? 1 : Math.max(1, page);

  if (!cityOptions.value.includes(state.city)) {
    state.city = "";
  }
};

const buildQuery = () => {
  const query = { type: state.type, page: String(state.page) };
  if (state.province) query.province = state.province;
  if (state.city) query.city = state.city;
  if (state.date) query.date = state.date;
  if (state.language) query.language = state.language;
  if (state.category) query.category = state.category;
  if (state.minPrice !== null) query.minPrice = String(state.minPrice);
  if (state.maxPrice !== null) query.maxPrice = String(state.maxPrice);
  return query;
};

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      province: state.province || undefined,
      city: state.city || undefined,
      date: state.date || undefined,
      language: state.language || undefined,
      category: state.category || undefined,
      minPrice: state.minPrice === null ? undefined : state.minPrice,
      maxPrice: state.maxPrice === null ? undefined : state.maxPrice,
      page: state.page - 1,
      size: 12
    };

    if (state.type === "guides") {
      const data = await searchGuides(params);
      guides.value = data.items || [];
      tours.value = [];
      totalItems.value = data.totalItems || 0;
    } else {
      const data = await searchTours(params);
      tours.value = data.items || [];
      guides.value = [];
      totalItems.value = data.totalItems || 0;
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to load search results");
  } finally {
    loading.value = false;
  }
};

const onProvinceChange = () => {
  if (!cityOptions.value.includes(state.city)) {
    state.city = "";
  }
  applyFilters();
};

const applyFilters = () => {
  state.page = 1;
  router.replace({ path: "/search", query: buildQuery() });
};

const onPageChange = (page) => {
  state.page = page;
  router.push({ path: "/search", query: buildQuery() });
};

const resetFilters = () => {
  state.type = "guides";
  state.province = DEFAULT_PROVINCE;
  state.city = "";
  state.date = "";
  state.language = "";
  state.category = "";
  state.minPrice = null;
  state.maxPrice = null;
  state.page = 1;
  router.replace({ path: "/search", query: buildQuery() });
};

const toggleFavorite = async (guideId) => {
  try {
    const becameFavorite = await favoritesStore.toggleFavorite(guideId);
    ElMessage.success(becameFavorite ? "Guide added to favorites" : "Guide removed from favorites");
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "Failed to update favorite");
  }
};

watch(
  () => route.query,
  () => {
    syncFromQuery();
    loadData();
  },
  { deep: true }
);

onMounted(async () => {
  syncFromQuery();
  if (authStore.accessToken && !authStore.user) {
    try {
      await authStore.loadProfile();
    } catch (error) {
      void error;
    }
  }

  if (canFavorite.value) {
    try {
      await favoritesStore.loadFavorites();
    } catch (error) {
      ElMessage.warning(error?.response?.data?.message || "Favorites are temporarily unavailable");
    }
  }
  loadData();
});
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(180deg, #fffaf0 0%, #f3f7ff 100%);
}

.filters {
  background: #ffffff;
  border: 1px solid #dee6f3;
  border-radius: 14px;
  padding: 16px;
  height: fit-content;
  box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06);
}

.filters h2 {
  margin: 0 0 12px;
}

.results {
  background: #ffffff;
  border: 1px solid #dee6f3;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06);
}

.results-header h1 {
  margin: 0;
}

.results-header p {
  margin-top: 4px;
  color: #475569;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
  margin-top: 10px;
}

.result-card {
  border: 1px solid #e3e8f2;
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.result-card h3 {
  margin: 0 0 8px;
}

.desc {
  min-height: 50px;
  color: #475569;
}

.price {
  font-weight: 700;
  color: #0f766e;
}

.favorite-btn {
  margin: 0;
}

.card-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: 8px;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

@media (max-width: 980px) {
  .search-page {
    grid-template-columns: 1fr;
  }
}
</style>
