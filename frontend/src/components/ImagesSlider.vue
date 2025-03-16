<script setup lang="ts">
import { fetchImages } from "../http-api";
import { useImageStore } from "../store/imageStore";
import { ref, onMounted, watch } from "vue";
import { register, type SwiperContainer } from 'swiper/element/bundle';
import 'swiper/swiper-bundle.css'; 
import type { Image } from '../types';

register();

const imageStore = useImageStore();

const selectImage = (imageId: string) => {
  imageStore.selectImage(imageId);
};

const images = ref<Image[]>([]);
const errorMessage = ref("");

// Refs for Swiper container and navigation buttons
const swiperEl = ref<SwiperContainer|null>(null);
const swiperNextButton = ref<HTMLElement|null>(null);
const swiperPrevButton = ref<HTMLElement|null>(null);

// Fetch the list of images
const fetchImagesList = async () => {
  try {
    images.value = await fetchImages();
  } catch (error) {
    console.error(error);
    errorMessage.value = "Failed to load images.";
  }
};

watch(() => imageStore.reloadTrigger, () => {
  fetchImagesList();
});

onMounted(() => {
  fetchImagesList();

  swiperPrevButton.value?.addEventListener('click', () => {
    swiperEl.value?.swiper.slidePrev();
  });
  swiperNextButton.value?.addEventListener('click', () => {
    swiperEl.value?.swiper.slideNext();
  });

});
</script>

<template>
  <div class="flex flex-col items-center">
    <swiper-container ref="swiperEl" class="container max-w-[50rem] w-auto" :speed="300" free-mode="true"  slides-per-view="auto" space-between="10">
      <swiper-slide class="w-auto p-2" v-for="(image, index) in images" :key="index">
        <img
          :src="'/images/visualize/' + image.id"
          @click="selectImage(image.id)"
          alt="Image Preview"
          :class="{
            image: true,
            'selected-image': String(image.id) === imageStore.selectedImage
          }"
        />
      </swiper-slide>
    </swiper-container>
    <div class="navigation-buttons">
      <button ref="swiperPrevButton" class="prev-button"><i class="pi pi-arrow-left" style="font-size: 1rem"></i></button>
      <button ref="swiperNextButton" class="next-button"><i class="pi pi-arrow-right" style="font-size: 1rem"></i></button>
    </div>
  </div>
</template>

<style scoped>

.image {
  width: 5rem;
  aspect-ratio: 1;
  object-fit: cover;
  object-position: center;
  cursor: pointer;
  opacity: 0.5;
  transition: transform 0.2s;
}

.selected-image {
  opacity: 1 !important;
  transform: scale(1.2);
  border: 2px dashed var(--color-beige);
}

.image:hover {
  transform: scale(1.1);
}

.error-message {
  text-align: center;
  color: red;
  font-size: 1.2rem;
}

.navigation-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.navigation-buttons button {
  padding: 0.5rem 0.7rem;
  border-radius: 100rem;
  border: 2px solid var(--color-beige);
  color: var(--color-beige);
  border-radius: 100rem;
  cursor: pointer;
}
</style>
