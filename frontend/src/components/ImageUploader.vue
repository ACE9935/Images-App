<script setup lang="ts">
import { ref, inject, computed } from 'vue';
import { uploadImage } from '../http-api';
import type { UserContext } from '../types/UserContext';
import BasicActionButton from './form/BasicActionButton.vue';
import BasicInput from './form/BasicInput.vue';
import { useToast } from 'primevue';
import Dialog from 'primevue/dialog';
import addImageToUploaded from '../firebase/addImageToUploaded';

const file = ref<File | null>(null);
const toast=useToast()
const title = ref<string>("");
const errorMessage = ref<string>("");
const isDragging = ref<boolean>(false);
const visibleDialog = ref(false);
const isUploading = ref<boolean>(false);

const userContext = inject<UserContext>('userContext');

const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;

const handleCLick=async ()=>{
  if (!file.value){
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select a file to upload.', life: 3000 })
      return
    }
    else {
      visibleDialog.value=true
    }
}

const submitFile = async (e:Event) => {
  e.preventDefault();
  errorMessage.value = "";
  try {

    if(title.value.length < 1){
      errorMessage.value = "Title must be at least 1 character long.";
      return
    }
    if (!file.value){
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select a file to upload.', life: 3000 })
      return
    }
    isUploading.value = true;

    if(!user.value) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'User not found, Login to upload images', life: 3000 });
      throw new Error('User not found, Login to upload images');
    }

    const data=await uploadImage(file.value, user?.value.id, title.value);
    await addImageToUploaded(user?.value.id, String(data.id));
    await refetchUser?.();
    toast.add({ severity: 'success', summary: 'Success', detail: 'Image uploaded successfully!', life: 3000 });
    file.value = null;
  } catch (error) {
    console.error("Upload failed:", error);
    toast.add({ severity: 'error', summary: 'Error', detail: 'Upload failed. Please try again.', life: 3000 });
  } finally {
    isUploading.value = false;
    visibleDialog.value = false;
  }
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    file.value = target.files[0];
  }
};

const handleDragOver = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = true;
};

const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
  
  const droppedFile = event.dataTransfer?.files[0];
  if (droppedFile && droppedFile.type.startsWith('image/')) {
    file.value = droppedFile;
  } else {
  toast.add({ severity: 'error', summary: 'Error', detail: 'Please drop a valid image file.', life: 3000 });
  }
};
</script>

<template>
    <Dialog v-model:visible="visibleDialog" modal header="Image Title" class="w-min">
     <div class="text-black flex flex-col gap-6 p-8 pt-2">
      <div class="flex self-end relative">
      </div>
  
      <form @submit="submitFile" class="flex flex-col gap-3">
        <BasicInput
        maxLength="24"
        :helper-text="errorMessage"
        :error="errorMessage.length>0"
        :style="{ width: '20rem' }"
        placeholder="Enter a title..."
        v-model="title"
        id="name-reset-input"
        label="Title*" 
          />
          <BasicActionButton :is-loading="isUploading" :on-click="submitFile" type="submit">Confirm</BasicActionButton>
        
      </form>
    </div>
    </Dialog>
    <div v-bind="$attrs" class="w-full bg-white p-4 border-2 border-gray-200 rounded-md flex flex-col items-center">
      <h2 class="text-black text-2xl font-bold text-center">Upload your images</h2>
      <p class="text-slate-500">Image should be: JPG or PNG</p>

      <div 
        class="container bg-slate-100" 
        @dragover.prevent="handleDragOver" 
        @drop.prevent="handleDrop"
        :class="{ 'dragging': isDragging }"
      >
        <div class="uploader-element flex flex-col gap-1 items-center">
          <i class="pi pi-upload text-slate-700" style="font-size: 2rem"></i>
          <p class="text-slate-500">Drag & Drop your file or</p>

          <label class="rounded-md grow justify-center font-bold bg-amber-400 hover:bg-amber-500 text-white p-2 px-5 flex gap-2 items-center cursor-pointer transition-all">
            Browse
            <input 
              type="file" 
              accept="image/png, image/jpeg"
              @change="handleFileUpload" 
              class="hidden"
            />
          </label>

          <!-- Display Selected File Name -->
          <p v-if="file" class="mt-3 text-black">Selected file: {{ file.name }}</p>
        </div>
      </div>

      <div class="flex justify-end w-full">
      <BasicActionButton icon="pi-upload" variant="gray" :is-loading="isUploading" :on-click="handleCLick">
         Submit
      </BasicActionButton>
    </div>
    </div>

</template>

<style scoped>
.container {
  margin-block: 1rem;
  display: grid;
  place-items: center;
  cursor: pointer;
  border: 2px dashed #ccc;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  position: relative;
}

.container.dragging {
  background-color: #f0f0f0;
  border-color: #00f;
}

.hidden {
  display: none;
}

.uploader-element {
  display: flex;
  gap: 0.5rem;
  flex-direction: column;
}
</style>
