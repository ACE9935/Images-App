<template>
  <LoaderLogo v-if="isLoading"/>
    <div v-else-if="viewedImages.length !== 0" class="overflow-auto w-full">
      <table class="table-auto w-full border-collapse text-black border-2 border-gray-200">
        <thead class="border-b-2 border-gray-200">
          <tr>
            <th class="py-2 px-4 text-left border-r-2 border-gray-200">Image</th>
            <th class="py-2 px-4 text-left border-r-2 border-gray-200">Title</th>
            <th class="py-2 px-4 text-left border-r-2 border-gray-200">Last Date Viewed</th>
            <th class="py-2 px-4"></th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(viewedImage, index) in paginatedImages"
            :key="index"
            class="border-b-2 border-gray-200"
          >
            <td class="py-2 px-4 border-r-2 border-gray-200 w-[6rem]">
              <img
                :src="viewedImage.image.imgUrl"
                alt="Image"
                class="w-full aspect-square object-cover rounded-md"
              />
            </td>
            <td class="py-2 px-4 border-r-2 border-gray-200 min-w-[10rem]">
              <h3 class="font-bold">{{ viewedImage.image.title }}</h3>
            </td>
            <td class="py-2 px-4 border-r-2 border-gray-200 w-0 whitespace-nowrap">
              <span>{{ viewedImage.date.toISOString().slice(0, 19).replace('T', ' ') }}</span>
            </td>
            <td class="py-2 px-4 border-r-2 border-gray-200 w-0 whitespace-nowrap">
              <BasicButton @click="router.push(`/image/${viewedImage.image.id}`)" class="w-fit">
                Show Image
              </BasicButton>
            </td>
          </tr>
        </tbody>
      </table>
  
      <!-- Pagination Controls -->
      <div class="mt-4 flex justify-center items-center gap-2" v-if="pageCount > 1">
        <Button
          @click="prevPage"
          :disabled="currentPage === 1"
          icon="pi pi-angle-left"
          size="normal"
          class="!bg-transparent !text-black !border-0"
        />
  
        <button
          v-for="page in pageCount"
          :key="page"
          @click="currentPage = page"
          :class="[
            'px-3 py-1 cursor-pointer rounded',
            currentPage === page ? 'bg-main font-semibold scale-105 border-1 border-black' : 'bg-gray-200 text-black'
          ]"
        >
          {{ page }}
        </button>
  
        <Button
          @click="nextPage"
          :disabled="currentPage === pageCount"
          icon="pi pi-angle-right"
          size="normal"
          class="!bg-transparent !text-black !border-0"
        />
    
      </div>
    </div>
    <NoDataPlaceholder v-else />
  </template>
  
  <script setup lang="ts">
  import { computed, inject, onMounted, ref, watch } from 'vue';
  import type { UserContext } from '../../types/UserContext';
  import type { ImageMetaData } from '../../types/ImageMetaData';
  import { fetchImages } from '../../http-api';
  import NoDataPlaceholder from '../utility/NoDataPlaceholder.vue';
  import { Timestamp } from 'firebase/firestore';
  import router from '../../router';
  import BasicButton from '../form/BasicButton.vue';
import { Button } from 'primevue';
import LoaderLogo from '../utility/LoaderLogo.vue';
  
  const userContext = inject<UserContext>('userContext');
  const user = computed(() => userContext?.user.value);
  const viewedImages = ref<{ image: ImageMetaData; date: Date }[]>([]);
  const isLoading = ref(false);
  
  // Pagination
  const itemsPerPage = 10;
  const currentPage = ref(1);
  
  const paginatedImages = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return viewedImages.value.slice(start, start + itemsPerPage);
  });
  
  const pageCount = computed(() => Math.ceil(viewedImages.value.length / itemsPerPage));
  const nextPage = () => {
    if (currentPage.value < pageCount.value) currentPage.value++;
  };
  const prevPage = () => {
    if (currentPage.value > 1) currentPage.value--;
  };
  
  const updateViewedImages = async () => {
    isLoading.value = true;
    if (user.value && user.value.viewedImages) {
      const images = await fetchImages();
      viewedImages.value = [];
  
      user.value.viewedImages.forEach((viewedImage) => {
        const image = images.find((img: ImageMetaData) => String(img.id) === viewedImage.imgId);
        if (image) {
          let date: Date;
          if (viewedImage.date instanceof Timestamp) {
            date = viewedImage.date.toDate();
          } else {
            date = new Date(viewedImage.date);
          }
  
          if (isNaN(date.getTime())) {
            console.warn('Invalid date:', viewedImage.date);
            return;
          }
  
          viewedImages.value.push({ image, date });
        }
      });
  
      // Sort by newest first
      viewedImages.value.sort((a, b) => b.date.getTime() - a.date.getTime());
  
      // Reset to first page after reload
      currentPage.value = 1;
      isLoading.value = false;
    }
  };
  
  onMounted(() => {
    updateViewedImages();
  });
  
  watch(
    () => user.value?.viewedImages,
    async (newViewedImages, oldViewedImages) => {
      if (newViewedImages !== oldViewedImages) {
        await updateViewedImages();
      }
    },
    { immediate: true }
  );
  </script>
  