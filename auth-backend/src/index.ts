const express = require('express');
const path = require('path');
const cors = require('cors');
import { Request, Response } from 'express';
import { customInitApp } from './firebase/firebase-admin';
import router from './api/route';
import 'dotenv/config';

const PORT = process.env.PORT || 5000;

const app = express();

// CORS configuration
const corsOptions = {
  origin: process.env.CLIENT_HOST, // Allow only your frontend
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization'],
};

app.use(cors(corsOptions));  // Use the CORS options for all routes
app.use(express.json());
customInitApp();

// API routes
app.use("/api", router);
app.get("/", (req:Request, res:Response) => res.send("Images App is welcoming users!"));
app.use("*", (req:Request, res:Response) => res.status(404).json({ error: "not found"}));

// Handle preflight requests
app.options('*', cors(corsOptions));

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
