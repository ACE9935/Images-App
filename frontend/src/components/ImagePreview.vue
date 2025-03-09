<script setup>
import { ref, computed, watch, onMounted } from "vue";
import ImageMetaDataTable from "./ImageMetaDataTable.vue";
import { fetchImageById, fetchImages, deleteImageById } from "../http-api";
import { useImageStore } from "../store/imageStore";

const imgStore = useImageStore();
const errorMessage = ref("");
const isDeleting = ref(false); // Track if the image is being deleted

const selectedImage = computed(() => imgStore.selectedImage);
const reloadTrigger = computed(() => imgStore.reloadTrigger);

const selectImage = async (event) => {
  try {
    imgStore.selectImage(event.target.value);
  } catch (error) {
    console.error(error);
    errorMessage.value = "Failed to select image";
  }
};

const downloadImage = async () => {
  try {
    const response = await fetchImageById(imgStore.selectedImage);
    
    const blob = response instanceof Blob ? response : await response.blob();

    const url = URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.download = imgStore.selectedImageMetaData.name || 'image.jpg';

    link.click();

    URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Failed to fetch the image:", error);
    errorMessage.value = "Failed to fetch the image.";
  }
};

const deleteImage = async () => {
  try {
    isDeleting.value = true; // Set deleting state to true
    await deleteImageById(imgStore.selectedImage);
    await imgStore.fetchImagesList();
    alert(`Image with ID: ${imgStore.selectedImage} deleted successfully`);
    imgStore.triggerReload();
    if (imgStore.imagesList.length > 0) {
      imgStore.selectImage(imgStore.imagesList[0].id);
    } else {
      imgStore.resetSelectedImageMetaData();
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = "Failed to delete image";
  } finally {
    isDeleting.value = false; // Set deleting state to false when done
  }
};

const fetchImage = async (id) => {
  try {
    const imageEl = document.getElementById("img-preview");
    const response = await fetchImageById(id);
    const reader = new window.FileReader();
    reader.readAsDataURL(response);
    reader.onload = () => {
      imageEl.setAttribute("src", reader.result);
    };
  } catch (error) {
    console.error("Failed to fetch the image:", error);
    errorMessage.value = "Failed to load the selected image.";
  }
};

onMounted(async () => {
  await imgStore.fetchImagesList();
  imgStore.selectImage(imgStore.imagesList[0].id);
});

watch(selectedImage, (newImageId) => {
  if (newImageId) {
    errorMessage.value = "";
    fetchImage(newImageId);
  }
}, { immediate: true });

watch(reloadTrigger, (newValue) => {
  if (newValue) imgStore.fetchImagesList();
}, { immediate: true });
</script>

<template>
  <div class="flex flex-col items-center gap-8">
    <h1 class="text-beige text-center font-bold text-6xl">Image List</h1>
    
    <select
      v-if="imgStore.imagesList.length > 0"
      :value="selectedImage"
      name="images"
      class="text-black bg-white p-2 rounded-md shadow-md cursor-pointer hover:outline-green-400 outline-2 border-none"
      id="images-selector"
      @change="selectImage"
    >
      <option v-for="(image, index) in imgStore.imagesList" :key="index" :value="String(image.id)">
        {{ image.name }}
      </option>
    </select>
    <p v-else class="flex gap-3 font-bold justify-center items-center bg-gray-300 border-2 border-gray-500 p-3 rounded-md text-black w-[20rem]"><i class="pi pi-image" style="font-size: 1rem"></i>No images found.</p>
    <p v-if="errorMessage" class="flex gap-3 font-bold items-center bg-red-300 border-2 border-red-500 p-3 rounded-md w-[20rem] justify-center"><i class="pi pi-exclamation-circle" style="font-size: 1rem"></i>{{ errorMessage }}</p>
    <div v-if="imgStore.imagesList.length > 0" class="rounded-sm shadow-md bg-white p-3 flex flex-col lg:flex-row gap-4">
      <img id="img-preview" class="max-w-full md:max-w-[40rem] w-auto" src="" />
      <div class="flex flex-col gap-4">
      <ImageMetaDataTable :image="imgStore.selectedImageMetaData"/>
      <div class="flex gap-4">
        <button 
          @click="downloadImage" 
          class="rounded-md grow justify-center font-bold bg-blue-400 shadow-md text-white p-3 flex gap-2 items-center cursor-pointer hover:bg-blue-600 transition-all"
        >
          <i class="pi pi-download" style="font-size: 1rem"></i>Download
        </button>
        <button 
          @click="deleteImage" 
          :disabled="isDeleting"
          class="rounded-md grow justify-center font-bold text-white p-3 flex gap-2 items-center transition-all"
          :class="{'bg-gray-400 cursor-wait': isDeleting, 'bg-red-400 hover:bg-red-600 cursor-pointer': !isDeleting}"
        >
          <i v-if="!isDeleting" class="pi pi-trash" style="font-size: 1rem"></i>
          <i v-if="isDeleting" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
          Delete
        </button>
      </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>

