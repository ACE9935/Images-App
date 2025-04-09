<script setup lang="ts">
import { Toast, ConfirmDialog } from "primevue";
import UserProvider from "./context/UserProvider.vue";
</script>

<template>
  <div class="min-h-screen">
    <Toast position="bottom-left" />
    <ConfirmDialog></ConfirmDialog>
    <UserProvider>
    <router-view />
  </UserProvider>
  </div>
</template>

<style scoped>
</style>

