<template>
    <BasicActionButton
    v-tooltip.bottom="{ value: 'Delete album', autoHide: false, showDelay: 1000 }"
    v-if="user" class="py-3" :is-loading="isLoading" :icon="'pi-trash'" :on-click="confirm1">
  
    </BasicActionButton>
    </template>
    
<script setup lang="ts">
  import { defineProps, computed, ref, inject } from 'vue';
  import type { UserContext } from '../../types/UserContext';
  import { useToast } from 'primevue';
  import BasicActionButton from '../form/BasicActionButton.vue';
  import { useConfirm } from 'primevue';
  import deleteAlbumFromUser from '../../firebase/deleteAlbum';
  
  // Props
  const props = defineProps<{ albumId: number,hanlder?:()=>void}>();
  const confirm = useConfirm();
  
  const userContext = inject<UserContext>('userContext');
  const isLoading = ref(false);
  
  const user = computed(() => userContext?.user.value);
  const refetchUser = userContext?.refreshUser;
  const toast = useToast();
  
  const handleDelete = async () => {
  
    if (!user.value) {
      return;
    }
  
    try {
      isLoading.value = true;
        await deleteAlbumFromUser(user.value.id, props.albumId);
        await refetchUser?.();
        props.hanlder?.()
  
    } catch (error) {
      console.error("Action failed:", error);
      alert("Failed to detete album.");
    } finally {
      isLoading.value = false;
    }
  };
  
  const confirm1 = () => {
      confirm.require({
          message: 'Are you sure you want to proceed?',
          header: 'Delete Album',
          icon: 'pi pi-exclamation-triangle',
          rejectProps: {
              label: 'Cancel',
              severity: 'secondary',
              outlined: true
          },
          acceptProps: {
              label: 'Delete'
          },
          accept: async () => {
              isLoading.value=true
              await handleDelete()
              .then(()=>isLoading.value=false)
              .then(()=> toast.add({ severity: 'success', summary: 'Success', detail: 'Album deleted successfully !', life: 3000 }))
          }
      });
  };
  
  </script>