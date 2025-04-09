import { collection, getDocs } from "firebase/firestore";
import { db } from "./firebase";
import type { UserPreview } from "../types/UserPreview";

export async function searchUsersByUsername(searchTerm: string): Promise<UserPreview[]> {
  try {
    const usersRef = collection(db, 'users');
    
    // Fetch all users (or consider paginating/limiting if your user base is large)
    const querySnapshot = await getDocs(usersRef);
    
    const lowerSearch = searchTerm.toLowerCase();

    const matchingUsers: UserPreview[] = querySnapshot.docs
      .map(doc => {
        const data = doc.data();
        return {
          id: data.id,
          userName: data.userName,
          photoUrl: data.photoUrl,
        };
      })
      .filter(user => user.userName.toLowerCase().includes(lowerSearch));

    return matchingUsers;
  } catch (error) {
    console.error("Error searching users by userName: ", error);
    throw error;
  }
}
