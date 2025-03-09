import axios from "axios";

const apiClient = axios.create({
    baseURL: "/images",
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

// Fetch a single image by ID
export const fetchImageById = async (id: string) => {
    const response = await apiClient.get(`/visualize/${id}`, { responseType: "blob" });
    return response.data;
};

// Delete an image by ID
export const deleteImageById = async (id: string) => {
    await apiClient.delete(`/${id}`);
};

// Fetch image metadata by ID
export const fetchImageMetaDataById = async (id: string) => {
    const response = await apiClient.get(`/${id}`);
    return response.data;
};

// Upload an image to server
export const uploadImage = async (file: File) => {
    let formData = new FormData();
    formData.append('file', file);
  
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