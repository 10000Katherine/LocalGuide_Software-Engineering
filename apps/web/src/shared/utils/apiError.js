export function extractApiErrorMessage(error, fallbackMessage) {
  const data = error?.response?.data;
  if (!data) {
    return fallbackMessage;
  }

  const fieldErrors = data.errors;
  if (fieldErrors && typeof fieldErrors === "object") {
    const entries = Object.entries(fieldErrors)
      .filter(([, value]) => Boolean(value))
      .map(([field, value]) => `${field}: ${value}`);
    if (entries.length > 0) {
      return entries.join(" | ");
    }
  }

  if (typeof data.message === "string" && data.message.trim() !== "") {
    return data.message;
  }

  return fallbackMessage;
}
