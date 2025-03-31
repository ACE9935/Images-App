// backend/index.js
const express = require('express');
const path = require('path');
const cors = require('cors');
import { Request, Response } from 'express';
import { customInitApp } from './firebase/firebase-admin';
import router from './api/route';
import 'dotenv/config';

const PORT = process.env.PORT || 5000;

// Serve the React app’s build folder
const app = express()

app.use(cors())
app.use(express.json())
customInitApp()

// API routes can be defined here
app.use("/api", router)
app.get("/", (req:Request, res:Response) => res.send("Images App is welcoming users!"));
app.use("*", (req:Request, res:Response) => res.status(404).json({ error: "not found"}))

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

