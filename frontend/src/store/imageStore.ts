import { defineStore } from "pinia";
import type { ImageMetadata } from "../types";
import { fetchImageMetaDataById, fetchImages } from "../http-api";

export const useImageStore = defineStore("imageStore", {
    state: () => ({
        selectedImage: null as string | null,
        reloadTrigger: 0,
        imagesList: [] as ImageMetadata[],
        selectedImageMetaData: {} as ImageMetadata | null,
    }),
    actions: {
        async selectImage(imageId: string) {
            this.selectedImage = String(imageId);
            const imgData= await fetchImageMetaDataById(imageId);
            this.selectedImageMetaData=imgData;
        },
        async fetchImagesList() {
            const images = await fetchImages();
            this.imagesList = images;
        },
        setSelectedImageMetaData(data: ImageMetadata) {
            this.selectedImageMetaData = data;
        },
        resetSelectedImageMetaData() {
            this.selectedImageMetaData = null;
        },
        selectNextImage() {
            this.selectedImage = String(Number(this.selectedImage) + 1);
        },
        selectPrevImage() {
            this.selectedImage = String(Number(this.selectedImage) - 1);
        },
        clearSelection() {
            this.selectedImage = null;
        },
        triggerReload() {
            this.reloadTrigger += 1; // Increment to signal a refetch
        },
    },
});
