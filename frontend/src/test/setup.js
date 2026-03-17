import '@testing-library/jest-dom';

// React act() requires development build; Vitest may run with production
process.env.NODE_ENV = 'development';
