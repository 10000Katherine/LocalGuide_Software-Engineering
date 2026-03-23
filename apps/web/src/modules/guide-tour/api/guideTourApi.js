import apiClient from "../../../shared/api/client";

export async function searchGuides(params = {}) {
  const { data } = await apiClient.get("/search/guides", { params });
  return data;
}

export async function searchTours(params = {}) {
  const { data } = await apiClient.get("/search/tours", { params });
  return data;
}

export async function getGuideProfile(id) {
  const { data } = await apiClient.get("/guides/" + id);
  return data;
}

export async function listGuideAvailability(guideId) {
  const { data } = await apiClient.get("/guides/" + guideId + "/availability");
  return data;
}

export async function getMyGuideProfile() {
  const { data } = await apiClient.get("/guides/me");
  return data;
}

export async function updateGuideProfile(payload) {
  const { data } = await apiClient.put("/guides/me", payload);
  return data;
}

export async function uploadGuidePhoto(file) {
  const formData = new FormData();
  formData.append("file", file);
  const { data } = await apiClient.post("/guides/me/photo", formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
  return data;
}

export async function getTourDetail(id) {
  const { data } = await apiClient.get("/tours/" + id);
  return data;
}

export async function listMyTours() {
  const { data } = await apiClient.get("/guides/me/tours");
  return data;
}

export async function createTour(payload) {
  const { data } = await apiClient.post("/guides/me/tours", payload);
  return data;
}

export async function updateTour(id, payload) {
  const { data } = await apiClient.put("/tours/" + id, payload);
  return data;
}

export async function deleteTour(id) {
  await apiClient.delete("/tours/" + id);
}

export async function listMyAvailability() {
  const { data } = await apiClient.get("/guides/me/availability");
  return data;
}

export async function createAvailability(payload) {
  const { data } = await apiClient.post("/guides/me/availability", payload);
  return data;
}

export async function updateAvailability(id, payload) {
  const { data } = await apiClient.put("/guides/me/availability/" + id, payload);
  return data;
}

export async function deleteAvailability(id) {
  await apiClient.delete("/guides/me/availability/" + id);
}

function canFallbackEndpoint(error) {
  const status = error?.response?.status;
  return status === 404 || status === 405 || status === 501;
}

export async function listMyConfirmedBookingRequests() {
  const candidates = [
    { url: "/bookings/guides/me/requests", params: { status: "CONFIRMED" } },
    { url: "/bookings/guides/me", params: { status: "CONFIRMED" } },
    { url: "/bookings", params: { status: "CONFIRMED" } }
  ];

  for (const candidate of candidates) {
    try {
      const { data } = await apiClient.get(candidate.url, { params: candidate.params });
      if (Array.isArray(data)) return data;
      if (Array.isArray(data?.items)) return data.items;
      return [];
    } catch (error) {
      if (canFallbackEndpoint(error)) {
        continue;
      }
      throw error;
    }
  }

  return [];
}

export async function listGuideBookingRequests(params = {}) {
  const { data } = await apiClient.get("/bookings/guides/me/requests", { params });
  if (Array.isArray(data)) {
    return data;
  }
  if (Array.isArray(data?.items)) {
    return data.items;
  }
  return [];
}

export async function acceptGuideBookingRequest(bookingId) {
  const { data } = await apiClient.put(`/bookings/guides/me/requests/${bookingId}/accept`);
  return data;
}

export async function declineGuideBookingRequest(bookingId) {
  const { data } = await apiClient.put(`/bookings/guides/me/requests/${bookingId}/decline`);
  return data;
}

export async function createBookingRequest(payload) {
  const candidates = ["/bookings", "/bookings/requests"];

  for (const url of candidates) {
    try {
      const { data } = await apiClient.post(url, payload);
      return data;
    } catch (error) {
      if (canFallbackEndpoint(error)) {
        continue;
      }
      throw error;
    }
  }

  const notImplemented = new Error("Booking API not implemented");
  notImplemented.status = 501;
  throw notImplemented;
}
