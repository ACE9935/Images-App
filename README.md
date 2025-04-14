# 📄 Images App

### 🔗 [💻 Live Demo](https://images-app-production.up.railway.app/)

## 🛠 Installation and Testing Guide

### ✅ Tested Operating Systems
- **Server**: Ubuntu 20.04

### ✅ Tested Web Browsers
- **Client**: Firefox (version 128.7.0)

---

## ⚙️ Compilation and Execution

### 🔧 Backend

1. **Prerequisites**  
   - Java 17 or higher  
   - Maven 3.6+  
   - PostgreSQL (configured in `application.properties`)

2. **Cloning the Project**  
   ```bash
   git https://gitlab.emi.u-bordeaux.fr/pdl-l3/teams/2025/l1/l1b.git
   cd l1b/backend
   ```

3. **Configuration**  
   Ensure the file `src/main/resources/application.properties` contains the correct PostgreSQL connection info.

4. **Build and Launch**  
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Running Tests**  
   ```bash
   mvn test
   ```

---

### 💻 Frontend

1. **Prerequisites**  
   - Node.js 16+  
   - npm 8+

2. **Install Dependencies**  
   ```bash
   cd ../frontend
   npm install
   ```

3. **Launch Client**  
   ```bash
   npm run dev
   ```

4. **Run Tests**  
   ```bash
   npm run test
   ```

---

## 🌐 REST API - Main Endpoints

### Get Image List
- **Method**: `GET /images`
- **Response**: JSON containing metadata of indexed images

### Add Image
- **Method**: `POST /images`
- **Payload**: Image sent as `multipart/form-data`
- **Response**: `201 Created` if successful

### Delete Image
- **Method**: `DELETE /images/{id}`
- **Response**: `200 OK` if successfully deleted

### Search for Similar Images
- **Method**: `GET /images/id/similar?number=N&descriptor=DESCR`

#### Query Parameters
- `number` (int, optional): Number of similar images to return (default: `5`)  
- `descriptor` (string, required): Descriptor type used (`histogram_2d`, `histogram_3d`, `histogram_of_visual_words`)

#### Response
- JSON with similar images and similarity scores

When using `histogram_of_visual_words`, the method uses a **Bag of Visual Words (BoVW)** model based on **k-means**:

- **Feature Extraction**: Local descriptors are extracted from images.
- **Clustering (k-means)**: Descriptors are clustered into `K` groups forming a visual vocabulary, stored in `/resources/visual_dictionary.dat`.
- **Histogram Construction**: Each image is represented by a histogram indicating descriptor frequency in each cluster.
- **Similarity Computation**: Based on Euclidean distance between histograms.

---

## 🧠 Image Classification with CIFAR-10 Labels

Each image stored in the database is automatically **classified using a CNN-based AI model** built with **TensorFlow**. This model is trained on the **CIFAR-10** dataset.

- **Predicted Labels**: `airplane`, `automobile`, `bird`, `cat`, `deer`, `dog`, `frog`, `horse`, `ship`, `truck`
- **Pipeline**:
  - Image is passed to the AI model upon upload
  - Model predicts its class using convolutional layers
  - Result is stored in the database for future use (e.g. filtering, analytics)

---

## 🔐 User Authentication and Account Management

Authentication is handled via **Firebase Authentication**, providing secure and scalable login options.

### Sign-In Options
- **Email and password**
- **Google account sign-in**

### Account Features
Once a user is authenticated and their **email is verified**, they gain access to the following features:

- Upload images to the backend
- Own a **public profile** showcasing all uploaded images
- **Download images** (any public image can be downloaded by any user)
- **Delete their own uploaded images** at any time
- **Save images as favorites**
- **Create albums** to organize images
- **Track view history** of visited images

### Account Deletion

- Users can **safely delete their account** at any time
- Deleting an account **does not delete the uploaded images**, which remain publicly available unless deleted manually by the user before account deletion

---

## 📁 Repository Cloning

### Via HTTPS
```bash
git clone https://gitlab.emi.u-bordeaux.fr/pdl-l3/teams/2025/l1/l1b.git
```



