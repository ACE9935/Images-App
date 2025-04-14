import { getAuth, deleteUser, signOut } from "firebase/auth";
import {
    getFirestore,
    collection,
    query,
    where,
    getDocs,
    deleteDoc,
} from "firebase/firestore";

// Deletes user document based on custom "id" field, then deletes Auth user and signs them out
export async function deleteUserAccount(userId: string): Promise<void> {
    try {
        const auth = getAuth();
        const user = auth.currentUser;

        if (!user || user.uid !== userId) {
            console.error("❌ No authenticated user or mismatched UID.");
            return;
        }

        // 1. Get Firestore reference
        const db = getFirestore();
        const usersRef = collection(db, "users");

        // 2. Find the document where "id" field === userId
        const q = query(usersRef, where("id", "==", userId));
        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            console.error("❌ No Firestore document found for user ID:", userId);
        } else {
            // 3. Delete the matched Firestore document
            const userDocRef = querySnapshot.docs[0].ref;
            await deleteDoc(userDocRef);
            console.log("✅ User document deleted from Firestore");
        }

        // 4. Delete the Firebase Auth user
        await deleteUser(user);
        console.log("✅ User deleted from Firebase Authentication");

        // 5. Sign out the user
        await signOut(auth);
        console.log("✅ User signed out");
    } catch (error: any) {
        console.error("❌ Error deleting user:", error.message || error);
    }
}
