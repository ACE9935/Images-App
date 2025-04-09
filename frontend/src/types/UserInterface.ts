import type { Album } from "./Album"

export interface User {
    id:string
    favImages:string[],
    albums:Album[],
    viewedImages:{imgId:string,date:Date}[],
    uploadedImages:string[],
    verificationToken?:string,
    resetPasswordToken?:string,
    photoUrl?:string
    emailVerified:boolean
    userName:string
    acceptPlcs:boolean
    email:string
    joinDate:Date
}