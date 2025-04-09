<template>
    <div class="w-full h-[30rem] bg-gray-200 border-2 border-dashed border-gray-700 grid place-items-center rounded-xl">
        <div class="flex flex-col items-center gap-3">
        <i class="pi pi-image text-black" style="font-size: 4rem"></i>
        <StyledHeading v-text="'No Data Was Found :('"></StyledHeading>
    </div>
    </div>
</template>

<script setup lang="ts">
import StyledHeading from '../StyledHeading.vue';
</script>