<script setup lang="ts">
import { defineProps } from 'vue';

const props = defineProps<{
  successMessage: string | null;
}>();
</script>

<template>
  <div v-if="props.successMessage" class="flex gap-3 font-semibold text-green-600 items-center bg-green-200 border-2 border-green-500 p-3 rounded-md w-full justify-center">
    <i class="pi pi-check-circle" style="font-size: 1rem"></i>
    {{ props.successMessage }}
  </div>
</template>

<style scoped>
/* You can add custom styles if needed */
</style>
