<script setup>
import { computed } from "vue";
import { Timestamp } from "firebase/firestore";
import UpdateProfileName from "../form/UpdateProfileName.vue";
import UpdateProfileImageButton from "../form/UpdateProfileImageButton.vue";
import LogoutButton from "./LogoutButton.vue";
import DeleteAccountButton from "./DeleteAccountButton.vue";
import parseFirebaseDateValue from "../../utils/parseFirebaseDate";

defineProps({
  user: Object,
  isLoadingUser: Boolean,
});

</script>

<template>
  
    <div v-if="user" class="w-full md:max-w-[20rem] flex flex-col border-2 h-fit border-gray-200 rounded-xl p-4 items-center">
      <div class="w-full max-w-[20rem] flex flex-col items-center gap-4">
      <div class="border-4 border-main p-1 rounded-full w-fit">
        <img :src="user.photoUrl" class="w-[12rem] aspect-square object-cover rounded-full" />
      </div>
      <div class="flex flex-col items-center">
        <div class="text-xl text-black font-bold">{{ user.userName }}</div>
        <div class="text-md text-gray-700">{{ user.email }}</div>
      </div>
      <div class="flex flex-col gap-2 w-full">
        <UpdateProfileImageButton />
        <UpdateProfileName :user-name="user.userName"/>
      </div>
      <div class="w-full border-b-2 border-gray-200 pb-2">
        <div class="text-gray-700 flex justify-between">
          <div>Uploaded images:</div>
          <div class="font-bold">{{ user.uploadedImages.length }}</div>
        </div>
        <div class="text-gray-700 flex justify-between">
          <div>Albums:</div>
          <div class="font-bold">{{ user.albums.length }}</div>
        </div>
        <div class="text-gray-700 flex justify-between">
          <div>Liked images:</div>
          <div class="font-bold">{{ user.favImages.length }}</div>
        </div>
        <div class="text-gray-700 flex justify-between">
          <div>Member since:</div>
          <div class="font-bold">{{ parseFirebaseDateValue(user.joinDate) }}</div>
        </div>
      </div>
      <div class="flex flex-col gap-2 w-full items-start !text-sm">
        <LogoutButton class="w-full" />
        <DeleteAccountButton variant="failure" class="w-full" />
      </div>
    </div>
  </div>

</template>
