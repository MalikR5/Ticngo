# 🎭 Tic'n Go — JavaFX

Back-office billetterie en JavaFX + Spring Boot + MySQL

## Prérequis

- Java 21+
- Maven 3.9+
- MySQL 8.0+ (en cours d'exécution)

## Configuration

Modifier `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ticngo?createDatabaseIfNotExist=true&...
spring.datasource.username=root
spring.datasource.password=TON_MOT_DE_PASSE
```

## Lancement

```bash
mvn javafx:run
```

La base de données est créée automatiquement au démarrage.

## Connexion par défaut

| Email                | Mot de passe |
|----------------------|--------------|
| admin@ticngo.fr      | Admin1234!   |

## Fonctionnalités

- **Tableau de bord** : statistiques globales (billets, spectacles, clients, revenus)
- **Billets** : création, annulation, validation, export PDF individuel ou liste
- **Spectacles** : CRUD complet avec gestion du lieu et de la catégorie
- **Clients** : CRUD complet avec recherche
- **Authentification** : BCrypt, session locale

## Structure du projet

```
src/main/java/fr/ticngo/
├── TicnGoApplication.java       # Point d'entrée JavaFX + Spring Boot
├── StageReadyEvent.java
├── StageInitializer.java
├── model/                       # Entités JPA
├── repository/                  # Repositories Spring Data
├── service/                     # Logique métier
├── controller/                  # Contrôleurs JavaFX (FXML)
├── config/                      # DataInitializer (données démo)
└── util/                        # FxmlLoader, NavigationService, SessionManager

src/main/resources/
├── fxml/                        # Vues FXML
├── css/style.css                # Thème dark sidebar
└── application.properties
```

## Tech stack

| Technologie           | Version   |
|-----------------------|-----------|
| Java                  | 21        |
| JavaFX                | 21.0.2    |
| Spring Boot           | 3.2.0     |
| Spring Data JPA       | 3.2.0     |
| MySQL Connector       | 8.x       |
| iText 8               | 8.0.3     |
| Lombok                | latest    |
