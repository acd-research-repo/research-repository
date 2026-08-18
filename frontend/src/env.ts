function isBlank(value: unknown): boolean {
  return typeof value !== "string" || value.trim() === "";
}

const rawGoogleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const rawBackendApiBaseUrl = import.meta.env.VITE_BACKEND_API_BASE_URL;

const missing: string[] = [];

if (isBlank(rawGoogleClientId)) {
  missing.push("VITE_GOOGLE_CLIENT_ID");
}
if (isBlank(rawBackendApiBaseUrl)) {
  missing.push("VITE_BACKEND_API_BASE_URL");
}

if (missing.length > 0) {
  throw new Error(`Missing or empty required environment variables: ${missing.join(", ")}`);
}

export const env = {
  googleClientId: rawGoogleClientId,
  backendApiBaseUrl: rawBackendApiBaseUrl,
};
