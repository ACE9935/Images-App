import { getStorage, ref, uploadBytesResumable, getDownloadURL } from "firebase/storage";

const uploadImageToStore = async (file: File): Promise<string> => {
  try {
    const storage = getStorage();
    const storageRef = ref(storage, `images/${file.name}`);
    const uploadTask = uploadBytesResumable(storageRef, file);

    return await new Promise((resolve, reject) => {
      uploadTask.on(
        "state_changed",
        (snapshot) => {
          const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
          console.log(`Upload is ${progress}% done`);
        },
        (error) => {
          console.error("Upload failed:", error);
          reject(error);
        },
        async () => {
          try {
            const downloadURL = await getDownloadURL(uploadTask.snapshot.ref);
            resolve(downloadURL);
          } catch (error) {
            console.error("Error getting download URL:", error);
            reject(error);
          }
        }
      );
    });
  } catch (error) {
    console.error("Unexpected error in uploadImageToStore:", error);
    throw error;
  }
};

export default uploadImageToStore;