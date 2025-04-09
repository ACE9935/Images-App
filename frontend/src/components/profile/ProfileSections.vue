<template>
   <ProfileSectionsNavigator v-model:selectedSection="selectedSection" />
   <div>
    <div v-if="selectedSection === 'upload-section'">
       <ImageUploader/>
    </div>
    <div v-else-if="selectedSection === 'favorites-images-section'">
      <FavImages/>
    </div>
    <div v-else-if="selectedSection === 'albums-section'">
      <AlbumSections/>
    </div>
    <div v-else-if="selectedSection === 'history-section'">
      <ImagesViewHistory/>
    </div>
    <div v-else-if="selectedSection === 'uploaded-images-section'">
      <UploadedImages/>
    </div>
   </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ProfileSectionsNavigator from './ProfileSectionsNavigator.vue';
import type { Section } from '../../types/ProfileSections';
import ImageUploader from '../ImageUploader.vue';
import FavImages from './FavImages.vue';
import UploadedImages from './UploadedImages.vue';
import ImagesViewHistory from './ImagesViewHistory.vue';
import AlbumSections from './AlbumSections.vue';

const selectedSection = ref<Section>('upload-section');

</script>