<script setup lang="ts">
import { ref, provide, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { auth } from '../firebase/firebase';
import { getUserById } from '../firebase/getUserById';
import { isValidUser } from '../firebase/isValidUser';
import type { User } from '../types/UserInterface';

const user = ref<User | null>(null);
const loading = ref(true);
const route = useRoute();

const fetchUser = async (watcher?:boolean) => {
  try {
    if(!watcher) loading.value = true;
    const userId = auth.currentUser?.uid;
    if (userId) {
      const userData = await getUserById(userId);
      user.value = userData;
    } else {
      user.value = null;
    }
  } catch (error) {
    console.error('Error getting user document:', error);
    user.value = null; 
  }
  finally {
    if(!watcher) loading.value = false;
  }
  
};

// Add debugging to confirm mounting behavior
onMounted(async () => {

  const unsubscribe = auth.onAuthStateChanged(async (authUser) => {

    if (authUser && isValidUser(authUser)) {
      await fetchUser()
    } else {
      user.value = null;
      loading.value = false;
    }
  });

  return () => unsubscribe();
});

watch(() => route.path, async () => {
  fetchUser(true)
});

provide('userContext', {
  user,
  loading,
  refreshUser: fetchUser,
});
</script>

<template>
  <slot></slot>
</template>