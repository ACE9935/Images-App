<script setup lang="ts">
import { ref } from 'vue';
import ImagesClassesNavigator from '../components/home/ImagesClassesNavigator.vue';
import { useRoute } from 'vue-router';
import { onMounted } from 'vue';
import { useToast } from "primevue/usetoast";
import AppNavBar from '../components/AppNavBar.vue';
import CIFAR10_CLASSES from '../utils/ImagesClasses';
import type { CIFAR10Classes } from '../types/cifar10';
import ImagesTab from '../components/home/ImagesTab.vue';

const route = useRoute();

// Define a ref to store the selected class
const selectedClass = ref<CIFAR10Classes>(CIFAR10_CLASSES[0]);

onMounted(() => {
  const isVerifiedUser = route.query['verified-user'];

  if (isVerifiedUser) {
    const toast = useToast();
    toast.add({ severity: 'success', summary: 'Success', detail: 'Your email has been verified !', life: 3000 });
  }
});

const updateSelectedClass = (newClass: CIFAR10Classes) => {
  selectedClass.value = newClass;
};
</script>

<template>
  <div class="p-4">
    <AppNavBar />
  </div>
  <main class="flex flex-col items-center gap-4 p-4">
    <div class="text-black flex flex-col items-center gap-6 py-10 w-full max-w-[45rem] text-center">
      <h1 class="text-5xl delius-regular font-bold leading-16">Discover captivating images in one place</h1>
      <p class="text-gray-800 text-xl">Explore images of 10 different classes and have them saved!</p>
    </div>
    
    <ImagesClassesNavigator :selectedClass="selectedClass" @update-selected-class="updateSelectedClass" />>
    <ImagesTab :imgClass="selectedClass" />
    
  </main>
</template>
