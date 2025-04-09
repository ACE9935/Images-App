<template>
    <div class="p-4">
        <AppNavBar />
 <LoaderLogo v-if="isLoading"/>
 <div v-else class="my-4 flex flex-col gap-4 items-center">
 <NoDataPlaceholder class="w-full max-w-[40rem]" v-if="users.length==0&&images.length==0"/>
    <div v-if="users.length>0" class="w-full max-w-[70rem] flex flex-col gap-2">
        <StyledHeading>Found Users: <span class="text-main">{{ users.length }}</span></StyledHeading>
        <div class="flex gap-2">
            <div @click="router.push(`/users/${user.id}`)" v-for="user in users" class="flex flex-col gap-2 items-center p-2 cursor-pointer">
                <img class="w-22 object-cover aspect-square rounded-full" :src="user.photoUrl"/>
                <div class="text-md w-16 text-center font-semibold text-black">{{ user.userName }}</div>
            </div>
        </div>
    </div>
    <div v-if="images.length>0" class="w-full max-w-[70rem] flex flex-col gap-2">
        <StyledHeading>Found Images: <span class="text-main">{{ images.length }}</span></StyledHeading>
        <ImagesContainer :images="images" />
    </div>


 </div>
</div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import type { UserPreview } from '../types/UserPreview';
import type { ImageMetaData } from '../types/ImageMetaData';
import { useRoute } from 'vue-router';
import { searchUsersByUsername } from '../firebase/searchUsersByUsername';
import AppNavBar from '../components/AppNavBar.vue';
import { searchImages } from '../http-api';
import LoaderLogo from '../components/utility/LoaderLogo.vue';
import ImagesContainer from '../components/ImagesContainer.vue';
import router from '../router';
import StyledHeading from '../components/StyledHeading.vue';
import NoDataPlaceholder from '../components/utility/NoDataPlaceholder.vue';

const route = useRoute();

const images = ref<ImageMetaData[]>([]);
const users = ref<UserPreview[]>([]);
const isLoading = ref(false);

const performSearch = async (term: string) => {
  isLoading.value = true;
  const [usersData, imagesData] = await Promise.all([
    searchUsersByUsername(term),
    searchImages(term),
  ]);
  users.value = usersData;
  images.value = imagesData;
  isLoading.value = false;
};

watchEffect(() => {
  const term = route.query.term as string || '';
  if (term) {
    performSearch(term);
  } else {
    users.value = [];
    images.value = [];
  }
});


</script>
