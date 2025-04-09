import { getAuth, updateProfile } from "firebase/auth";
import { collection, query, where, getDocs, updateDoc, getFirestore } from "firebase/firestore";

// Function to update username in both Firebase Auth and Firestore
export async function updateUsername(userId: string, newUsername: string): Promise<void> {
    try {
        // 1. Update in Firebase Authentication
        const auth = getAuth();
        const user = auth.currentUser;

        if (user && user.uid === userId) {
            // Update the username in Firebase Authentication (displayName)
            await updateProfile(user, {
                displayName: newUsername,
            });
            console.log("Username updated in Firebase Authentication");

            // 2. Update in Firestore (User document) based on custom id field
            const db = getFirestore();
            const usersRef = collection(db, "users");

            // Query to find the document based on the custom 'id' field
            const q = query(usersRef, where("id", "==", userId));
            const querySnapshot = await getDocs(q);

            if (!querySnapshot.empty) {
                // If the document exists, update it
                const userDocRef = querySnapshot.docs[0].ref;

                // Update the Firestore user document
                await updateDoc(userDocRef, {
                    userName: newUsername,
                });
                console.log("Username updated in Firestore");
            } else {
                console.error("No user found with the provided ID.");
            }
        } else {
            console.error("User ID does not match the current authenticated user.");
        }
    } catch (error) {
        console.error("Error updating username:", error);
    }
}
