<script setup lang="ts">
import { watch } from 'vue';
import type { CIFAR10Classes } from '../../types/cifar10';
import { ref } from 'vue';
import { fetchImagesOfClass } from '../../http-api';
import type { ImageMetaData } from '../../types/ImageMetaData';
import ImagesContainer from '../ImagesContainer.vue';
import LoaderLogo from '../utility/LoaderLogo.vue';
import NoDataPlaceholder from '../utility/NoDataPlaceholder.vue';

const props = defineProps<{
  imgClass: CIFAR10Classes;
}>();

const images = ref<ImageMetaData[]>([]);
const isLoading = ref(false);

watch(
  () => props.imgClass,
  async (newClass) => {
    images.value = [];
    console.log('Loading...');
    isLoading.value = true;
    images.value = await fetchImagesOfClass(newClass)
    console.log('Images Loaded');
    isLoading.value = false;
  },
  { immediate: true }
);
</script>

<template>
  <LoaderLogo v-if="isLoading&&images.length==0"/>
  <NoDataPlaceholder class="max-w-[50rem]" v-else-if="images.length==0"/>
  <div v-else :class="{'opacity-50 pointer-events-none': isLoading}" class="transition-opacity duration-300 w-full max-w-[70rem]">
    <ImagesContainer :images="images"/>
  </div>
</template>

<style scoped>

.transition-opacity {
  transition: opacity 0.5s ease;
}
</style>
