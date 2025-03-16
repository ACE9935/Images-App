# PDL - Recherche d'Images par Similarité

## Documentation d'Installation et de Test

### Systèmes d'Exploitation Testés
- **Serveur** : Ubuntu 20.04

### Navigateurs Web Testés
- **Client** : Firefox (version 128.7.0)

---

## Compilation et Exécution

### Backend

1. **Prérequis**  
   - Java 17 ou supérieur
   - Maven 3.6+
   - PostgreSQL (configuration dans `application.properties`)

2. **Clonage du Projet**  
   Cloner le projet avec :
   ```bash
   git clone https://gitlab.emi.u-bordeaux.fr/pdl-l3/teams/2025/l1/l1b.git
   cd l1b/backend
   ```

3. **Configuration**  
   S'assurer que le fichier `src/main/resources/application.properties` contient les bonnes informations de connexion à la base de données PostgreSQL.

4. **Compilation et Lancement**  
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   Cela compile et démarre le serveur.

5. **Exécution des Tests**  
   Pour lancer les tests unitaires :
   ```bash
   mvn test
   ```

### Frontend

1. **Prérequis**  
   - Node.js 16+
   - npm 8+

2. **Installation des Dépendances**  
   ```bash
   cd ../frontend
   npm install
   ```

3. **Lancement du Client**  
   ```bash
   npm run dev
   ```

4. **Exécution des Tests**  
   ```bash
   npm run test
   ```

---

## API REST - Points d'Entrée Principaux

### Récupération de la Liste des Images
- **Méthode** : `GET /images`
- **Réponse** : JSON contenant les métadonnées des images indexées

### Ajout d'une Image
- **Méthode** : `POST /images`
- **Données** : Image envoyée en `multipart/form-data`
- **Réponse** : `201 Created` si l'ajout est réussi

### Suppression d'une Image
- **Méthode** : `DELETE /images/{id}`
- **Réponse** : `200 OK` si la suppression est réussie

### Recherche d'Images Similaires
- **Méthode** : `GET /images/id/similar?number=N&descriptor=DESCR`
- **Données** : Identifiant de l'image
- **Réponse** : JSON contenant les images les plus similaires et  leurs scores de similarité


#### Paramètres de requête

Ce service accepte les paramètres suivants pour affiner la recherche :

- **`number` (int, optionnel)** : Nombre d'images similaires à retourner (par défaut : `5`).  
- **`descriptor` (string, requis)** : Type de descripteur utilisé pour la comparaison (`histogram_2d`, `histogram_3d`, `histogram_of_visual_words`).  

#### Lorsqu'on utilise `histogram_of_visual_words`, la méthode repose sur un **modèle de sac de mots visuels (BoVW)** basé sur **k-means**. Voici les étapes principales :

- **Extraction des caractéristiques locales** : Descripteurs importants sont extraits des images.
- **Clustering avec k-means** : Ces descripteurs sont regroupés en `K` clusters représentant un vocabulaire visuel et stocké dans `/resources/visual_dictionary.dat`.
- **Construction de l’histogramme** : Chaque image est représentée par un histogramme indiquant la fréquence des descripteurs dans chaque cluster.
- **Comparaison des images** : La similarité entre images est calculée la distance euclidienne entre ces histogrammes.

---

## Clonage du Repository

### Via HTTPS
```bash
git clone https://gitlab.emi.u-bordeaux.fr/pdl-l3/teams/2025/l1/l1b.git
```

### Via SSH
```bash
git clone git@gitlab.emi.u-bordeaux.fr:pdl-l3/teams/2025/l1/l1b.git
```

---


