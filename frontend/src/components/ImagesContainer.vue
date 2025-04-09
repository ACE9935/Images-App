<template>
  <div class="flex gap-4" :class="columnClass">
    <div v-for="(column, colIndex) in columns" :key="colIndex" class="flex-1 flex flex-col gap-4">
      <div v-for="(image, index) in column" :key="index" class="w-full">
        <ImageCard :imgData="image" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import type { ImageMetaData } from '../types/ImageMetaData';
import ImageCard from './ImageCard.vue';

const props = defineProps<{
  images: ImageMetaData[];
  maxColumns?: 1 | 2 | 3;  // New prop to control maximum columns
}>();

const screenWidth = ref(window.innerWidth);

const updateScreenWidth = () => {
  screenWidth.value = window.innerWidth;
};

// Listen for window resize events
onMounted(() => {
  window.addEventListener('resize', updateScreenWidth);
});

onUnmounted(() => {
  window.removeEventListener('resize', updateScreenWidth);
});

const columns = computed(() => {
  let numColumns = 3; // Default to 3 columns

  if (props.maxColumns) {
    // If maxColumns prop is provided, use it
    numColumns = props.maxColumns;
  } else {
    // If no maxColumns, adjust based on screen width
    if (screenWidth.value >= 1024) {
      numColumns = 3;
    } else if (screenWidth.value >= 768) {
      numColumns = 2;
    } else {
      numColumns = 1;
    }
  }

  // Create empty columns
  const columnData: ImageMetaData[][] = Array.from({ length: numColumns }, () => []);

  // Distribute images using round-robin
  props.images.forEach((image, index) => {
    columnData[index % numColumns].push(image);
  });

  return columnData;
});

const columnClass = computed(() => {
  if (screenWidth.value >= 1024) return 'flex';
  if (screenWidth.value >= 768) return 'flex flex-wrap';
  return 'flex flex-col';
});
</script>
