import path from "node:path";
import react from "@vitejs/plugin-react";
import { defineConfig, loadEnv } from "vite";

const REQUIRED_ENV_VARS = ["VITE_GOOGLE_CLIENT_ID", "VITE_BACKEND_API_BASE_URL"];

function validateEnv(mode: string): void {
  const env = loadEnv(mode, process.cwd(), "");
  const missing = REQUIRED_ENV_VARS.filter((key) => !env[key]?.trim());
  if (missing.length > 0) {
    throw new Error(
      `Missing or empty required environment variables: ${missing.join(", ")}. ` +
        "Set them in frontend/.env (see frontend/.env.example).",
    );
  }
}

export default defineConfig(({ mode }) => {
  validateEnv(mode);

  return {
    plugins: [
      react({
        babel: {
          plugins: ["babel-plugin-react-compiler"],
        },
      }),
    ],
    preview: {
      allowedHosts: true, // for testing (change this in prod to actual domain)
    },
    server: {
      allowedHosts: true, //for testing tunneling with cloudflare tunnel
      proxy: {
        "/api": {
          target: "http://localhost:8080",
          changeOrigin: true,
        },
      },
    },
    base: "./",
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "src"),
      },
    },
    build: {
      chunkSizeWarningLimit: 600, // Increase from default 500 kB
    },
  };
});
