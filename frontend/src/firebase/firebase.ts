import { configurations } from "../app-configuration";
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
    apiKey: "AIzaSyAFVjPJh_AcP_YVfi5JbqvYqx9D422Sl3c",
    authDomain: "recipes-app-d9a48.firebaseapp.com",
    projectId: "recipes-app-d9a48",
    storageBucket: "recipes-app-d9a48.firebasestorage.app",
    messagingSenderId: "782441545728",
    appId: "1:782441545728:web:a37251435387ca2632d8e5",
    measurementId: "G-1Q1EMJX4SB"
  };

export const actionCodeSettings = {
  url: `${configurations.host}/?verified=true`, // URL where the link will redirect to after email verification
  handleCodeInApp: true, // This must be true for redirects to work on mobile devices// Optional, if you're using Firebase Dynamic Links
};
// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth=getAuth(app)
const db = getFirestore(app)
const storage=getStorage(app)

export {app,auth,db,storage}