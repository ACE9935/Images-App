<script setup lang="ts">
import { ref, onMounted } from "vue";
import { register, type SwiperContainer } from 'swiper/element/bundle';
import 'swiper/swiper-bundle.css'; 
import CIFAR10_CLASSES from "../../utils/ImagesClasses";
import type { CIFAR10Classes } from "../../types/cifar10";

register();

const props = defineProps({
  selectedClass: {
    type: String as () => CIFAR10Classes,
    required: true,
  }
});

const emit = defineEmits(['update-selected-class']);

// Removed duplicate declaration of selectClass

const swiperEl = ref<SwiperContainer | null>(null);

const swiperNextButton = ref<HTMLElement | null>(null);
const swiperPrevButton = ref<HTMLElement | null>(null);

onMounted(() => {
  swiperPrevButton.value?.addEventListener('click', () => {
    swiperEl.value?.swiper.slidePrev();
  });
  swiperNextButton.value?.addEventListener('click', () => {
    swiperEl.value?.swiper.slideNext();
  });
});

const selectClass = (newClass: CIFAR10Classes) => {
  emit('update-selected-class', newClass);
};

</script>

<template>
  <div class="flex items-center gap-2 max-w-full">
    <button ref="swiperPrevButton" class="prev-button">
      <i class="pi pi-angle-left" style="font-size: 1.2rem"></i>
    </button>
    <swiper-container
      ref="swiperEl"
      class="container w-full max-w-[45rem]"
      :speed="300"
      free-mode="true"
      slides-per-view="auto"
      space-between="40"
    >
      <swiper-slide class="w-auto" v-for="(imgClass, index) in CIFAR10_CLASSES" :key="index">
        <button
          :aria-label="`Navigate to ${imgClass} images`"
          @click="selectClass(imgClass)"
          :class="[
            'text-black cursor-pointer font-semibold px-4 py-2 rounded-md transition-all',
            props.selectedClass === imgClass ? 'bg-gray-100' : ''
          ]"
        >
          {{ imgClass }}
        </button>
      </swiper-slide>
    </swiper-container>
    <button ref="swiperNextButton" class="next-button">
      <i class="pi pi-angle-right" style="font-size: 1.2rem"></i>
    </button>
  </div>
</template>

<style scoped>
.prev-button,
.next-button {
  color: black;
  cursor: pointer;
  margin-top: 5px;
}
</style>