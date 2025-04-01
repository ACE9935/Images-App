<script setup lang="ts">
import { RouterView } from "vue-router";
import { Toast } from "primevue";
</script>

<template>
  <div class="min-h-screen">
    <Toast position="bottom-left" />
    <router-view />
  </div>
</template>

<style scoped>
</style>

