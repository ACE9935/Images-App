<template>
    <div class="fade-loop w-full h-screen flex items-center justify-center">
      <Logo font-size="8rem" />
    </div>
  </template>
  
  <script setup lang="ts">
  import Logo from '../Logo.vue';
  </script>
  
  <style scoped>
  .fade-loop {
    animation: fadeInOut 2s ease-in-out infinite;
  }
  
  @keyframes fadeInOut {
    0% {
      opacity: 0;
    }
    50% {
      opacity: 1;
    }
    100% {
      opacity: 0;
    }
  }
  </style>
  