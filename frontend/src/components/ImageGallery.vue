<script setup>
import { ref, onMounted } from "vue";
import { fetchImages } from "../http-api";
import axios from "axios";

const images = ref([]);
const API_BASE_URL = "http://localhost:8001/api";

const deleteImage = async (id) => {
    try {
        await axios.delete(`${API_BASE_URL}/images/${id}`);
        images.value = images.value.filter(img => img.id !== id); // Mise à jour locale
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
      <div v-for="img in images" :key="img.id" class="image-container">
        <img :src="img.url" class="gallery-image" />
        <button @click="deleteImage(img.id)">🗑 Supprimer</button>
      </div>
    </div>
  </div>
</template>

<style>
.image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
