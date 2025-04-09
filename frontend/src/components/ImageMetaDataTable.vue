<script setup lang="ts">
import type { ImageMetaData } from '../types/ImageMetaData';
defineProps<{ image: ImageMetaData }>();
</script>

<template>
  <div v-bind="$attrs" class="overflow-x-auto mx-auto w-full">
    <table class="w-full border-collapse border border-gray-300 rounded-lg">
      <thead>
        <tr class="bg-gray-100 text-gray-700 uppercase text-sm">
          <th class="px-4 py-3 text-left">ID</th>
          <th class="px-4 py-3 text-left">Name</th>
          <th class="px-4 py-3 text-left">Category</th>
          <th class="px-4 py-3 text-left">Size</th>
          <th class="px-4 py-3 text-left">Format</th>
        </tr>
      </thead>
      <tbody>
        <tr class="hover:bg-gray-50 text-black">
          <td class="px-4 py-2">{{ image.id }}</td>
          <td class="px-4 py-2 font-medium">{{ image.name }}</td>
          <td class="px-4 py-2 font-medium">{{ image.imgClass }}</td>
          <td class="px-4 py-2">{{ image.size }}</td>
          <td class="px-4 py-2">{{ image.format }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
