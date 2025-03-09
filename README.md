# PDL - Recherche d'Images par Similarité

## Introduction

Ce projet est réalisé dans le cadre de la formation L3 Informatique à l'Université de Bordeaux. L'objectif est de développer une application de recherche d'images par similarité reposant sur une architecture client-serveur. Le serveur indexe et traite les images stockées dans un dossier spécifique, tandis que le client permet d'interagir via une interface web.

## Exigences du Projet

### Noyau Commun (Backend)
- **Langage et Outils** :
  - Java (JDK 17)
  - Maven
  - Spring Boot 3.2.x
- **Bibliothèques et Extensions** :
  - BoofCV 1.1.2 pour le traitement d'images
  - PostgreSQL 42.7.1 avec extension **pgvector** pour l'indexation dans la base de données
  - Spring JDBC pour l'interfaçage avec la base de données
- **Fonctionnalités** :
  1. **Initialisation du Serveur** : Chargement et indexation des images présentes dans le dossier `images`.
  2. **Gestion des Images** : Accès, ajout, suppression des images, et fourniture des métadonnées (ID, nom, taille, format).
  3. **Indexation des Images** : Calcul des descripteurs (histogramme 2D Teinte/Saturation, histogramme 3D RGB et histogramme des visual words) et stockage dans la base.
  4. **Recherche par Similarité** : Retour des N images les plus similaires à une image donnée, selon le descripteur spécifié.

### Communication (API REST)
- **GET /images** : Récupération de la liste des images (format JSON).
- **POST /images** : Ajout d'une image via une requête multipart.
- **GET /images/{id}** : Récupération d'une image par son identifiant.
- **DELETE /images/{id}** : Suppression d'une image.
- **GET /images/{id}/similar?number=N&descriptor=DESCR** : Recherche d'images similaires.

### Client
- **Technologies** : TypeScript et Vue.js 3.x.
- **Fonctionnalités** :
  1. Visualiser les images disponibles sur le serveur sous forme de galerie ou carrousel.
  2. Afficher les métadonnées et les images similaires lors de la sélection d'une image.
  3. Enregistrer, ajouter et supprimer des images via l'interface.
- **Compatibilité** :
  - Serveur testé sur : Windows (≥ 10), Ubuntu (≥ 20.04), Debian Bookworm, ou MacOS (≥ 11).
  - Client testé sur : Safari, Google Chrome, ou Firefox.
- **Documentation** : Ce README décrit les étapes d'installation et d'exécution ainsi que la structure du projet.

## Arborescence du Projet

