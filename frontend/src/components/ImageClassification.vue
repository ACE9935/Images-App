<script setup lang="ts">

import { computed, ref, watch } from 'vue';
import type { ImageMetadata } from '../types';
import AppAlert from './utility/AppAlert.vue';
import { fetchImageClassification } from '../http-api';
import { useImageStore } from '../store/imageStore';
const props=defineProps<{ image: ImageMetadata }>();

const imgStore = useImageStore();
const selectedImage = computed(() => imgStore.selectedImage);

const imageClass = ref<String[]>([]);
const errorMessage = ref("");
const isLoading = ref(false);

const getImageClass = async () => {
  try {
    errorMessage.value = "";
    isLoading.value = true; // Set deleting state to true
    const similarImagesResponse : String[] = await fetchImageClassification(String(props.image.id));
    imageClass.value = similarImagesResponse;

  } catch (error) {
    console.error("Failed to classify image:", error);
    errorMessage.value = "Failed to get image class.";
  }
  finally {
    isLoading.value = false; // Set deleting state to false when done
  }
};

watch(selectedImage, (newImageId) => {
  if (newImageId) {
    imageClass.value = [];
  }
}, { immediate: true });

</script>

<template>
  <div class="text-black flex flex-col gap-3 max-w-[22rem]">
    <h2 class="font-bold text-2xl">Image Classification:</h2>
    <p v-if="errorMessage" class="flex gap-3 font-bold items-center bg-red-300 border-2 border-red-500 p-3 rounded-md w-full justify-center"><i class="pi pi-exclamation-circle" style="font-size: 1rem"></i>{{ errorMessage }}</p>
    <AppAlert icon="pi-info-circle">
     <p>Classification is based on the <a class="underline text-blue-600" target="_blank" href="https://www.cs.toronto.edu/~kriz/cifar.html">CIFAR-10</a> dataset classes.</p>
    </AppAlert>
<button 
          @click="getImageClass" 
          :disabled="isLoading"
          class="rounded-md grow justify-center font-bold text-white p-3 flex gap-2 items-center transition-all"
          :class="{'bg-gray-400 cursor-wait': isLoading, 'bg-green-500 hover:bg-green-700 cursor-pointer': !isLoading}"
        >
          <i v-if="!isLoading" class="pi pi-cog" style="font-size: 1rem"></i>
          <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
          Get Image Class
        </button>
<div v-if="imageClass.length > 0" class="h-[10rem] border-2 border-dashed rounded-md grid place-items-center aspect-square bg-slate-100 shadow-lg" :class="{'opacity-[0.6]': isLoading, 'opacity-[1]': !isLoading}">
    <div class="text-4xl font-bold">{{ imageClass[0] }}</div>
    <div>Confidence: <span class="font-bold text-green-500">{{ imageClass[1] }}%</span></div>
</div>
  </div>
</template>