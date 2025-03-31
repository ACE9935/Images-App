import { firestore } from "firebase-admin";
import { User } from "../types/UserInterface";

export async function getUserById(id: string): Promise<User | null> {
  try {
    const db = firestore(); // Get Firestore instance from Admin SDK
    const usersRef = db.collection("users");

    // Query for the document with the specified 'id'
    const querySnapshot = await usersRef.where("id", "==", id).get();

    // Check if there are any matching documents
    if (!querySnapshot.empty) {
      const userDoc = querySnapshot.docs[0];
      const userData = userDoc.data() as User;
      return userData;
    } else {
      // No matching document found
      return null;
    }
  } catch (error) {
    console.error("Error getting user document: ", error);
    throw error; // Re-throwing the error to be handled by the caller
  }
}
