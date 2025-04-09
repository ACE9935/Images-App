import { getAuth, deleteUser, signOut } from "firebase/auth";
import { collection, query, where, getDocs, deleteDoc, getFirestore } from "firebase/firestore";

// Function to delete user from Firestore and Firebase Authentication, then sign them out
export async function deleteUserAccount(userId: string): Promise<void> {
    try {
        // 1. Get Firebase Auth instance
        const auth = getAuth();
        const user = auth.currentUser;

        if (user && user.uid === userId) {
            // 2. Delete user in Firebase Authentication
            await deleteUser(user);  // Delete the user from Firebase Authentication
            console.log("User deleted from Firebase Authentication");

            // 3. Sign out after deleting user
            await signOut(auth); // Sign the user out
            console.log("User signed out");

            // 4. Delete user document from Firestore based on custom 'id' field
            const db = getFirestore();
            const usersRef = collection(db, "users");

            // Query to find the document based on the custom 'id' field
            const q = query(usersRef, where("id", "==", userId));
            const querySnapshot = await getDocs(q);

            if (!querySnapshot.empty) {
                // If the document exists, delete it
                const userDocRef = querySnapshot.docs[0].ref;

                // Delete the Firestore user document
                await deleteDoc(userDocRef);
                console.log("User deleted from Firestore");
            } else {
                console.error("No user found with the provided ID in Firestore.");
            }
        } else {
            console.error("User ID does not match the current authenticated user.");
        }
    } catch (error) {
        console.error("Error deleting user:", error);
    }
}