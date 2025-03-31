export interface User {
    id:string
    favImages:[],
    albums:[],
    uploadedImages:[],
    verificationToken?:string,
    resetPasswordToken?:string,
    photoUrl?:string
    emailVerified:boolean
    userName:string
    acceptPlcs:boolean
    email:string
    joinDate:Date
}