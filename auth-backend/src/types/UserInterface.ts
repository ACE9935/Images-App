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
    email:string
    acceptPlcs:boolean
}