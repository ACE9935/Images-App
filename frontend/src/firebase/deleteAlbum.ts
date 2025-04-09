import { collection, query, where, getDocs, updateDoc } from "firebase/firestore";
import type { DocumentData } from "firebase/firestore";
import type { Album } from "../types/Album";
import { db } from "./firebase";

async function deleteAlbumFromUser(userId: string, albumId: number): Promise<void> {
  try {
    const usersRef = collection(db, "users");
    const q = query(usersRef, where("id", "==", userId));
    const querySnapshot = await getDocs(q);

    if (!querySnapshot.empty) {
      const userDocSnap = querySnapshot.docs[0];
      const userDoc = userDocSnap.ref;
      const userData = userDocSnap.data() as DocumentData;

      const existingAlbums: Album[] = userData.albums || [];
      const updatedAlbums = existingAlbums.filter((album) => album.id !== albumId);

      await updateDoc(userDoc, {
        albums: updatedAlbums,
      });

      console.log(`Album with ID ${albumId} deleted successfully!`);
    } else {
      console.log("User does not exist. No changes were made.");
    }
  } catch (error) {
    console.error("Error:", error);
    throw new Error("Failed to delete album.");
  }
}

export default deleteAlbumFromUser;
