import { collection, query, where, getDocs, updateDoc } from "firebase/firestore";
import { db } from "./firebase";
import type { Album } from "../types/Album";

async function removeImageFromAlbum(userId: string, albumId: number, imageId: string): Promise<void> {
  try {
    const usersRef = collection(db, "users");
    const q = query(usersRef, where("id", "==", userId));
    const querySnapshot = await getDocs(q);

    if (!querySnapshot.empty) {
      const userDoc = querySnapshot.docs[0].ref;
      const userData = querySnapshot.docs[0].data();
      const albums: Album[] = userData.albums || [];

      const updatedAlbums = albums.map(album => {
        if (album.id === albumId) {
          return {
            ...album,
            images: album.images.filter(id => id !== imageId),
          };
        }
        return album;
      });

      await updateDoc(userDoc, {
        albums: updatedAlbums,
      });

      console.log("Image removed from album successfully!");
    } else {
      console.log("User does not exist. No changes were made.");
    }
  } catch (error) {
    console.error("Error:", error);
    throw new Error("Failed to remove image from album.");
  }
}

export default removeImageFromAlbum;
