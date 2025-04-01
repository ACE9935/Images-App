import { configurations } from "../app-configuration";
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyCqBGiU6pklEiOL84qLIOu3dXrKg7dKJhE",
  authDomain: "images-app-6bbe5.firebaseapp.com",
  projectId: "images-app-6bbe5",
  storageBucket: "images-app-6bbe5.firebasestorage.app",
  messagingSenderId: "440600748418",
  appId: "1:440600748418:web:34b49c1f042e07c7f1ef9b"
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