<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import ImageMetaDataTable from "./ImageMetaDataTable.vue";
import { fetchImageById, deleteImageById } from "../http-api";
import { useImageStore } from "../store/imageStore";
import SimilarImages from "./SimilarImages.vue";
import ImageClassification from "./ImageClassification.vue";
import AppAlert from "./utility/AppAlert.vue";

const imgStore = useImageStore();

const errorMessage = ref<string>("");
const isDeleting = ref<boolean>(false);

const selectedImage = computed(() => imgStore.selectedImage);
const reloadTrigger = computed(() => imgStore.reloadTrigger);

const selectImage = (event: Event) => {
  const target = event.target as HTMLSelectElement;
  try {
    imgStore.selectImage(target.value);
  } catch (error) {
    console.error(error);
    errorMessage.value = "Failed to select image";
  }
};

const downloadImage = async () => {
  try {
    if (!imgStore.selectedImage) return;
    const response = await fetchImageById(imgStore.selectedImage);

    const blob = response instanceof Blob ? response : await response.blob();
    const url = URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.download = imgStore.selectedImageMetaData?.name || "image.jpg";
    link.click();

    URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Failed to fetch the image:", error);
    errorMessage.value = "Failed to fetch the image.";
  }
};

// Function to delete the selected image
const deleteImage = async () => {
  try {
    if (!imgStore.selectedImage) return;
    isDeleting.value = true;
    
    await deleteImageById(imgStore.selectedImage);
    await imgStore.fetchImagesList();
    alert(`Image with ID: ${imgStore.selectedImage} deleted successfully`);

    imgStore.triggerReload();
    if (imgStore.imagesList.length > 0) {
      imgStore.selectImage(String(imgStore.imagesList[0].id));
    } else {
      imgStore.resetSelectedImageMetaData();
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = "Failed to delete image";
  } finally {
    isDeleting.value = false;
  }
};

// Function to fetch and display the selected image
const fetchImage = async (id: string) => {
  try {
    const imageEl = document.getElementById("img-preview") as HTMLImageElement | null;
    if (!imageEl) return;

    const response = await fetchImageById(id);
    const reader = new FileReader();

    reader.onload = () => {
      if (reader.result) {
        imageEl.src = reader.result.toString();
      }
    };

    reader.readAsDataURL(response);
  } catch (error) {
    console.error("Failed to fetch the image:", error);
    errorMessage.value = "Failed to load the selected image.";
  }
};

// On component mount, fetch image list and select the first one
onMounted(async () => {
  await imgStore.fetchImagesList();
  if (imgStore.imagesList.length > 0) {
    imgStore.selectImage(String(imgStore.imagesList[0].id));
  }
});

// Watch for image selection changes
watch(selectedImage, (newImageId) => {
  if (newImageId) {
    errorMessage.value = "";
    fetchImage(newImageId);
  }
}, { immediate: true });

// Watch for reload trigger
watch(reloadTrigger, (newValue) => {
  if (newValue) imgStore.fetchImagesList();
}, { immediate: true });
</script>

<template>
  <div class="flex flex-col items-center gap-8">
    <h1 class="text-beige text-center font-bold text-6xl">Image Listo</h1>
    
    <!-- Image Selector Dropdown -->
    <select
      v-if="imgStore.imagesList.length > 0"
      :value="selectedImage"
      name="images"
      class="text-black bg-white p-2 rounded-md shadow-md cursor-pointer hover:outline-green-400 outline-2 border-none"
      id="images-selector"
      @change="selectImage"
    >
      <option 
        v-for="(image, index) in imgStore.imagesList" 
        :key="index" 
        :value="String(image.id)"
      >
        {{ image.name }}
      </option>
    </select>

    <!-- No Images Found Message -->
    <AppAlert v-else icon="pi-image">
      No images found. Please upload an image.
    </AppAlert>

    <!-- Error Message -->
    <p 
      v-if="errorMessage" 
      class="flex gap-3 font-bold items-center bg-red-300 border-2 border-red-500 p-3 rounded-md w-[20rem] justify-center"
    >
      <i class="pi pi-exclamation-circle" style="font-size: 1rem"></i>{{ errorMessage }}
    </p>

    <!-- Image Preview and Controls -->
    <div 
      v-if="imgStore.imagesList.length > 0" 
      class="rounded-sm shadow-md bg-white p-3 flex flex-col lg:flex-row gap-4"
    >
      <img id="img-preview" class="max-w-full md:max-w-[40rem] w-auto h-full" src="" />

      <div class="flex flex-col gap-4">
        <ImageMetaDataTable v-if="imgStore.selectedImageMetaData" :image="imgStore.selectedImageMetaData" />

        <div class="flex gap-4">
          <!-- Download Button -->
          <button 
            @click="downloadImage" 
            class="rounded-md grow justify-center font-bold bg-blue-400 shadow-md text-white p-3 flex gap-2 items-center cursor-pointer hover:bg-blue-600 transition-all"
          >
            <i class="pi pi-download" style="font-size: 1rem"></i>Download
          </button>

          <!-- Delete Button -->
          <button 
            @click="deleteImage" 
            :disabled="isDeleting"
            class="rounded-md grow justify-center font-bold text-white p-3 flex gap-2 items-center transition-all"
            :class="{
              'bg-gray-400 cursor-wait': isDeleting, 
              'bg-red-400 hover:bg-red-600 cursor-pointer': !isDeleting
            }"
          >
            <i v-if="!isDeleting" class="pi pi-trash" style="font-size: 1rem"></i>
            <i v-if="isDeleting" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
            Delete
          </button>
        </div>

        <SimilarImages v-if="imgStore.selectedImageMetaData" :image="imgStore.selectedImageMetaData" />
        <ImageClassification  v-if="imgStore.selectedImageMetaData" :image="imgStore.selectedImageMetaData" />
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
