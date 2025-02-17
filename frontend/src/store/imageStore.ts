import { defineStore } from "pinia";

export const useImageStore = defineStore("imageStore", {
    state: () => ({
        selectedImage: null as string | null,
        reloadTrigger: 0, // Counter to trigger watchers
    }),
    actions: {
        selectImage(imageId: string) {
            this.selectedImage = String(imageId);
        },
        clearSelection() {
            this.selectedImage = null;
        },
        triggerReload() {
            this.reloadTrigger += 1; // Increment to signal a refetch
        },
    },
});
