<script setup lang="ts">
import ImagePreview from '../components/ImagePreview.vue'
import ImagesListLengthTab from '../components/ImagesListLengthTab.vue';
import ImagesSlider from '../components/ImagesSlider.vue';
import ImageUploader from '../components/ImageUploader.vue';
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'
import { useToast } from "primevue/usetoast";

const route = useRoute()

onMounted(() => {
  const isVerifiedUser = route.query['verified-user'];

  if(isVerifiedUser) {
    const toast = useToast();
    toast.add({ severity: 'success', summary: 'Success', detail: 'Your email has been verified !', life: 3000 });
  }
  
})

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