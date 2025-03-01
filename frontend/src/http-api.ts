import axios from "axios";

const apiClient = axios.create({
    baseURL: "/images",
});

// Fetch the list of images
export const fetchImages = async () => {
    const response = await apiClient.get("");
    return response.data;
};

// Fetch a single image by ID
export const fetchImageById = async (id: string) => {
    const response = await apiClient.get(`/visualize/${id}`, { responseType: "blob" });
    return response.data;
};

export const uploadImage = async (file: File) => {

    let formData = new FormData();

    /*
        Add the form data we need to submit
    */
    formData.append('file', file);

    await apiClient.post('',
        formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
    ).then(function () {
        console.log('SUCCESS!!');
    })
        .catch(function () {
            console.log('FAILURE!!');
        });

}

export default apiClient;