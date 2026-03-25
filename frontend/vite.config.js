import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    environmentMatchGlobs: [['**/readme-resume*.test.js', 'node']],
    setupFiles: './src/test/setup.js',
  },
});
