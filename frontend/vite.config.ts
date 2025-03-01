import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    outDir: 'target/dist',
    assetsDir: 'static'
  },
  server: {
    proxy: {
      '^/images': {
        target: 'http://localhost:8001' // Spring boot backend address
      },
    }
  }
})
