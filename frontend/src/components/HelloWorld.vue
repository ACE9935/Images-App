<script>
import { fetchImageById, fetchImages } from "../http-api";
import { useImageStore } from "../store/imageStore";

export default {
  data() {
    return {
      images: [],
      errorMessage: "", // To store error messages
    };
  },
  methods: {
    selectImage(event) {
      const imageStore = useImageStore(); // Call inside the method
      imageStore.selectImage(event.target.value);
    },
    async fetchImagesList() {
      try {
        this.images = await fetchImages(); // Fetch the list of images
      } catch (error) {
        console.error(error);
        this.errorMessage = "Failed to load images.";
      }
    },
    async fetchImage(id) {
      try {
        const imageEl = document.getElementById("img-preview");
        const response = await fetchImageById(id);
        const reader = new window.FileReader();
        reader.readAsDataURL(response);
        reader.onload = function () {
          const imageDataUrl = reader.result;
          imageEl.setAttribute("src", imageDataUrl);
        };
      } catch (error) {
        console.error("Failed to fetch the image:", error);
        this.errorMessage = "Failed to load the selected image.";
      }
    },
  },
  mounted() {
    const imageStore = useImageStore();
    this.fetchImagesList();
    imageStore.selectImage("0")
  },
  watch: {
    // Watch the `selectedImage` in the Pinia store
    selectedImage: {
      handler(newImageId) {
        if (newImageId) {
          this.fetchImage(newImageId);
        }
      },
      immediate: true, // Trigger the watcher immediately on component load
    },
    reloadTrigger:{
      handler(newValue) {
        if (newValue) {
          this.fetchImagesList();
        }
      },
      immediate: true,
    }
  },
  computed: {
    reloadTrigger(){
      const imageStore = useImageStore();
      return imageStore.reloadTrigger;
    },
    selectedImage() {
      const imageStore = useImageStore();
      return imageStore.selectedImage;
    },
  },
};
</script>

<template>
  <div class="container">
    <h1>Image List</h1>
    
    <select
      v-if="images.length > 0"
      :value="selectedImage"
      name="images"
      id="images-selector"
      @change="selectImage"
    >
      <option v-for="(image, index) in images" :key="index" :value="String(image.id)">
        {{ image.name }}
      </option>
    </select>
    <p v-else>No images found.</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    <img id="img-preview" src="" />
  </div>
</template>

<style scoped>
h1 {
  color: white;
}
select {
  width: fit-content;
}
.container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  align-items: center;
}
#img-preview {
  border: 2px dashed rgba(255, 58, 4, 0.82);
}
.read-the-docs {
  color: #888;
}
</style>
