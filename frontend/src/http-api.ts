import axios from "axios";
import uploadImageToStore from "./firebase/uploadImageToStore";
import type { CIFAR10Classes } from "./types/cifar10";
import type { ImageMetaData } from "./types/ImageMetaData";

const apiClient = axios.create({
    baseURL: "https://images-app-production.up.railway.app/images",
});

// Fetch the list of images
export const fetchImages = async () => {
    const response = await apiClient.get("");
    return response.data;
};

export const fetchSimilarImages = async (id: string, number: string, descr:string) => {
  const response = await apiClient.get(`/${id}/similar?number=${number}&descr=${descr}`);
  return response.data;
};

export async function searchImages(searchTerm: string) {
  try {
    const response = await apiClient.get("");
    const allImages: ImageMetaData[] = response.data;

    const lowerSearchTerm = searchTerm.toLowerCase();

    const matchingImages = allImages.filter(image => {
      const titleMatch = image.title.toLowerCase().includes(lowerSearchTerm);
      const classMatch = image.imgClass?.toLowerCase().includes(lowerSearchTerm);
      return titleMatch || classMatch;
    });

    return matchingImages;
  } catch (error) {
    console.error("Error searching images by title or class:", error);
    throw error;
  }
}

export const fetchImagesOfClass = async (imgClass: CIFAR10Classes) => {
  const response = await apiClient.get(`/class/${imgClass}`);
  return response.data;
};

// Fetch a single image by ID
export const fetchImageById = async (id: string) => {
    const response = await apiClient.get(`/visualize/${id}`, { responseType: "blob" });
    return response.data;
};

// Delete an image by ID
export const deleteImageById = async (id: string) => {
    await apiClient.delete(`/${id}`);
};

export const incrementLikes = async (id: string) => {
  await apiClient.put(`/${id}/like`)
};

export const decrementLikes = async (id: string) => {
  await apiClient.delete(`/${id}/like`)
};

export const incrementDownload = async (id: string) => {
  await apiClient.put(`/${id}/increment-download`)
};

// Fetch image metadata by ID
export const fetchImageMetaDataById = async (id: string) => {
    const response = await apiClient.get(`/${id}`);
    return response.data;
};

export const updateImageTitle = async (id: string, newTitle:string) => {
  let formData = new FormData();
  formData.append('title', newTitle);
  await apiClient.put(`/${id}/title`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// Upload an image to server
export const uploadImage = async (file: File, userId: string, title: string) => {
    let formData = new FormData();
    formData.append('file', file);
    const imgUrl = await uploadImageToStore(file)
    console.log(imgUrl)
    formData.append('author', userId);
    formData.append('title', title);
    formData.append('imgUrl', imgUrl);
  
    try {
      const response = await apiClient.post('', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      console.log('SUCCESSFULY UPLOADED THE IMAGE');
      return response.data;
    } catch (error) {
      console.error('FAILURE!! CHECK YOUR IMAGE AGAIN', error);
      throw new Error('Failed to upload image');
    }
  };
  

export default apiClient;