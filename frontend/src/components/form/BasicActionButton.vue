<template>
    <BasicButton :disabled="isLoading" @click="onClick" v-bind="$attrs">
      <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
      <i v-else :class="`pi ${icon}`" style="font-size: 1rem"></i>
      <slot></slot>
    </BasicButton>
  </template>
  
  <script setup lang="ts">
  import { defineProps } from 'vue';
  import BasicButton from './BasicButton.vue';
  
  // Props
  defineProps<{
    isLoading: boolean; // Loading state
    onClick: (e:Event) => void; // Submit handler
    icon?: string; // Optional icon
  }>();
  </script>