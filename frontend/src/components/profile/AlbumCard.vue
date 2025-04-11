<template>
    <Dialog v-model:visible="visibleDialog" modal :header="`${props.album.name}`" class="w-min">
      <div class="text-black flex items-center justify-center gap-3 w-[30rem] max-w-screen">
        <Button
          @click="getPreviousImage"
          :disabled="selectedImageIndex === 0"
          icon="pi pi-angle-left"
          size="large"
          class="!bg-transparent w-full !text-black !border-0"
        />
        <div class="flex flex-col items-center gap-3 p-4">
          <!-- Use the computed selectedImage -->
          <img :src="selectedImage?.imgUrl" class="w-full h-full" />
          <div class="flex gap-2">
            <BasicButton @click="visibleDialog = false; router.push(`/image/${selectedImage?.id}`)" class="w-fit">
              <i class="pi pi-image py-2" style="font-size: 1rem"></i>
            </BasicButton>
            <FavButton :refetch-image="() => null" :img-id="String(selectedImage?.id)" />
            <AddImageToAlbumButton :force-close="forceClose" :img-id="String(selectedImage?.id)" />
            <DownloadImageButton :img-id="String(selectedImage?.id)" :refetch-data="() => null" :img-name="selectedImage?.name" />
          </div>
        </div>
        <Button
          @click="getNextImage"
          :disabled="selectedImageIndex === albumImages.length - 1"
          icon="pi pi-angle-right"
          size="large"
          class="!bg-transparent w-full !text-black !border-0"
        />
      </div>
    </Dialog>
  
    <div class="p-4 bg-white rounded-xl border-2 border-gray-200 flex flex-col gap-3">
      <div>
        <h2 class="text-xl text-black font-semibold flex gap-2">
          {{ props.album.name }}
          <UpdateAlbumName :album-id="props.album.id" :album-name="props.album.name" />
        </h2>
        <div class="text-gray-700 text-sm">
          Created at: {{ parseFirebaseDateValue(props.album.date, true) }}
        </div>
      </div>
      <div
        @click="albumImages.length !== 0 ? visibleDialog = true : null"
        class="grid gap-1 h-[20rem] cursor-pointer bg-gray-200 relative rounded-lg"
        :class="{
          'grid-cols-2 grid-rows-2': albumImages.length >= 4 || albumImages.length === 3, // fallback layout for 3
          'grid-cols-2 grid-rows-1': albumImages.length === 2,
          'grid-cols-1 grid-rows-1': albumImages.length === 1,
        }"
      >
        <img
          v-for="(image, index) in albumImages.slice(0, 4)"
          :key="index"
          :src="image.imgUrl"
          alt="Album image"
          class="object-cover h-full w-full rounded-md"
        />
        <i v-if="albumImages.length === 0" class="pi pi-bookmark-fill text-gray-500 absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2" style="font-size: 6rem"></i>
      </div>
      <div class="flex justify-between items-center w-full">
        <div class="text-black text-lg">
          <span class="font-bold">{{ albumImages.length }}</span> Images
        </div>
        <DeleteAlbumButton :album-id="props.album.id" />
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed, watch } from 'vue';
  import type { Album } from '../../types/Album';
  import type { ImageMetaData } from '../../types/ImageMetaData';
  import parseFirebaseDateValue from '../../utils/parseFirebaseDate';
  import DeleteAlbumButton from '../utility/DeleteAlbumButton.vue';
  import UpdateAlbumName from '../form/UpdateAlbumName.vue';
  import { Dialog } from 'primevue';
  import router from '../../router';
  import Button from 'primevue/button';
  import BasicButton from '../form/BasicButton.vue';
  import FavButton from '../utility/FavButton.vue';
  import AddImageToAlbumButton from '../AddImageToAlbumButton.vue';
  import DownloadImageButton from '../utility/DownloadImageButton.vue';
  
  const visibleDialog = ref(false);
  const selectedImageIndex = ref<number>(0);
  const forceClose = ref(0);
  
  const props = defineProps<{
    album: Album;
    images: ImageMetaData[];
  }>();
  
  // Compute images that belong to the album.
  // Assumes album.images is an array of image IDs stored as strings.
  const albumImages = computed((): ImageMetaData[] => {
    return props.images.filter((img: ImageMetaData) =>
      props.album.images.includes(String(img.id))
    );
  });
  
  // Compute the current selected image based on index.
  const selectedImage = computed(() => {
    return albumImages.value[selectedImageIndex.value];
  });
  
  // Functions for navigation.
  const getNextImage = () => {
    if (selectedImageIndex.value < albumImages.value.length - 1) {
      selectedImageIndex.value++;
    }
  };
  
  const getPreviousImage = () => {
    if (selectedImageIndex.value > 0) {
      selectedImageIndex.value--;
    }
  };
  
  // Watch for changes in albumImages and reset index if needed.
  watch(albumImages, (newImages, oldImages) => {
    if (newImages.length === 0) {
      visibleDialog.value = false;
    } if (newImages.length != oldImages.length) {
      selectedImageIndex.value = 0;
      forceClose.value = forceClose.value + 1;
    }
   
  });
  </script>
  
  