<!-- filepath: c:\PFE\l1b\frontend\src\components\Logo.vue -->
<script setup lang="ts">

defineProps<{
  fontSize: string;
  color?: string;
}>();
</script>

<template>
  <div :style="{ fontSize }" class="flex items-center gap-3">
    <img src="/logo.png" class="w-[3.2em]" />
  </div>
</template>