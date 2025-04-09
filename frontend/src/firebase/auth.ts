
import { auth, } from "./firebase";
import {
  signInWithPopup,
  GoogleAuthProvider,
  updateProfile,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "firebase/auth";

import { configurations } from "../app-configuration";
import AuthServices from "../auth-services/services";
import { addUser } from "./addUser";
import type { User } from "../types/UserInterface";
import type { SignUpResponse } from "../types/SignUpResponse";
import type { LoginResponse } from "../types/LoginResponse";
import type { SignUpErrors } from "../types/SignUpErrors";
import type { RawUser } from "../types/RawUser";
import { getUserById } from "./getUserById";
import { updateUserField } from "./updateUserField";
import { getUserByEmail } from "./getUserByEmail";
import type { PwdResetResponse } from "../types/PwdResetResponse";
import type { PwdResetFormResponse } from "../types/PwdResetFormResponse";

export const doSignInWithEmailAndPassword = async (email:string, password:string):Promise<LoginResponse> => {
  let unverifiedUser:User|null=null,verificationAccessToken
  try {
    const result = await signInWithEmailAndPassword(auth, email, password)
    if (!result.user.emailVerified){

      unverifiedUser=await getUserById(result.user.uid)!
      await auth?.currentUser?.getIdToken()  // Pass 'true' if you want to force refresh the token
      .then((token) => {
        verificationAccessToken=token
        console.log(verificationAccessToken)
      })
      .catch((error) => {
        console.error("Error getting token:", error);
      });
      await AuthServices.verifyJwtToken(unverifiedUser?.verificationToken!)

      return {errorMsg: "Please verify your email to log in", status: "ERROR"}
    }
    return {errorMsg:null,status:"OK"}
  } catch (error:any) {

    if (error.code === 'auth/invalid-email') {
      // Handle the case where the email is invalid
      console.error('Invalid email:', error);
      return { errorMsg: "Please provide a valid email address", status: "ERROR" };
    }
    if (error.code === 'auth/invalid-credential' || error.code === 'auth/missing-password') {
      // Handle the case where the user is unfound
      console.error('Invalid credentials:', error);
      return { errorMsg: "User not found. Please check your email and password", status: "ERROR" };
    } else {
      // Handle other types of errors
      console.error('Error login:', error);
      // Set a generic error message or handle it based on your requirement
    }

    if(error.status==510){
      
      AuthServices.generateVerificationToken(unverifiedUser?.email!)
            .then(async response => {
              await updateUserField("id",unverifiedUser?.id!,"verificationToken",response.data.data)
              await AuthServices.sendVerificationEmail(
                unverifiedUser?.email!,
                response.data.data,
                unverifiedUser?.id!,
                verificationAccessToken!);
            })
            .catch(e => {
              console.log(e);
         });  
    
      return {errorMsg: "A link was sent to your email to complete your verification", status: "ERROR"}
    }

    return { errorMsg: 'An error occurred, please try again later', status: "ERROR" };
  }
};

export const doCreateUserWithEmailAndPassword = async ({userName,email, pwd,rePwd,acceptPlcs}:RawUser):Promise<SignUpResponse> => {
  const errors:SignUpErrors={error:{userName:"",email:"",pwd:"",rePwd:"",acceptPlcs:""}}
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!userName){
    errors.error.userName = "A name is required";
    return {errors:errors,status:"ERROR"}
  }
  
  // Check if email is provided and is valid
  if (!email) {
    errors.error.email = "An email address is required";
    return {errors:errors,status:"ERROR"}
  } else if (!emailRegex.test(email)) {
    errors.error.email = "Please provide a valid email address";
    return {errors:errors,status:"ERROR"}
  }

  // Check if password is provided and meets criteria
  if (!pwd) {
    errors.error.pwd = "A password is required";
    return {errors:errors,status:"ERROR"}
  } else if (pwd.length < 6) {
    errors.error.pwd = "The password must be at least 6 characters long";
    return {errors:errors,status:"ERROR"}
  }

  // Check if re-entered password matches the original password
  if (pwd !== rePwd) {
    errors.error.rePwd = "Passwords do not match";
    return {errors:errors,status:"ERROR"}
  }

  // Check if phone number is provided and is valid
  /*if (!phone) {
    errors.error.phone = "A phone number is required";
    return {errors:errors,status:"ERROR"}
  } else if (!validator.isMobilePhone(phone,false)) {
    errors.error.phone = "Please provide a valid phone number";
    return {errors:errors,status:"ERROR"}
  }*/

  // Check if terms and conditions are accepted
  if (!acceptPlcs) {
    errors.error.acceptPlcs = "Please check this box";
    return {errors:errors,status:"ERROR"}
  }

  try {
    await createUserWithEmailAndPassword(auth, email, pwd);

    // Update user profile with name
    if (auth.currentUser) {
      const data:User={userName,email,acceptPlcs,id:auth.currentUser.uid,emailVerified:false,photoUrl:configurations.userDefaultPic,favImages:[],albums:[],uploadedImages:[], joinDate:(new Date()), viewedImages:[]}
      await updateProfile(auth?.currentUser, { displayName: userName,photoURL:data.photoUrl })
            .then(async ()=>{
              const newUser=await addUser(data,"Email")
              const accessToken=await auth?.currentUser?.getIdToken()
              if(auth.currentUser) await AuthServices.sendVerificationEmail(
                newUser?.email!,
                newUser?.verificationToken!,
                newUser?.id!,
                accessToken!);
            })
    }

    // Return success status if everything is successful
    return { errors: null, status: "OK",method:"credentials" };
  } catch (error:any) {

      // Firebase authentication error
      if (error.code === 'auth/email-already-in-use') {
        // Handle the case where the email is already in use
        errors.error.email = "The email address is already associated with another account";
        return { errors: errors, status: "ERROR" };
      } else {
        // Handle other types of errors
        console.error('Error creating account:', error);
        // Set a generic error message or handle it based on your requirement
      }

    return { errors: { error: { ...errors.error, email: "An error occurred while creating the account." } }, status: "ERROR" };
  }
};

