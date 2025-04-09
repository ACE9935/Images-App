import type { User } from "../types/UserInterface";
import { addDoc, collection, getDocs, query, where } from "firebase/firestore";
import { db } from "./firebase";
import AuthServices from "../auth-services/services";

export async function addUser(user:User,provider:"Google"|"Email") {
    try {
        const collectionRef = collection(db, 'users'); 
        if(provider=="Email"){

            AuthServices.generateVerificationToken(user.email)
            .then(response => {
              
              user={...user,verificationToken:response.data.data}
            })
            .catch(e => {
              console.log(e);
            });    

        }
        // Check if a document with the same ID already exists
        const q = query(collectionRef, where("id", "==", user.id));
        const querySnapshot = await getDocs(q);
        
        if (querySnapshot.empty) {
            await addDoc(collectionRef, user);
            console.log("User added to the store");
            return user
        }else{
            console.log("User already exists in the store");
            return null
        }

    } catch (error) {
        console.error("Error adding document: ", error);
        throw error; // Re-throwing the error to be handled by the caller
    }
}