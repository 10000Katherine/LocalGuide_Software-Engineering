import apiClient from "../../../shared/api/client";

export const listBookings = async () => {
  const { data } = await apiClient.get("/bookings");
  return data;
};

export const getBookingById = async (bookingId) => {
  const { data } = await apiClient.get(`/bookings/${bookingId}`);
  return data;
};

export const createBooking = async (payload) => {
  const { data } = await apiClient.post("/bookings", payload);
  return data;
};

export const cancelBooking = async (bookingId) => {
  const { data } = await apiClient.put(`/bookings/${bookingId}/cancel`);
  return data;
};

export const createPaymentIntent = async (bookingId) => {
  const { data } = await apiClient.post("/payments/create-intent", { bookingId });
  return data;
};

export const markPaymentSucceeded = async (paymentIntentId) => {
  const { data } = await apiClient.post("/payments/webhook", {
    paymentIntentId,
    status: "SUCCEEDED"
  });
  return data;
};
