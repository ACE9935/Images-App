<script setup lang="ts">
import ImagePreview from '../components/ImagePreview.vue'
import ImagesListLengthTab from '../components/ImagesListLengthTab.vue';
import ImagesSlider from '../components/ImagesSlider.vue';
import ImageUploader from '../components/ImageUploader.vue';

</script>

<template>
  <main class="max-w-[70rem] flex flex-col items-center gap-4 p-4">
  <ImagePreview msg="Images App" />
  <div class="flex sm:flex-row flex-col gap-3 sm:gap-6 w-full max-w-[50rem]">
    <ImageUploader/>
    <ImagesListLengthTab/>
  </div>
  <ImagesSlider/>
</main>
</template>

<style scoped>

</style>