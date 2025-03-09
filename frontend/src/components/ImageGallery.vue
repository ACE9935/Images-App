<script setup>
import { ref, onMounted } from "vue";
import { fetchImages } from "../http-api";
import { useImageStore } from "../store/imageStore";
import { register } from 'swiper/element/bundle';
import 'swiper/swiper-bundle.css'; // Import Swiper styles
import axios from "axios";

register();

const images = ref([]);
const API_BASE_URL = "http://localhost:8001";

const deleteImage = async (id) => {
    try {
        await axios.delete(`${API_BASE_URL}/images/${id}`);
        images.value = images.value.filter(img => img.id !== id);
        const imageStore = useImageStore();
        imageStore.triggerReload();
    } catch (error) {
        console.error("Erreur lors de la suppression :", error);
    }
};

onMounted(async () => {
    images.value = await fetchImages();
});
</script>

<template>
  <div>
    <h2>Galerie d'Images</h2>
    <div v-if="images.length === 0">Aucune image trouvée.</div>
    <div v-else class="gallery">
      <swiper-container class="image-container" v-for="img in images" :key="img.id" :slides-per-view="3" :speed="500" loop="true" free-mode="true">
        <swiper-slide>
          <img :src="img.url" class="gallery-image" />
        </swiper-slide>
      </swiper-container>
    </div>
  </div>
</template>

<style>
.image-container {
  width: 20rem;
}
.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
