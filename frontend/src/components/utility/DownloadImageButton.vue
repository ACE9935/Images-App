<script setup lang="ts">
import downloadImage from '../../utils/downloadImage';
import { incrementDownload } from '../../http-api';
import { ref } from 'vue';
import BasicActionButton from '../form/BasicActionButton.vue';

const props = defineProps({
  imgName: {
    type: String,
    required: true,
  },
  imgId: {
    type: String,
    required: true,
  },
  refetchData: {
    type: Function,
  }
});

const isLoading = ref(false);

const handleDownload = async () => {
  try {
    isLoading.value = true;
    await downloadImage(props.imgName);
    await incrementDownload(props.imgId);
    if (props.refetchData) {
      await props.refetchData();
    }
  } catch (e) {
    console.error(e);
  }
  finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <BasicActionButton class="py-3" :is-loading="isLoading" icon="pi-download" @click="handleDownload">
    <div class="hidden">Download Image</div>
  </BasicActionButton>
</template>
