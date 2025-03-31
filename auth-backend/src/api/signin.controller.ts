import { NextFunction, Request, Response } from "express"
import { auth } from "firebase-admin";
import { sendVerificationRequest } from "../utils/sendVerificationRequest";
import { generateVerificationToken } from "../utils/generateVerificationToken";
import { firestore } from "firebase-admin";
import { FieldValue } from 'firebase-admin/firestore';
import jwt from 'jsonwebtoken'
import { sendPasswordResetRequest } from "../utils/sendPasswordResetRequest";
import { getUserById } from "../firebase/getUserById";

export default class SignInController {

  static async sendVerificationEmail(req:Request, res:Response, next:NextFunction) {

    try {
      const {email,verificationToken,id,accessToken} = req.body
      const decodedToken = await auth().verifyIdToken(accessToken);
      await sendVerificationRequest({identifier:email!,id:id!,token:verificationToken!})

      res.json({ status: "success",message:"Verification email sent" })
    } catch (e:any) {
      res.status(500).json({ error: e })
    }
  }

  static async generateVerificationToken(req:Request, res:Response, next:NextFunction) {

    try {
      const {email} = req.body
      const token=generateVerificationToken(email!)

      res.json({ status: "success",data:token })
    } catch (e:any) {
      res.status(500).json({ error: e })
    }
  }

  static async verifyUser(req:Request, res:Response, next:NextFunction) {

    const myURL = new URL(req.url, `http://${req.headers.host}`);
    const token = myURL.searchParams.get('token');
    const id = myURL.searchParams.get('id');
    if (!token || !id) {
    throw new Error("Token or ID is missing in the request URL");
    }

    try {
     
     const decoded:any = jwt.verify(token!, process.env.JWT_SECRET!);
     const user=await auth().getUserByEmail(decoded.email)
    
     if(user){
        if(user.emailVerified==true) throw "email already verified"
        await auth().updateUser(user.uid, {emailVerified:true})
        const db = firestore();
       const querySnapshot = await db.collection("users").where("id", "==", id).get();
    
    // Iterate over the query results and update documents
     querySnapshot.forEach(async (doc) => {
      const docRef = db.collection("users").doc(doc.id);
      await docRef.update({
        emailVerified: true,
        verificationToken:FieldValue.delete(),
      });
     });
    }
      res.redirect(process.env.CLIENT_HOST+"/?verified-user=true")
    } catch (e:any) {
      res.status(500).json({ error: e })
    }
  }
  
  static async verifyJwtToken(req:Request, res:Response, next:NextFunction) {

    try {
     
      const {token} = req.body
      
      const decodedToken:any = jwt.verify(token!, process.env.JWT_SECRET!);
      res.json({ status: "success",data:decodedToken })
    } catch (e:any) {
      res.status(510).json({ error: e })
    }
  }

  static async sendPasswordResetLink(req:Request, res:Response, next:NextFunction) {

    try {
     
      const {email} = req.body
      const token=generateVerificationToken(email!)
      const db = firestore();
      const querySnapshot = await db.collection("users").where("email", "==", email).get();
    
      if (!querySnapshot.empty) {
        // Since the user is unique, we can assume there's only one document
        const doc = querySnapshot.docs[0];
        const docRef = db.collection("users").doc(doc.id);
    
        // Update the document
        await docRef.update({
            resetPasswordToken: token,
        });
    
        // Retrieve the updated document data
        const updatedDoc = await docRef.get();
        const updatedUser = { id: updatedDoc.id, ...updatedDoc.data()};

        if(updatedUser) await sendPasswordResetRequest({identifier:email,id:updatedUser.id,token})
      }
      res.json({ status: "success"})
    } catch (e:any) {
      res.status(500).json({ error: e })
    }
  }

  static async setPassword(req:Request, res:Response, next:NextFunction) {

    try {
     
      const {password,token,id} = req.body
      if (!token || !id) {
        throw new Error("Token or ID is missing in the request URL");
      }
    
      const decoded:any = jwt.verify(token!, process.env.JWT_SECRET!);
      const user=await auth().getUserByEmail(decoded.email)
      const userDocument=await getUserById(user.uid)

      if(userDocument?.resetPasswordToken!=token) throw new Error("Invalid Token");
    
      if(user){
      await auth().updateUser(user.uid, {password})
      const db = firestore();
      const querySnapshot = await db.collection("users").where("id", "==", id).get();
  
  // Iterate over the query results and update documents
      querySnapshot.forEach(async (doc) => {
      const docRef = db.collection("users").doc(doc.id);
       await docRef.update({
        emailVerified: true,
        resetPasswordToken:FieldValue.delete(),
    });
    });
    } else throw new Error("unfound user");
      res.json({ status: "success"})
    } catch (e:any) {
      res.status(500).json({ error: e })
    }
  }

}