<script setup lang="ts">
import { defineProps } from 'vue';

const props = defineProps<{
  url: string;        
  disabled?: boolean;
  [key: string]: any;
}>();
</script>

<template>
  <button
    :disabled="props.disabled"
    v-bind="$attrs" 
    class="p-3 w-full bg-white cursor-pointer justify-center shadow-md flex items-center gap-3 border-md font-semibold text-lg text-slate-600 border-slate-400 hover:outline-main hover:outline-2 border-2 rounded-md"
  >
    <!-- Image inside the button -->
    <img alt="provider logo" :src="props.url" width="30" height="30" />
    <!-- Content inside the button -->
    <p><slot></slot></p>
  </button>
</template>

<style scoped>
/* Add custom styles if needed */
</style>
