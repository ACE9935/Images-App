import type { User } from "../types/UserInterface";
import { addDoc, collection, getDocs, query, where } from "firebase/firestore";
import { auth } from "./firebase";
import { deleteUser } from "firebase/auth";
import { db } from "./firebase";
import AuthServices from "../auth-services/services";

export async function addUser(user: User, provider: "Google" | "Email") {
  const collectionRef = collection(db, 'users');

  try {
    // Handle Email verification
    if (provider === "Email") {
      const response = await AuthServices.generateVerificationToken(user.email);
      const token = response?.data?.data;

      if (!token) {
        throw new Error("Unable to sign in user: missing verification token");
      }

      user = { ...user, verificationToken: token };
    }

    // Check if user already exists
    const q = query(collectionRef, where("id", "==", user.id));
    const querySnapshot = await getDocs(q);

    if (querySnapshot.empty) {
      await addDoc(collectionRef, user);
      console.log("User added to the store");
      return user;
    } else {
      console.log("User already exists in the store");
      return null;
    }

  } catch (error) {
    console.error("Error adding user:", error);

    try {
      await deleteUser(auth.currentUser!);
      console.log("Auth user deleted due to failure.");
    } catch (delError) {
      console.error("Failed to delete auth user:", delError);
    }

    throw error;
  }
}
