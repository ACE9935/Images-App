// main.ts
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import PrimeVue from 'primevue/config';
import Noir from './presets/Noir';
import DialogService from 'primevue/dialogservice'
import ToastService from 'primevue/toastservice';
import ConfirmationService from 'primevue/confirmationservice'
import Tooltip from 'primevue/tooltip';
import './style.css'

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);   

app.use(PrimeVue, {
    theme: {
        preset: Noir,
        options: {
            prefix: 'p',
            darkModeSelector: '.p-dark',
            cssLayer: false,
        }
    }
});
app.use(ConfirmationService);
app.use(ToastService);
app.use(DialogService);
app.directive('tooltip', Tooltip);
app.mount('#app');
