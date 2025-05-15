# 💻 InnoMall — Application Bureau (Java 17 + JavaFX)

## 🧭 Présentation Générale

**InnoMall JavaFX** est une application desktop complète et intelligente permettant la gestion d’un centre commercial, avec un back-end en Java 17, UI en JavaFX, et intégration de technologies modernes comme Stripe, Twilio, QR, PDF, et IA (Gemini).

---

## 🧩 Fonctionnalités Clés

### 👥 Connexion & Profils
- Connexion multi-rôle : `Client`, `Commerçant`, `Admin`
- Sécurité avec **ReCAPTCHA** + connexion Google OAuth
- Modification du profil

---

### 🛒 Module Produits
- Commerçants : CRUD de produits, horaires, promos
- Clients :
  - Ajout au **panier**
  - Paiement sécurisé via **Stripe**
  - Réception de **mail + reçu PDF**

---

### ⭐ Fidélité & Feedback
- Attribution automatique de **points**
- Notation des boutiques (★)
- Feedback modifiable/supprimable

---

### 🗨️ Postes & Commentaires
- Création de posts par les clients
- Interaction via commentaires / sous-commentaires
- Points attribués selon les interactions

---

### 📅 Gestion d'Événements
- Commerçants créent des événements
- Clients participent et génèrent des **QR Codes**
- Export du pass événement

---

### 🅿️ Réservation de Parking
- Réservation intuitive via interface graphique
- Confirmations instantanées par Email et SMS (Twilio)
- Monitoring temps réel avec capteurs IoT
- Services additionnels : lavage auto (3 options)
- Fonction "Find My Car" pour localisation
- Gestion automatique des expirations

---

### 📢 Réclamations
- Clients déposent une réclamation
- Admins les traitent via back-office

---

### 🤖 Chatbot Gemini (IA)
- Intégré dans la section gauche de l’interface JavaFX
- Propulsé par **l’API Gemini de Google**
- Répond aux requêtes des clients (navigation, aide, infos)
- Interface fluide en JavaFX avec animation et transition

---

### 🧑‍💼 Tableau de Bord Admin
- Accès aux données des utilisateurs
- Statistiques dynamiques
- Suivi des parkings, événements et réclamations

---

## ⚙️ Stack Technique

- Java 17
- JavaFX (UI graphique)
- MySQL 8 (JDBC)
- Stripe SDK (paiement)
- Twilio SDK (SMS)
- Gemini API (Chatbot IA)
- ZXing (QR Code)
- JavaMail + iText (PDF/email)
- Maven

---

## 📁 Structure du Projet

```bash
innomall-java/
├── gui/                 # Interfaces JavaFX (FXML + CSS)
├── services/            # Logique métier (produits, users, events, etc.)
├── models/              # Entités Java
├── database/            # Connexion JDBC
├── utils/               # QR, mail, PDF, chatbot
├── Main.java            # Point d’entrée

