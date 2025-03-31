<script setup lang="ts">
import { defineProps } from 'vue';

const props = defineProps<{
  errorMessage: string | null;
}>();
</script>

<template>
  <div v-if="props.errorMessage" class="flex gap-3 font-semibold items-center bg-red-200 border-2 border-red-500 p-3 rounded-md w-full justify-center">
    <i class="pi pi-exclamation-circle" style="font-size: 1rem"></i>
    {{ props.errorMessage }}
  </div>
</template>

<style scoped>
/* You can add custom styles if needed */
</style>
