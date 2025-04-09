<template>
    <BasicActionButton class="py-3" :icon="isImgLiked ? 'pi-heart-fill' : 'pi-heart'" :is-loading="isLoading" :on-click="toggleLike">
  
    </BasicActionButton>
  </template>
  
<script setup lang="ts">
import { defineProps, computed, ref, inject } from 'vue';
import BasicActionButton from '../form/BasicActionButton.vue';
import type { UserContext } from '../../types/UserContext';
import router from '../../router';
import addImageToFav from '../../firebase/addImageToFav';
import removeImageFromFav from '../../firebase/removeImageFromFav';
import { useToast } from 'primevue';
import { decrementLikes, incrementLikes } from '../../http-api';

// Props
const props = defineProps<{ imgId: string, refetchImage:()=>void }>();

const userContext = inject<UserContext>('userContext');
const isLoading = ref(false);

const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;
const toast = useToast();

const isImgLiked = computed(() => user.value?.favImages?.includes(props.imgId) ?? false);

const toggleLike = async () => {
  if (!user.value) {
    router.push("/login");
    return;
  }

  try {
    isLoading.value = true;
    if (isImgLiked.value) {
      await removeImageFromFav(user.value.id, props.imgId);
      await decrementLikes(props.imgId)
      toast.add({ severity: 'success', summary: 'Success', detail: 'Image removed from favorites', life: 3000 });
    } else {
      await addImageToFav(user.value.id, props.imgId);
      await incrementLikes(props.imgId) 
      toast.add({ severity: 'success', summary: 'Success', detail: 'Image added to favorites', life: 3000 });
    }
    await refetchUser?.(); // Ensure we await the refresh
  } catch (error) {
    console.error("Action failed:", error);
    alert("Failed to update favorites.");
  } finally {
    props.refetchImage()
    isLoading.value = false;
  }
};


</script>