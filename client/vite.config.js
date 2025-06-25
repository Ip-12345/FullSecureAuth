import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: true,       // <--- add this line
    port: 5173,       // optional, just to be explicit
  },
  plugins: [react()],
});
