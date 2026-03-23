import { defineStore } from "pinia";
import {
  cancelBooking,
  createBooking,
  createPaymentIntent,
  getBookingById,
  listBookings,
  markPaymentSucceeded
} from "../api/bookingPaymentApi";
import {
  BOOKING_STATUS,
  PAYMENT_INTENT_CREATION_STATUSES
} from "../constants/bookingStatus";

export const useBookingPaymentStore = defineStore("bookingPayment", {
  state: () => ({
    bookings: [],
    currentBooking: null,
    paymentIntent: null,
    loading: false,
    actionLoading: false
  }),
  getters: {
    sortedBookings(state) {
      return [...state.bookings].sort((a, b) => {
        const aKey = `${a.bookingDate || ""}T${a.startTime || "00:00:00"}`;
        const bKey = `${b.bookingDate || ""}T${b.startTime || "00:00:00"}`;
        return bKey.localeCompare(aKey);
      });
    }
  },
  actions: {
    upsertBooking(booking) {
      if (!booking?.id) {
        return;
      }

      const index = this.bookings.findIndex((item) => item.id === booking.id);
      if (index === -1) {
        this.bookings = [booking, ...this.bookings];
        return;
      }

      this.bookings[index] = { ...this.bookings[index], ...booking };
    },
    async fetchBookings() {
      this.loading = true;
      try {
        this.bookings = await listBookings();
      } finally {
        this.loading = false;
      }
    },
    async fetchBooking(bookingId) {
      this.loading = true;
      try {
        const booking = await getBookingById(bookingId);
        this.currentBooking = booking;
        this.upsertBooking(booking);
        return booking;
      } finally {
        this.loading = false;
      }
    },
    async submitBooking(payload) {
      this.actionLoading = true;
      try {
        const booking = await createBooking(payload);
        this.currentBooking = booking;
        this.upsertBooking(booking);
        return booking;
      } finally {
        this.actionLoading = false;
      }
    },
    async requestCancelBooking(bookingId) {
      this.actionLoading = true;
      try {
        const result = await cancelBooking(bookingId);
        this.upsertBooking({ id: bookingId, status: BOOKING_STATUS.CANCELLED_BY_TOURIST });
        if (this.currentBooking?.id === bookingId) {
          this.currentBooking = {
            ...this.currentBooking,
            status: BOOKING_STATUS.CANCELLED_BY_TOURIST
          };
        }
        return result;
      } finally {
        this.actionLoading = false;
      }
    },
    async preparePayment(booking) {
      const status = booking?.status;
      if (!status || !booking?.id) {
        throw new Error("Invalid booking for payment");
      }

      if (status === BOOKING_STATUS.PENDING_PAYMENT && booking.stripePaymentIntentId) {
        const existingIntent = {
          bookingId: booking.id,
          paymentIntentId: booking.stripePaymentIntentId,
          clientSecret: "existing-intent"
        };
        this.paymentIntent = existingIntent;
        return existingIntent;
      }

      if (!PAYMENT_INTENT_CREATION_STATUSES.has(status)) {
        throw new Error("Booking is not payable in current status");
      }

      const paymentIntent = await createPaymentIntent(booking.id);
      this.paymentIntent = paymentIntent;
      const updatedBooking = {
        ...booking,
        status: BOOKING_STATUS.PENDING_PAYMENT,
        stripePaymentIntentId: paymentIntent.paymentIntentId
      };
      this.currentBooking = updatedBooking;
      this.upsertBooking(updatedBooking);
      return paymentIntent;
    },
    async confirmPaymentSuccess(paymentIntentId) {
      this.actionLoading = true;
      try {
        return await markPaymentSucceeded(paymentIntentId);
      } finally {
        this.actionLoading = false;
      }
    },
    clearPaymentIntent() {
      this.paymentIntent = null;
    }
  }
});
