export interface PwdResetFormResponse {
    // Indique les erreurs rencontrées lors de la tentative de connexion.
    errors: {pwd:string,rePwd:string} | null;
    // Indique le statut de la réponse.
    status: "ERROR" | "OK" | null;
 }