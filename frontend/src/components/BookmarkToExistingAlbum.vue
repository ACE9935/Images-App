<template>
  <BasicActionButton
    :is-loading="isLoading"
    :icon="isInAlbum ? 'pi-bookmark' : 'pi-plus'"
    :on-click="handleToggle"
    :variant="isInAlbum ? 'neutral' : 'gray'"
    class="bg-gray-100 hover:bg-gray-200 text-black"
  >
    {{ album.name }}
  </BasicActionButton>
</template>

<script setup lang="ts">
import BasicActionButton from './form/BasicActionButton.vue';
import { ref, inject, computed } from 'vue';
import type { UserContext } from '../types/UserContext';
import { useToast } from 'primevue';
import addImageToAlbum from '../firebase/addImageToAlbum';
import removeImageFromAlbum from '../firebase/removeImageFromAlbum';
import type { Album } from '../types/Album';

const props = defineProps<{
  imgId: string;
  album: Album;
}>();

const userContext = inject<UserContext>('userContext');
const isLoading = ref(false);
const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;
const toast = useToast();

const isInAlbum = computed(() => props.album.images.includes(props.imgId));

const handleToggle = async () => {
  if (!user.value || isLoading.value) return;

  try {
    isLoading.value = true;

    if (isInAlbum.value) {
      await removeImageFromAlbum(user.value.id, props.album.id, props.imgId);
      toast.add({ severity: 'info', summary: 'Removed', detail: 'Image removed from album.', life: 3000 });
    } else {
      await addImageToAlbum(user.value.id, props.album.id, props.imgId);
      toast.add({ severity: 'success', summary: 'Added', detail: 'Image added to album.', life: 3000 });
    }

    await refetchUser?.()
  } catch (e) {
    console.error(e);
    toast.add({ severity: 'error', summary: 'Error', detail: 'Something went wrong.', life: 3000 });
  } finally {
    isLoading.value = false;
  }
};
</script>
