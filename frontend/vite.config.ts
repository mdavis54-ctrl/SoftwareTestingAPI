import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
// `mvn spring-boot:run` builds this app and Spring Boot serves it on port 8080, so you
// only need `npm run dev` (port 5173) if you want to change the UI and see it reload live.
export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        watch: {
            usePolling: true, // Forces Vite to notice saves even if OS signals fail
        },
        proxy: {
            // This redirects any frontend call to /api over to your Java server
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
            '/lab-config.js': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            }
        }
    }
})
