<template>
    <BasicButton class="w-full" variant="gray" :disabled="isLoading" @click="triggerFileInput">
        <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
        <i v-else class="pi pi-camera" style="font-size: 1rem"></i>
        Edit Image
    </BasicButton>
    <input type="file" ref="fileInput" accept="image/*" class="hidden" @change="handleUpload" />
</template>

<script setup lang="ts">
import { computed, ref, inject } from 'vue';
import BasicButton from '../form/BasicButton.vue';
import type { UserContext } from '../../types/UserContext';
import { useToast } from 'primevue';
import { updateUserField } from '../../firebase/updateUserField';
import uploadImageToStore from '../../firebase/uploadImageToStore';
import { configurations } from '../../app-configuration';
import { deleteImageFromStore } from '../../firebase/deleteImageFromStore';

const userContext = inject<UserContext>('userContext');
const isLoading = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;
const toast = useToast();

const triggerFileInput = () => {
    fileInput.value?.click();
};

const handleUpload = async (e: Event) => {
    const target = e.target as HTMLInputElement;
    if (!target.files || target.files.length === 0) return;

    const selectedImage = target.files[0];
    isLoading.value = true;

    try {
        const newUrl = await uploadImageToStore(selectedImage);

        if (user.value) {
            const oldPhotoUrl = user.value.photoUrl;

            if (oldPhotoUrl && oldPhotoUrl !== configurations.userDefaultPic) {
                await deleteImageFromStore(oldPhotoUrl);
            }

            await updateUserField("id", user.value.id, "photoUrl", newUrl);
            refetchUser?.();
            toast.add({ severity: 'success', summary: 'Success', detail: 'Image updated successfully!', life: 3000 });
        }
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to update image.', life: 3000 });
    } finally {
        isLoading.value = false;
    }
};
</script>
