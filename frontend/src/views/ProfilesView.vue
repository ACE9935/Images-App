<script setup lang="ts">

import AppNavBar from '../components/AppNavBar.vue';
import LoaderLogo from '../components/utility/LoaderLogo.vue';
import { ref, watch } from 'vue';
import type { User } from '../types/UserInterface';
import { useRoute } from 'vue-router';
import { getUserById } from '../firebase/getUserById';
import router from '../router';
import parseFirebaseDateValue from '../utils/parseFirebaseDate';
import ImagesContainer from '../components/ImagesContainer.vue';
import type { ImageMetaData } from '../types/ImageMetaData';
import { fetchImages } from '../http-api';
import NoDataPlaceholder from '../components/utility/NoDataPlaceholder.vue';

const user=ref<User | null>(null);
const userImages=ref<ImageMetaData[]>([])
const imagesLoading=ref(false)
const route = useRoute();

const fetchUser = async () => {

  const userId = route.params['id'] as string;
  try {
    const response:User|null = await getUserById(userId);
    if(!response) {
      console.error('User not found');
      router.push("/404");
      return;
    }
    
    user.value = response;
  } catch (error) {
    console.error('Error fetching user data:', error);
  }
};

const updateUploadedImages = async () => {
    imagesLoading.value = true;
  if (user.value && user.value.uploadedImages) {
    const images = await fetchImages();
    userImages.value = images
      .filter((image: ImageMetaData) =>
        user.value?.uploadedImages.includes(String(image.id))
      )
      .sort((a:any, b:any) => {
        const dateA = a.imgUploadDate instanceof Date ? a.imgUploadDate : new Date(a.imgUploadDate);
        const dateB = b.imgUploadDate instanceof Date ? b.imgUploadDate : new Date(b.imgUploadDate);

        return dateB.getTime() - dateA.getTime(); // Sort in descending order
      });
  }
  imagesLoading.value=false;
};

watch(() => route.params['id'], (newId) => {
  if (typeof newId === 'string') {
    window.scrollTo(0, 0)
    fetchUser().then(() => {
      updateUploadedImages();
    });
  } else {
    console.error('Invalid user ID format');
    router.push("/404");
  }
}, { immediate: true });


</script>

<template>
 <div class="p-4 flex flex-col items-center">
  <AppNavBar class="w-full"/>
  <LoaderLogo v-if="!user"/>
    <div v-else class="flex flex-col gap-6 my-8 w-full max-w-[70rem]">
     <div class="flex flex-row gap-6 items-center">
      <div class="p-1 border-4 border-black/70 rounded-full"><img :src="user.photoUrl" class="w-[10rem] aspect-square rounded-full object-cover"/></div>
      <div class="flex flex-col gap-3">
        <div>
        <div class="text-3xl font-bold text-black/80">{{ user.userName }}</div>
        <div class="text-md text-gray-700">{{ user.email }}</div>
    </div>
        <div class="flex md:flex-row flex-col">
        <div class="text-gray-600 text-lg md:text-xl md:border-r-2 md:pr-4">Posts: <span class="text font-bold text-black">{{ user.uploadedImages.length }}</span></div>
        <div class="text-gray-600 text-lg md:text-xl md:pl-4">Member Since: <span class="text font-bold text-black">{{ parseFirebaseDateValue(user.joinDate) }}</span></div>
    </div>
      </div>
    </div>
    <LoaderLogo v-if="imagesLoading"/>
    <NoDataPlaceholder class="max-w-[50rem]" v-else-if="userImages.length==0"/>
    <ImagesContainer v-else :images="userImages"/>
    </div>
  </div>
</template>