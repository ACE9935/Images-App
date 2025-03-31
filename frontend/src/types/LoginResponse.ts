export interface LoginResponse {
    // Indicates the errors encountered during the login attempt.
    errorMsg: "Please check your email to log in" | "An error occurred, please try again later" | "User not found. Please check your email and password" | "Please provide a valid email address" | string | null;
    // Indicates the response status.
    status: "ERROR" | "OK" | null;
}