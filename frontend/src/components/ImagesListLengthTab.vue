<template>
  <div class="p-5 flex flex-col items-center gap-8 bg-white shadow-md rounded-md justify-center px-12">
    <h1 class="text-3xl font-bold text-center text-slate-900">Number of images: </h1>
    <p class="text-3xl text-green-600 font-bold">{{ imagesCount }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useImageStore } from '../store/imageStore';

const imageStore = useImageStore();
const imagesCount = computed(() => imageStore.imagesList.length);
</script>

<style scoped>
</style>