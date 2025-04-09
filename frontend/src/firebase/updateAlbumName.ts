import { collection, query, where, getDocs, updateDoc } from "firebase/firestore";
import { db } from "./firebase";
import type { Album } from "../types/Album";

async function updateAlbumName(userId: string, albumId: number, newName: string): Promise<void> {
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
            name: newName,
          };
        }
        return album;
      });

      await updateDoc(userDoc, {
        albums: updatedAlbums,
      });

      console.log("Album name updated successfully!");
    } else {
      console.log("User does not exist. No changes were made.");
    }
  } catch (error) {
    console.error("Error:", error);
    throw new Error("Failed to update album name.");
  }
}

export default updateAlbumName;
