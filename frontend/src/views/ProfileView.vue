<script setup lang="ts">
import { watch } from 'vue';
import AppNavBar from '../components/AppNavBar.vue';
import type { UserContext } from '../types/UserContext';
import { inject, computed } from 'vue';
import router from '../router';
import ProfileTab from '../components/profile/ProfileTab.vue';
import StyledHeading from '../components/StyledHeading.vue';
import ProfileSections from '../components/profile/ProfileSections.vue';
import LoaderLogo from '../components/utility/LoaderLogo.vue';

const userContext = inject<UserContext>('userContext');

const user = computed(() => userContext?.user.value);
const isLoadingUser = computed(() => userContext?.loading.value);

watch(
  () => [user.value, isLoadingUser.value],
  ([newUser, loading]) => {
    if (!newUser && !loading) {
      console.error('Unauthenticated user, redirecting to login page');
      router.push('/login');
    }
  },
  { immediate: true }
);


</script>

<template>
 <div class="p-4 flex flex-col items-center">
  <AppNavBar class="w-full"/>
  <LoaderLogo v-if="isLoadingUser&&!user"/>
  <div v-else class="flex md:flex-row flex-col gap-6 my-4 w-full max-w-[70rem]">
   <ProfileTab :user="user" :isLoadingUser="isLoadingUser" />
   <div class="min-w-0 w-full flex flex-col gap-3">
    <StyledHeading class="hidden md:block">Profile</StyledHeading>
      <ProfileSections />
    </div>
  </div>
 </div>
</template>