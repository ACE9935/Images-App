import { collection, query, where, getDocs, updateDoc, arrayUnion, getDoc } from "firebase/firestore";
import { db } from "./firebase";

async function addImageToViewed(userId: string, imageId: string): Promise<boolean> {
    try {
        // Query to find the user document with the specified `id` field
        const usersRef = collection(db, "users");
        const q = query(usersRef, where("id", "==", userId));
        const querySnapshot = await getDocs(q);

        if (!querySnapshot.empty) {
            // If user exists, get their document reference
            const userDoc = querySnapshot.docs[0].ref;

            // Get the current viewedImages array
            const userDocSnapshot = await getDoc(userDoc);
            const userData = userDocSnapshot.data();
            const viewedImages = userData?.viewedImages || [];

            // Check if the image already exists in the viewedImages array
            const imageIndex = viewedImages.findIndex((item: { imgId: string }) => item.imgId === imageId);

            if (imageIndex !== -1) {
                // Image exists, update the date to the current date
                viewedImages[imageIndex].date = new Date();

                // Update the user document with the new viewedImages array
                await updateDoc(userDoc, {
                    viewedImages: viewedImages,
                });

                return false;

            } else {
                // Image doesn't exist, add a new entry
                await updateDoc(userDoc, {
                    viewedImages: arrayUnion({ imgId: imageId, date: new Date() }),
                });

                console.log("Image added to viewed images successfully!");
                return true;
            }
        } else {
            console.log("User does not exist. No changes were made.");
        } 
        return false; // Return false if the user does not exist
    } catch (error) {
        console.error("Error:", error);
        throw new Error("Failed to add image to viewed images.");
    }
}

export default addImageToViewed;
