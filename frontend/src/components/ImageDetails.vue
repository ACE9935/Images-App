<script setup>
import { ref, defineProps, onMounted } from "vue";
import axios from "axios";

const API_BASE_URL = "http://localhost:8001/api";
const props = defineProps(["imageId"]);
const similarImages = ref([]);

const fetchSimilarImages = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/images/similar/${props.imageId}?N=5`);
        similarImages.value = response.data;
    } catch (error) {
        console.error("Erreur :", error);
    }
};

onMounted(fetchSimilarImages);
</script>

<template>
  <div>
    <h2>Images Similaires</h2>
    <div v-if="similarImages.length === 0">Aucune image similaire trouvée.</div>
    <div v-else class="gallery">
      <img v-for="img in similarImages" :key="img.id" :src="img.url" class="gallery-image" />
    </div>
  </div>
</template>
