<template>
  <BasicActionButton v-if="isAuthor" class="py-3" :is-loading="isLoading" :icon="'pi-trash'" :on-click="confirm1">

  </BasicActionButton>
  </template>
  
<script setup lang="ts">
 import { defineProps, computed, ref, inject } from 'vue';
import type { UserContext } from '../../types/UserContext';
import { useToast } from 'primevue';
import { deleteImageById} from '../../http-api';
import removeImageFromUploaded from '../../firebase/removeImageFromUploaded';
import BasicActionButton from '../form/BasicActionButton.vue';
import { useConfirm } from 'primevue';

// Props
const props = defineProps<{ imgId: string, handler:()=>void}>();
const confirm = useConfirm();

const userContext = inject<UserContext>('userContext');
const isLoading = ref(false);

const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;
const toast = useToast();

const isAuthor = computed(() => 
  user.value?.uploadedImages?.map(Number).includes(Number(props.imgId)) ?? false
);

const handleDelete = async () => {

  if (!user.value) {
    return;
  }

  try {
    isLoading.value = true;

    if (isAuthor.value) {
      await removeImageFromUploaded(user.value.id, props.imgId);
      await deleteImageById(props.imgId);
      await refetchUser?.();
    }

  } catch (error) {
    console.error("Action failed:", error);
    alert("Failed to delete image.");
  } finally {
    isLoading.value = false;
  }
};

const confirm1 = () => {
    confirm.require({
        message: 'Are you sure you want to proceed?',
        header: 'Delete Image',
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
            .then(()=> toast.add({ severity: 'success', summary: 'Success', detail: 'Image deleted successfully !', life: 3000 }))
            .then(()=>props.handler())
        }
    });
};

</script>