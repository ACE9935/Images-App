<script setup lang="ts">
import { ref, watch, computed, inject } from 'vue';
import type { ImageMetaData } from '../types/ImageMetaData';
import { useRoute } from 'vue-router';
import AppNavBar from '../components/AppNavBar.vue';
import { fetchImageMetaDataById } from '../http-api';
import router from '../router';
import { getUserById } from '../firebase/getUserById';
import { formatUploadDate } from '../utils/formatUploadDate';
import FavButton from '../components/utility/FavButton.vue';
import SimilarImages from '../components/SimilarImages.vue';
import type { User } from '../types/UserInterface';
import ImageMetaDataTable from '../components/ImageMetaDataTable.vue';
import type { UserContext } from '../types/UserContext';
import addImageToViewed from '../firebase/addImageToViewed';
import DownloadImageButton from '../components/utility/DownloadImageButton.vue';
import DeleteImageButton from '../components/utility/DeleteImageButton.vue';
import UpdateImageName from '../components/form/UpdateImageName.vue';
import LoaderLogo from '../components/utility/LoaderLogo.vue';
import AddImageToAlbumButton from '../components/AddImageToAlbumButton.vue';

const route = useRoute();
const imgData = ref<ImageMetaData | null>(null);
const imgAuthor = ref<User | null>(null);

const userContext = inject<UserContext>('userContext');
const user = computed(() => userContext?.user?.value);

const isAuthor = computed(() => 
  user.value?.uploadedImages?.map(Number).includes(Number(imgData.value?.id)) ?? false
);

// Function to fetch the image and author data
const fetchData = async (imgId: string) => {
  try {
    await fetchImageMetaDataById(imgId)
      .then(async (data: ImageMetaData) => {
        if (!data) {
          console.error('Image not found');
          router.push("/404");
        }
        imgData.value = data;
        imgAuthor.value = await getUserById(data.author);
        // Increment view count when the data is successfully fetched
      })
      .catch((error) => {
        console.error('Error fetching image metadata:', error);
        router.push("/404");
      });
  } catch (error) {
    console.error('Error:', error);
  }
};

// Watch for changes in the route parameters (id) and fetch data again when it changes
watch(() => route.params['id'], (newId) => {
  if (typeof newId === 'string') {
    window.scrollTo(0, 0)
    fetchData(newId);
  } else {
    console.error('Invalid image ID format');
    router.push("/404");
  }
}, { immediate: true });

watch(user, async (newUser) => {
  if (newUser && imgData.value) {
    await addImageToViewed(newUser.id, String(imgData.value.id));
  }
},
  { immediate: true }
);

</script>

<template>
  <div class="p-4">
    <AppNavBar />
    <LoaderLogo v-if="!imgData"/>
    <div v-else class="flex flex-col items-center gap-6">
   <div class="my-4 w-full max-w-[70rem] flex-col md:flex-row flex items-center border-2 border-gray-200 rounded-xl">
    <div class="flex justify-center grow p-4 md:p-2"><img :src="imgData.imgUrl" class="max-w-full rounded-xl"/></div>
    <div class="flex flex-col justify-between gap-4 min-h-[20rem] p-4 w-full max-w-[35rem]">
        <div class="flex flex-col gap-2">
        <div class="flex flex-col gap-1">

        <div class="flex justify-between items-center flex-wrap gap-4">
         <div v-if="imgAuthor" class="flex items-center gap-2 cursor-pointer" @click="router.push(`/users/${imgAuthor.id}`)">
           <img :src="imgAuthor.photoUrl" class="w-[1.8rem] aspect-square rounded-full object-cover" />
           <p :class="[
           'text-md font-medium', 
           isAuthor ? 'text-amber-600 bg-amber-200 px-2 rounded-md' : 'text-black'
          ]">{{ imgAuthor.userName }}</p>
         </div>
         <div v-else class="bg-gray-300 w-[12rem] h-5 rounded-md"></div>
         <div class="flex gap-2 items-center">
            <DeleteImageButton :handler="()=>router.push('/')" :img-id="String(imgData?.id)"/>
            <AddImageToAlbumButton :img-id="String(imgData?.id)"/>
            <FavButton :refetch-image="async ()=>await fetchData(String(imgData?.id))" :img-id="String(imgData.id)"/>
            <DownloadImageButton :img-id="String(imgData?.id)" :refetch-data="async ()=>await fetchData(String(imgData?.id))" :img-name="imgData.name"/>
         </div>
        </div>

            <h1 class="text-4xl text-black font-bold flex items-center gap-2">{{ imgData.title}}<UpdateImageName :refetch-image="()=>fetchData(String(imgData?.id))" :img-id="String(imgData?.id)"/></h1>
            <div class="flex items-center gap-3 mt-1">
        <div class="flex items-center gap-1"><i class="pi pi-heart-fill text-gray-500 mt-1" style="font-size: 1rem"></i><span class="text-black">{{ imgData.likeCount }}</span></div>
        <div class="flex items-center gap-1"><i class="pi pi-download text-gray-500 mt-1" style="font-size: 1rem"></i><span class="text-black">{{ imgData.downloadCount }}</span></div>
      </div>
        </div>
        <ImageMetaDataTable class="mt-4" :image="imgData"/>
    </div>
        <p class="text-sm text-gray-500 flex gap-2 items-center"><i class="pi pi-calendar text-gray-500" style="font-size: 1rem"></i>Uploaded at: {{ formatUploadDate(imgData.imgUploadDate) }}</p>
    </div>
   </div>
   <SimilarImages v-if="imgData" :image="imgData"/>
</div>
    
  </div>
</template>