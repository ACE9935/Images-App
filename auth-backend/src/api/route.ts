import express from "express"
import SignInController from "./signin.controller"


const router = express.Router()

router.route("/send-verification-email").post(SignInController.sendVerificationEmail)
router.route("/set-password").post(SignInController.setPassword)
router.route("/send-password-reset-link").post(SignInController.sendPasswordResetLink)
router.route("/verify-user").get(SignInController.verifyUser)
router.route("/verify-jwt-token").post(SignInController.verifyJwtToken)
router.route("/generate-verification-token").post(SignInController.generateVerificationToken)

export default router