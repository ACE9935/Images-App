<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import type { ImageMetaData } from '../types/ImageMetaData';
import { fetchImages, fetchSimilarImages } from '../http-api';
import Popover from 'primevue/popover';
import BasicActionButton from './form/BasicActionButton.vue';
import ImagesContainer from './ImagesContainer.vue';
import LoaderLogo from './utility/LoaderLogo.vue';

const props = defineProps<{ image: ImageMetaData | null }>();
const op = ref();

const similarImages = ref<ImageMetaData[]>([]);
const numOfSimilarImages = ref(10);
const numOfImages=ref(0)
const descrOfSimilarImages = ref<"histogram_2d" | "histogram_3d" | "histogram_of_visual_words">("histogram_of_visual_words");
const errorMessage = ref("");
const isLoading = ref(false);

const showMoreImages = () => {
  numOfSimilarImages.value += 10;
  getSimilarImages();
};

const toggle = (event:Event) => {
    op.value.toggle(event);
}

const getSimilarImages = async () => {
  try {
    if (!props.image) {
      console.error("Image not found");
      return;
    }
    errorMessage.value = "";
    isLoading.value = true;
    const similarImagesResponse = await fetchSimilarImages(
      String(props.image.id),
      String(numOfSimilarImages.value),
      descrOfSimilarImages.value
    );
    similarImages.value = similarImagesResponse;
  } catch (error) {
    console.error("Failed to load images:", error);
    errorMessage.value = "Failed to load similar images.";
  } finally {
    isLoading.value = false;
  }
};

onMounted(async ()=>{
  const data=await fetchImages();
  numOfImages.value=data.length
})

watch(
  () => props.image,
  (newImage) => {
    if (newImage) {
      numOfSimilarImages.value = 10;
      getSimilarImages();
    }
  },
  { immediate: true }
);

</script>

<template>
  <div class="text-black flex flex-col gap-3 w-full max-w-[70rem]">
    <div class="flex justify-between">
      <h2 class="font-bold text-3xl delius-regular">Most Similar Images:</h2>
      <div class="relative">

        <BasicActionButton class="py-3 md:py-2" icon="pi-filter" variant="gray" :is-loading="isLoading" :on-click="toggle">
          <div class="hidden md:block">Similarity Descriptor</div>
        </BasicActionButton>

        <Popover ref="op">
          <ul class="w-48 rounded-md">
            <li 
              v-for="option in [
                { value: 'histogram_2d', label: 'HSV' },
                { value: 'histogram_3d', label: 'RGB' },
                { value: 'histogram_of_visual_words', label: 'Visual words' }
              ]" 
              :key="option.value" 
              class="p-2 cursor-pointer hover:bg-gray-200"
              @click="descrOfSimilarImages = option.value as 'histogram_2d' | 'histogram_3d' | 'histogram_of_visual_words'; op.hide();numOfSimilarImages=10; getSimilarImages();"
            >
              {{ option.label }}
            </li>
          </ul>
        </Popover>
      </div>
    </div>
    
    <LoaderLogo v-if="similarImages.length==0"/>
    <div v-else>
     <ImagesContainer :class="{'opacity-50 pointer-events-none': isLoading}" class="transition-opacity duration-300" :images="similarImages"/>
     <div v-if="similarImages.length != (numOfImages-1)" class="flex justify-center mt-4">
  <BasicActionButton icon="pi-plus" :is-loading="isLoading" :on-click="showMoreImages">
    Show More
  </BasicActionButton>
</div>
  </div>
  </div>
</template>

<style scoped>
ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
</style>