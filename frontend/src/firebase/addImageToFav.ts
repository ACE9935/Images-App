import { collection, query, where, getDocs, updateDoc, arrayUnion } from "firebase/firestore";
import { db } from "./firebase";

async function addImageToFav(userId: string, imageId: string): Promise<void> {
    try {
        // Query to find the user document with the specified `id` field
        const usersRef = collection(db, "users");
        const q = query(usersRef, where("id", "==", userId));
        const querySnapshot = await getDocs(q);

        if (!querySnapshot.empty) {
            // If user exists, update their document
            const userDoc = querySnapshot.docs[0].ref;
            await updateDoc(userDoc, {
                favImages: arrayUnion(imageId),
            });

            console.log("Image added to favorites successfully!");
        } else {
            console.log("User does not exist. No changes were made.");
        }
    } catch (error) {
        console.error("Error:", error);
        throw new Error("Failed to add image to favorites.");
    }
}

export default addImageToFav;