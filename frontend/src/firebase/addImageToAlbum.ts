import { collection, query, where, getDocs, updateDoc } from "firebase/firestore";
import { db } from "./firebase";
import type { Album } from "../types/Album";

async function addImageToAlbum(userId: string, albumId: number, imageId: string): Promise<void> {
  try {
    const usersRef = collection(db, "users");
    const q = query(usersRef, where("id", "==", userId));
    const querySnapshot = await getDocs(q);

    if (!querySnapshot.empty) {
      const userDoc = querySnapshot.docs[0].ref;
      const userData = querySnapshot.docs[0].data();
      const albums: Album[] = userData.albums || [];

      // Find and update the matching album
      const updatedAlbums = albums.map(album => {
        if (album.id === albumId) {
          // Only add image if it's not already in the album
          if (!album.images.includes(imageId)) {
            return {
              ...album,
              images: [...album.images, imageId],
            };
          }
        }
        return album;
      });

      await updateDoc(userDoc, {
        albums: updatedAlbums,
      });

      console.log("Image added to album successfully!");
    } else {
      console.log("User does not exist. No changes were made.");
    }
  } catch (error) {
    console.error("Error:", error);
    throw new Error("Failed to add image to album.");
  }
}

export default addImageToAlbum;
