<template>
  <section class="flex justify-center w-full shrink">
    <div class="w-full bg-white p-4 rounded-md shadow-lg flex flex-col items-center">
      <h2 class="text-black text-2xl font-bold text-center">Upload your images</h2>
      <p class="text-slate-500">Image should be: JPG PNG</p>
      <div 
        class="container bg-slate-100" 
        @dragover.prevent="handleDragOver" 
        @drop.prevent="handleDrop"
        :class="{ 'dragging': isDragging }"
      >
        <div class="uploader-element flex flex-col gap-1 items-center">
          <i class="pi pi-upload text-slate-700" style="font-size: 2rem"></i>
          <p class="text-slate-500">Drag & Drop your file or</p>
          <label class="rounded-md grow justify-center font-bold bg-amber-400 shadow-md text-white p-3 px-5 flex gap-2 items-center cursor-pointer transition-all">Browse
            <input 
              type="file" 
              id="file" 
              ref="file" 
              @change="handleFileUpload" 
              class="hidden"
            />
          </label>
          <p v-if="file" class="mt-3 text-black">Selected file: {{ file.name }}</p>
        </div>
      </div>
      
      <button 
        class="rounded-md grow justify-center font-bold text-white p-3 px-5 flex gap-2 items-center transition-all self-end"
        :class="{'bg-gray-400 cursor-wait': isUploading, 'bg-blue-400 hover:bg-blue-600 cursor-pointer': !isUploading}"
        :disabled="isUploading"
        v-on:click="submitFile"
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

<script>
import { uploadImage } from '../http-api';
import { useImageStore } from '../store/imageStore';

export default {
  data() {
    return {
      file: null,
      isDragging: false, // Track if dragging is happening
      isUploading: false, // Track uploading state
    };
  },

  methods: {
    // Handle file submission
    async submitFile() {
      const imageStore = useImageStore();
      if (this.file) {
        this.isUploading = true;
        try {
          const response = await uploadImage(this.file);
          imageStore.triggerReload();
          imageStore.selectImage(response.id);
          alert(`Image with ID: ${response.id} added successfully`);
          this.file = null;
        } catch (error) {
          alert('Failed to upload image');
        } finally {
          this.isUploading = false; // Set uploading state to false when done
        }
      } else {
        alert('Please select a file before submitting.');
      }
    },

    handleFileUpload(event) {
      this.file = event.target.files[0];
    },

    handleDragOver(event) {
      this.isDragging = true;
    },

    // Handle the drop event
    handleDrop(event) {
      this.isDragging = false;
      const file = event.dataTransfer.files[0];
      if (file && file.type.startsWith('image/')) {
        this.file = file;
      } else {
        alert('Please drop an image file.');
      }
    },
  },
};
</script>
