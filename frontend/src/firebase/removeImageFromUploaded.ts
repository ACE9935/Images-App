import { collection, query, where, getDocs, updateDoc, arrayRemove } from "firebase/firestore";
import { db } from "./firebase";

async function removeImageFromUploaded(userId: string, imageId: string): Promise<void> {
    try {
        // Query to find the user document with the specified `id` field
        const usersRef = collection(db, "users");
        const q = query(usersRef, where("id", "==", userId));
        const querySnapshot = await getDocs(q);

        if (!querySnapshot.empty) {
            // If user exists, update their document
            const userDoc = querySnapshot.docs[0].ref;
            await updateDoc(userDoc, {
                uploadedImages: arrayRemove(imageId),
            });

            console.log("Image removed from uploaded successfully!");
        } else {
            console.log("User does not exist. No changes were made.");
        }
    } catch (error) {
        console.error("Error:", error);
        throw new Error("Failed to remove image from uploaded.");
    }
}

export default removeImageFromUploaded;
