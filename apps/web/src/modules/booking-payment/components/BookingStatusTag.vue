<template>
  <el-tag :type="tagType" effect="light" class="status-tag">
    {{ statusLabel }}
  </el-tag>
</template>

<script setup>
import { computed } from "vue";
import { BOOKING_STATUS } from "../constants/bookingStatus";

const props = defineProps({
  status: { type: String, required: true }
});

const statusLabel = computed(() =>
  props.status
    .toLowerCase()
    .split("_")
    .map((token) => token.charAt(0).toUpperCase() + token.slice(1))
    .join(" ")
);

const tagType = computed(() => {
  switch (props.status) {
    case BOOKING_STATUS.CONFIRMED:
    case BOOKING_STATUS.COMPLETED:
      return "success";
    case BOOKING_STATUS.PENDING_PAYMENT:
      return "warning";
    case BOOKING_STATUS.PAYMENT_FAILED:
    case BOOKING_STATUS.DISPUTED:
      return "danger";
    case BOOKING_STATUS.CREATED:
    case BOOKING_STATUS.CANCELLED_BY_GUIDE:
    case BOOKING_STATUS.CANCELLED_BY_TOURIST:
      return "info";
    default:
      return "";
  }
});
</script>

<style scoped>
.status-tag {
  font-weight: 600;
}
</style>
