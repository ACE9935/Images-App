import type { SignUpErrors } from "./SignUpErrors"


export interface SignUpResponse {
    method?:"credentials" | "google"
    errors:SignUpErrors | null | string
    status:"ERROR" | "OK" | null
 }