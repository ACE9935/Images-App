<template>
  <div class="w-full flex flex-col items-center">
    <div class="flex gap-4 w-full">
      <BasicButton
        v-for="(section, index) in sections"
        :key="index"
        @click="emit('update:selectedSection', section.id)"
        v-tooltip.bottom="{ value: section.name, autoHide: false, showDelay: 1000 }"
        :variant="selectedSection === section.id ? 'neutral' : 'gray'"
        class="w-full font-bold py-3"
      >
        <i :class="section.icon"></i>
      </BasicButton>
    </div>
</div>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue';
  import type { Section, SectionType } from '../../types/ProfileSections';
  import BasicButton from '../form/BasicButton.vue';
  
  const sections: SectionType[] = [
    { id: "uploaded-images-section", icon: "pi pi-images", name: "Your Images" },
    { id: "upload-section", icon: "pi pi-upload", name: "Upload Images" },
    { id: "favorites-images-section", icon: "pi pi-heart", name: "Favorites Images" },
    { id: "albums-section", icon: "pi pi-bookmark", name: "Albums" },
    { id: "history-section", icon: "pi pi-history", name: "History of Viewed Images" }
  ];
  
  const props = defineProps<{
    selectedSection: Section
  }>();
  
  const emit = defineEmits<{
    (e: 'update:selectedSection', value: Section): void;
  }>();
  
  const selectedSection = computed(() => props.selectedSection);
  </script>
  