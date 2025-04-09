import { collection, query, where, getDocs, updateDoc, arrayUnion } from "firebase/firestore";
import type { DocumentData } from "firebase/firestore";
import { db } from "./firebase";
import type { Album } from "../types/Album";

async function addNewAlbumToUser(userId: string, albumName: string, imageId: string): Promise<void> {
  try {
    const usersRef = collection(db, "users");
    const q = query(usersRef, where("id", "==", userId));
    const querySnapshot = await getDocs(q);

    if (!querySnapshot.empty) {
      const userDocSnap = querySnapshot.docs[0];
      const userDoc = userDocSnap.ref;
      const userData = userDocSnap.data() as DocumentData;

      const existingAlbums: Album[] = userData.albums || [];
      const nextId = existingAlbums.length > 0
        ? Math.max(...existingAlbums.map((a) => a.id ?? 0)) + 1
        : 0;

      const newAlbum: Album = {
        id: nextId,
        name: albumName,
        images: [imageId],
        date: new Date(), // <-- added date here
      };

      await updateDoc(userDoc, {
        albums: arrayUnion(newAlbum),
      });

      console.log("Album added successfully!");
    } else {
      console.log("User does not exist. No changes were made.");
    }
  } catch (error) {
    console.error("Error:", error);
    throw new Error("Failed to add album.");
  }
}

export default addNewAlbumToUser;
