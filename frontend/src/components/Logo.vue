<!-- filepath: c:\PFE\l1b\frontend\src\components\Logo.vue -->
<script setup lang="ts">
import router from '../router';

defineProps<{
  fontSize: string;
  color?: string;
}>();
</script>

<template>
  <div @click="() => router.push('/')" :style="{ fontSize }" class="flex items-center gap-3 cursor-pointer">
    <img src="/imgs/logo.png" class="w-[3.2em]" />
  </div>
</template>