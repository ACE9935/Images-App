<script setup lang="ts">
import BasicButton from './form/BasicButton.vue';
import Logo from './Logo.vue';
import router from '../router';
import { computed, inject } from 'vue';
import type { UserContext } from '../types/UserContext';
import SearchBar from './SearchBar.vue';

const userContext = inject<UserContext>('userContext');
const user = computed(() => userContext?.user.value);
const loadingUser = computed(() => userContext?.loading.value);

</script>

<template>
  <div class="py-1 md:py-2 px-2 md:px-4 bg-main rounded-xl flex justify-between items-center">
    <Logo font-size="1.5rem" />
    <div class="flex gap-2 sm:gap-4 items-center">
    <SearchBar/>
    <i v-if="loadingUser" class="pi pi-spinner pi-spin" style="font-size: 2rem"></i>
    <div v-else>
      <div
        v-if="user"
        class="w-14 h-14 rounded-full bg-cover bg-center border-2 border-white cursor-pointer"
        @click="router.push('/profile')"
        :style="{ backgroundImage: `url(${user.photoUrl})` }"
      ></div>
      <BasicButton v-else @click="router.push('/login')" variant="contrast">Signin</BasicButton>
    </div>
  </div>
  </div>
</template>

<style scoped>
</style>
