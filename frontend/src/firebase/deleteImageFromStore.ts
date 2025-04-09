import { storage } from './firebase';
import { ref, deleteObject } from 'firebase/storage';

/**
 * Deletes an image from Firebase Storage given its URL.
 * @param oldImageUrl - The URL of the image to delete.
 * @returns A promise that resolves if the image is deleted successfully, or rejects with an error.
 */
export const deleteImageFromStore = async (ImageUrl: string): Promise<void> => {
    if (!ImageUrl) return;

    try {
        const oldImageRef = ref(storage, ImageUrl);
        await deleteObject(oldImageRef);
        console.log("Old profile picture deleted successfully.");
    } catch (error) {
        console.warn("Failed to delete old profile picture:", error);
    }
};
