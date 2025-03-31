export interface PwdResetResponse {
    // Indicates the errors encountered during the password reset attempt.
    errorMsg: "An error occurred, please try again later" | "User not found. Please check your email" | "Please provide a valid email address" | null;
    // Indicates the response status.
    status: "ERROR" | "OK" | null;
}