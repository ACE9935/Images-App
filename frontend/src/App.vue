<script setup lang="ts">
import HelloWorld from './components/HelloWorld.vue'
import ImagesSlider from './components/ImagesSlider.vue';
import ImageUploader from './components/ImageUploader.vue';
import ImageGallery from './components/ImageGallery.vue';
import ImageDetails from './components/ImageDetails.vue';

</script>

<template>
  <HelloWorld msg="Images App" />
  <ImageUploader/>
  <ImagesSlider/>
  <ImageGallery/>
  <ImageDetails/>
</template>

<style scoped>

</style>
