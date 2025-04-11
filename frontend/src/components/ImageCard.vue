<script setup lang="ts">
import type { ImageMetaData } from '../types/ImageMetaData';
import { ref, computed, inject, watch } from 'vue';
import type { User } from '../types/UserInterface';
import { getUserById } from '../firebase/getUserById';
import { formatUploadDate } from '../utils/formatUploadDate';
import { useRouter } from 'vue-router';
import FavButton from './utility/FavButton.vue';
import type { UserContext } from '../types/UserContext';
import AddImageToAlbumButton from './AddImageToAlbumButton.vue';

const router = useRouter();
const userContext = inject<UserContext>('userContext');
const user = computed(() => userContext?.user.value);

const props = defineProps({
  imgData: {
    type: Object as () => ImageMetaData,
    required: true,
  },
});

const imgAuthor = ref<User | null>(null);
const isLoaded = ref(false);
const isLoading = ref(false);

const isAuthor = computed(() => 
  user.value?.uploadedImages?.map(Number).includes(Number(props.imgData.id)) ?? false
);

watch(
  () => props.imgData,
  async (newImage) => {
    if (newImage) {
      try {
       isLoading.value = true;
       imgAuthor.value = await getUserById(props.imgData.author);
      } catch (error) {
       console.error('Error fetching Author:', error);
       imgAuthor.value = null;
      } finally {
       isLoading.value = false;
      }
    }
  },
  { immediate: true }
);

const navigateToImagePage=(e:Event)=> {
  e.stopPropagation()
  router.push(`/image/${props.imgData.id}`);
}
</script>

<template>
  <div class="flex flex-col gap-2 relative border-2 border-gray-200 rounded-xl p-3">
    <div @click="navigateToImagePage" class="image-container relative">
      <div
    class="bg-gray-200 rounded-lg overflow-hidden transition-all duration-300"
    :style="{ minHeight: isLoaded ? 'auto' : '200px' }"
  >
    <img
      :src="imgData.imgUrl"
      alt=""
      @load="isLoaded = true"
      class="w-full rounded-lg cursor-pointer"
    />
  </div>
      <div class="image-info-overlay rounded-b-lg cursor-pointer bg-linear-to-b from-transparent to-black/60 to-30% absolute bottom-0 w-full p-4 bg-opacity-50 text-white flex justify-between items-end opacity-0 transition-opacity duration-300">
        <div class="flex flex-col gap-1">
          <p class="font-medium text-lg">{{ imgData.title }}</p>
          <p class="text-xs text-slate-300">Uploaded at: {{ formatUploadDate(imgData.imgUploadDate) }}</p>
        </div>
        <div class="flex gap-2">
        <div @click.stop><FavButton class="!py-3" :refetch-image="()=>null" :img-id="String(imgData.id)" /></div>
        <div @click.stop><AddImageToAlbumButton :img-id="String(imgData.id)" /></div>
      </div>
      </div>
    </div>
    <div class="flex justify-between items-center gap-2">
      <div v-if="imgAuthor&&!isLoading" class="flex items-center gap-2 cursor-pointer" @click="router.push(`/users/${imgAuthor.id}`)">
        <img :src="imgAuthor.photoUrl" class="w-[1.6rem] rounded-full object-cover h-[1.6rem]" />
        <div class="flex gap-1 items-center">
          <p :class="[
           'text-md font-medium', 
           isAuthor ? 'text-amber-600 bg-amber-200 px-2 rounded-md' : 'text-black'
          ]">
           {{ imgAuthor.userName }}
          </p>
      </div>
      </div>
      <div v-else-if="!imgAuthor&&isLoading" class="bg-gray-300 w-full h-5 rounded-md"></div>
      <div class="flex items-center gap-3">
        <div class="flex items-center gap-1"><i class="pi pi-heart-fill text-gray-500 mt-1" style="font-size: 1rem"></i><span class="text-black">{{ imgData.likeCount }}</span></div>
        <div class="flex items-center gap-1"><i class="pi pi-download text-gray-500 mt-1" style="font-size: 1rem"></i><span class="text-black">{{ imgData.downloadCount }}</span></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.image-container {
  position: relative;
}

.image-container:hover .image-info-overlay {
  opacity: 1;
}

.image-info-overlay {
  height: 6rem; /* Takes roughly half the height of the image */
}
</style>
