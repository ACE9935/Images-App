<script setup lang="ts">
import { ref } from 'vue';
import { uploadImage } from '../http-api';
import { useImageStore } from '../store/imageStore';

const file = ref<File | null>(null);
const isDragging = ref<boolean>(false);
const isUploading = ref<boolean>(false);

const imageStore = useImageStore();

const submitFile = async () => {
  if (!file.value) {
    alert('Please select a file before submitting.');
    return;
  }

  isUploading.value = true;
  try {
    const response = await uploadImage(file.value);
    imageStore.triggerReload();
    imageStore.selectImage(response.id);
    alert(`Image with ID: ${response.id} added successfully`);
    file.value = null;
  } catch (error) {
    console.error("Upload failed:", error);
    alert('Failed to upload image.');
  } finally {
    isUploading.value = false;
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
    alert('Please drop a valid image file (JPG or PNG).');
  }
};
</script>

<template>
  <section class="flex justify-center w-full shrink">
    <div class="w-full bg-white p-4 rounded-md shadow-lg flex flex-col items-center">
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

          <label class="rounded-md grow justify-center font-bold bg-amber-400 shadow-md text-white p-3 px-5 flex gap-2 items-center cursor-pointer transition-all">
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

      <!-- Submit Button -->
      <button 
        class="rounded-md grow justify-center font-bold text-white p-3 px-5 flex gap-2 items-center transition-all self-end"
        :class="{
          'bg-gray-400 cursor-wait': isUploading, 
          'bg-blue-400 hover:bg-blue-600 cursor-pointer': !isUploading
        }"
        :disabled="isUploading"
        @click="submitFile"
      >
        <span v-if="!isUploading">Submit</span>
        <i v-if="isUploading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
      </button>
    </div>
  </section>
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
