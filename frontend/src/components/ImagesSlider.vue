<script setup lang="ts">
import { fetchImages } from "../http-api";
import { useImageStore } from "../store/imageStore";

interface Image {
  id: string; // Or number, depending on your ID type
  name: string;
}

// Access the Pinia store
const imageStore = useImageStore();

// Function to handle image selection
const selectImage = (imageId: string) => {
  imageStore.selectImage(imageId); // Update the global state with the selected image ID
};

// Local state to manage the list of images and errors
import { ref, onMounted, watch } from "vue";
const images = ref<Image[]>([]);
const errorMessage = ref("");

// Fetch the list of images
const fetchImagesList = async () => {
  try {
    images.value = await fetchImages(); // Call the function from http-api
  } catch (error) {
    console.error(error);
    errorMessage.value = "Failed to load images.";
  }
};

watch(() => imageStore.reloadTrigger, () => {
  fetchImagesList();
});

// Fetch images when the component is mounted
onMounted(() => {
  fetchImagesList();
});
</script>

<template>
  <div v-if="images.length > 0" class="container">
    <img
      v-for="(image, index) in images"
      :key="index"
      :src="'http://localhost:8001/imagesToVisualize/' + image.id"
      @click="selectImage(image.id)"
      alt="Image Preview"
      :class="{
       image: true,
       'selected-image': String(image.id) === imageStore.selectedImage
      }"
    />
  </div>
  <div v-else class="error-message">
    <p>{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  padding: 2rem;
  gap: 1rem;
  justify-content: center;
  align-items: center;
  overflow-x: auto;
  width: 90%;
}

.image {
  width: 8rem;
  cursor: pointer;
  opacity: 0.5;
  transition: transform 0.2s;
}

.selected-image {
  opacity: 1 !important; /* Example of highlighting selected image */
}

.image:hover {
  transform: scale(1.1);
}

.error-message {
  text-align: center;
  color: red;
  font-size: 1.2rem;
}
</style>
