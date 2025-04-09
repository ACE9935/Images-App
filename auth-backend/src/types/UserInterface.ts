export interface User {
    id:string
    favImages:string[],
    albums:{images:string[],name:string, id:number}[],
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