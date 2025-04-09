<template>
    <LoaderLogo v-if="images.length==0"/>
    <div v-else-if="user?.albums?.length!=0&&images.length!=0" class="grid grid-cols-1 md:grid-cols-2 gap-3">
      <AlbumCard v-for="(album, index) in user?.albums" :key="index" :album="album" :images="images"/>
    </div>
    <NoDataPlaceholder v-else/>
  </template>
  
<script setup lang="ts">
  import { computed, inject, onMounted, ref } from 'vue';
  import type { UserContext } from '../../types/UserContext';
  import type { ImageMetaData } from '../../types/ImageMetaData';
  import NoDataPlaceholder from '../utility/NoDataPlaceholder.vue';
  import { fetchImages } from '../../http-api';
  import AlbumCard from './AlbumCard.vue';
import LoaderLogo from '../utility/LoaderLogo.vue';
  
  const userContext = inject<UserContext>('userContext');
  
  const user = computed(() => userContext?.user.value);
  const images = ref<ImageMetaData[]>([]);  
  
  onMounted(async () => {
    if (user.value?.albums) {
      images.value = await fetchImages();
    }
  });
  
</script>
  
  