<script setup lang="ts">
import { defineProps } from 'vue';
import { useRouter } from 'vue-router';

// Define props
defineProps<{
  form: "register" | "login" | "none";
}>();

// Use Vue Router for navigation
const router = useRouter();
const selectedClass='bg-form text-white'
const btnClass='text-black/90 px-4 py-2 grow cursor-pointer hover:bg-slate-200/50 transition-all'

// Navigation function
const navigateTo = (path: string) => {
  router.push(path);
};
</script>

<template>
  <div class="w-full flex h-[3rem] text-xl text-black/90 mt-4 border-[1.5px] border-slate-400 rounded-md overflow-hidden">
    <button
      @click="navigateTo('/login')"
      :class="[
        btnClass,
        form === 'login' ? selectedClass : 'border-r-[1.5px] border-slate-400'
      ]"
    >
      Login
    </button>
    <button
      @click="navigateTo('/signup')"
      :class="[
        btnClass,
        form === 'register' ? selectedClass : ''
      ]"
    >
      Signup
    </button>
  </div>
</template>

<style scoped>
/* Optional custom styles */
</style>
