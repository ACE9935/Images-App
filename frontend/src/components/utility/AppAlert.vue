<template>
    <p 
      class="flex gap-3 font-semibold bg-gray-300 border-2 border-gray-500 p-3 rounded-md text-black w-full max-w-[30rem]"
    >
      <i :class="['pi', icon]" class="mt-1" style="font-size: 1.6rem"></i>
      <slot></slot>
    </p>
  </template>
  
  <script setup lang="ts">
  defineProps<{
    icon: string;
  }>();
  </script>
  