// Function to sign in with Google
export const doSignInWithGoogle = async ():Promise<SignUpResponse> => {
  
  try{
    const provider = new GoogleAuthProvider();
    const result = await signInWithPopup(auth, provider);
    const user = result.user;
    const data:User={userName: user.displayName!, email: user.email!, emailVerified: user.emailVerified, id: user.uid, photoUrl: user.photoURL!,acceptPlcs:false, favImages:[],albums:[],uploadedImages:[],joinDate:(new Date()), viewedImages:[]}
    await addUser(data, "Google")
    return { errors: null, status: "OK", method: "google" };  
  }catch(e){
    console.log(e)
    return { errors: "An error has occured", status: "ERROR", method: "google" }; 
  }    
};

// Function to sign out
export const doSignOut = () => {
  return auth.signOut();
};

export const resetPassword = async (email:string): Promise<PwdResetResponse> => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  try {
    if (!emailRegex.test(email)) return { errorMsg: 'Please provide a valid email address', status: "ERROR" };

    const user = await getUserByEmail(email);
    if (!user) return { errorMsg: 'User not found. Please check your email', status: "ERROR" };

    await AuthServices.sendPasswordResetLink(email);

    // Return success status if everything is successful
    return { errorMsg: null, status: "OK" };
  } catch (error) {
    console.error("Error resetting password:", error);
    return { errorMsg: "An error occurred, please try again later", status: "ERROR" };
  }
}

export const updatePassword = async (pwd:string,rePwd:string,token:string,id:string): Promise<PwdResetFormResponse> => {
  const errors={pwd:"",rePwd:""};
  try {
    if (!pwd) {
      errors.pwd = "A password is required";
      return {errors:errors,status:"ERROR"}
    } else if (pwd.length < 6) {
      errors.pwd = "The password must be at least 6 characters long";
      return {errors:errors,status:"ERROR"}
    }
  
    // Check if re-entered password matches the original password
    if (pwd !== rePwd) {
      errors.rePwd = "Passwords do not match";
      return {errors:errors,status:"ERROR"}
    }

    await AuthServices.setPassword(
      pwd,
      token,
      id
    );

    // Return success status if everything is successful
    return { errors: null, status: "OK" };
  } catch (error) {
    console.error("Error resetting password:", error);
    return { errors: {pwd: "An error occurred, please try again later", rePwd: ""}, status: "ERROR" };
  }
}