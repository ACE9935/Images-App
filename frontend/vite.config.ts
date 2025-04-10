import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(
    {
      template: {
        compilerOptions: {
          isCustomElement: (tag) => tag.startsWith('swiper-') // Ignore Swiper Elements
        }
      }
    }
  ),tailwindcss(),],
  build: {
    outDir: 'target/dist',
    assetsDir: 'static'
  },
  server: {
    proxy: {
      '^/images': {
        target: 'https://images-app-production.up.railway.app',
        changeOrigin:true,
        secure: false,
      },
      },
      watch: {
        usePolling: true, // Forces Vite to check file changes actively
        interval: 100, // Adjust the polling interval (default is 100ms)
      },
      hmr: {
        overlay: false, // Prevents the error overlay from blocking UI
      }
    }
  }
)
