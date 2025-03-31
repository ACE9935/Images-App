<script setup lang="ts">

import { computed, ref, watch } from 'vue';
import type { ImageMetadata } from '../types';
import { fetchSimilarImages } from '../http-api';
import { useImageStore } from '../store/imageStore';
const props=defineProps<{ image: ImageMetadata }>();

const imgStore = useImageStore();
const selectedImage = computed(() => imgStore.selectedImage);

const similarImages = ref<ImageMetadata[]>([]);
const numOfSimilarImages = ref<string>("5");
const descrOfSimilarImages = ref<"histogram_2d"|"histogram_3d"|"histogram_of_visual_words">("histogram_2d");
const errorMessage = ref("");
const isLoading = ref(false);

const getSimilarImages = async () => {
  try {
    errorMessage.value = "";
    isLoading.value = true; // Set deleting state to true
    const similarImagesResponse = await fetchSimilarImages(String(props.image.id), numOfSimilarImages.value, descrOfSimilarImages.value);
    similarImages.value = similarImagesResponse;

  } catch (error) {
    console.error("Failed to images:", error);
    errorMessage.value = "Failed to load similar images.";
  }
  finally {
    isLoading.value = false; // Set deleting state to false when done
  }
};

watch(selectedImage, (newImageId) => {
  if (newImageId) {
    similarImages.value = [];
  }
}, { immediate: true });

</script>

<template>
  <div class="text-black flex flex-col gap-3 max-w-[22rem]">
    <h2 class="font-bold text-2xl">Similar Images:</h2>
    <p v-if="errorMessage" class="flex gap-3 font-bold items-center bg-red-300 border-2 border-red-500 p-3 rounded-md w-full justify-center"><i class="pi pi-exclamation-circle" style="font-size: 1rem"></i>{{ errorMessage }}</p>
    <div class="flex gap-3 items-center justify-between">
    <div class="flex gap-2 font-semibold flex-col">
    <label for="#descr-selector">Choose descriptor </label>
    <select id="descr-selector" v-model="descrOfSimilarImages" class="text-black w-min bg-white p-2 rounded-md shadow-md cursor-pointer hover:outline-green-400 outline-2 border-none">
        <option value="histogram_2d">HSV</option>
        <option value="histogram_3d">RGB</option>
        <option value="histogram_of_visual_words">Bag of words</option>
    </select>
</div>
<div class="flex gap-2 font-semibold flex-col">
    <label class="w-max">Number of images</label>
    <input type="number" v-model="numOfSimilarImages" class="text-black max-w-[10rem] bg-white p-2 rounded-md shadow-md cursor-pointer hover:outline-green-400 outline-2 border-none"/>
</div>
</div>
<button 
          @click="getSimilarImages" 
          :disabled="isLoading"
          class="rounded-md grow justify-center font-bold text-white p-3 flex gap-2 items-center transition-all"
          :class="{'bg-gray-400 cursor-wait': isLoading, 'bg-amber-500 hover:bg-amber-700 cursor-pointer': !isLoading}"
        >
          <i v-if="!isLoading" class="pi pi-cog" style="font-size: 1rem"></i>
          <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
          Get Similar Images
        </button>
<div v-if="similarImages.length > 0" class="grid grid-cols-2 overflow-auto max-h-[30rem] gap-3 py-3" :class="{'opacity-[0.6]': isLoading, 'opacity-[1]': !isLoading}">
  <div v-for="(image, index) in similarImages" @click="!isLoading ? imgStore.selectImage(String(image.id)) : null" :key="index" class="cursor-pointer">
    <img :src="'/images/visualize/' + image.id" class="w-full h-auto rounded-t-md"/>
    <div class="p-3 h-fit rounded-b-md bg-slate-100/45 shadow-md">
      <p class="font-bold hover:underline">{{ image.name }}</p>
      <p>Size: {{ image.size }}</p>
      <p class="truncate">Score: {{ image.score }}</p>
    </div>
  </div>
</div>
  </div>
</template>
