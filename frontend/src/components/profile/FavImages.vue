<template>
  <LoaderLogo v-if="isLoading&&favImages.length==0"/>
    <div v-else-if="favImages?.length!=0">
      <ImagesContainer :max-columns="2" :images="favImages"/>
    </div>
    <NoDataPlaceholder v-else/>
  </template>
  
  <script setup lang="ts">
  import { computed, inject, onMounted, ref, watch } from 'vue';
  import type { UserContext } from '../../types/UserContext';
  import ImagesContainer from '../ImagesContainer.vue';
  import type { ImageMetaData } from '../../types/ImageMetaData';
  import { fetchImages } from '../../http-api';
  import NoDataPlaceholder from '../utility/NoDataPlaceholder.vue';
import LoaderLogo from '../utility/LoaderLogo.vue';
  
  const userContext = inject<UserContext>('userContext');
  const isLoading = ref(false);
  
  const user = computed(() => userContext?.user.value);
  const favImages = ref<ImageMetaData[]>([]);
  
  // Function to update the favImages based on user's favImages
  const updateFavImages = async () => {
    if (user.value && user.value.favImages) {
      isLoading.value = true;
      const images = await fetchImages();
      favImages.value = images.filter((image: ImageMetaData) =>
        user.value?.favImages.includes(String(image.id)) // Ensure image.id is compared as a string
      );
    }
    isLoading.value = false;
  };
  
  // Initial fetch when component is mounted
  onMounted(() => {
    updateFavImages();
  });
  
  // Watch for changes in user.favImages and update favImages accordingly
  watch(() => user.value?.favImages, async (newFavImages, oldFavImages) => {
    if (newFavImages !== oldFavImages) {
      await updateFavImages();
    }
  }, { immediate: true });
  
  </script>
  
  