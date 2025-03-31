import http from "./http-common";

class AuthServices {

  sendVerificationEmail(email:string,verificationToken:string,id:string,accessToken:string) {
    return http.post(`/send-verification-email`,{email,verificationToken,id,accessToken});
  }
  generateVerificationToken(email:string){
    return http.post('/generate-verification-token', {
    email
   });
  }
  verifyJwtToken(token:string){
    return http.post('/verify-jwt-token', {
    token
   });
  }
  sendPasswordResetLink(email:string){
    return http.post('/send-password-reset-link', {
    email
   });
  }
  setPassword(password:string,token:string,id:string){
    return http.post('/set-password', {
    password,token,id
   });
  }
}

export default new AuthServices();