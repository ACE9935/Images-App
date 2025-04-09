import { ref, getBlob } from "firebase/storage";
import { storage } from "../firebase/firebase";

export default async function downloadImage(filePath: string) {
  try {
    // Create a reference to the file
    const fileRef = ref(storage, "images/"+filePath);
    
    // Get the blob directly from Firebase Storage
    const blob = await getBlob(fileRef);
    
    // Create an object URL for the blob
    const blobUrl = URL.createObjectURL(blob);
    
    // Create an anchor element and trigger the download
    const link = document.createElement('a');
    link.href = blobUrl;
    link.download = filePath.split('/').pop() || 'image';
    document.body.appendChild(link);
    link.click();
    
    // Clean up
    document.body.removeChild(link);
    setTimeout(() => URL.revokeObjectURL(blobUrl), 100);
    
    console.log("Download initiated!");
  } catch (error) {
    console.error("Error downloading file:", error);
  }
}