import type { RawUser } from "./RawUser";

export interface SignUpErrors {
    error: Omit<RawUser, "acceptPlcs"> & { acceptPlcs: string }
}