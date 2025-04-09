<template>
  <LoaderLogo v-if="isLoading&&uploadedImagess.length==0"/>
    <div v-else-if="uploadedImagess?.length !== 0">
      <ImagesContainer :max-columns="2" :images="uploadedImagess" />
    </div>
    <NoDataPlaceholder v-else />
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
  const uploadedImagess = ref<ImageMetaData[]>([]);
  
  const updateUploadedImages = async () => {
    isLoading.value = true;
  if (user.value && user.value.uploadedImages) {
    const images = await fetchImages();
    uploadedImagess.value = images
      .filter((image: ImageMetaData) =>
        user.value?.uploadedImages.includes(String(image.id))
      )
      .sort((a:any, b:any) => {
        const dateA = a.imgUploadDate instanceof Date ? a.imgUploadDate : new Date(a.imgUploadDate);
        const dateB = b.imgUploadDate instanceof Date ? b.imgUploadDate : new Date(b.imgUploadDate);

        return dateB.getTime() - dateA.getTime(); // Sort in descending order
      });
  }
  isLoading.value = false;
};

  
  onMounted(() => {
    updateUploadedImages();
  });
  
  // Watch for changes in user.uploadedImages and update uploadedImagess accordingly
  watch(
    () => user.value?.uploadedImages,
    async (newUploadedImages, oldUploadedImages) => {
      if (newUploadedImages !== oldUploadedImages) {
        await updateUploadedImages();
      }
    },
    { immediate: true }
  );
  </script>
  