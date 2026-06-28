# 🎭 Tic'n Go — JavaFX

Back-office billetterie en JavaFX + JDBC + MySQL

## Prérequis

- Java 21+
- Maven 3.9+
- MySQL 8.0+ (en cours d'exécution)

## Configuration

Modifier les identifiants de connexion dans src/main/java/fr/ticngo/config/DatabaseConnection.java :


private static final String URL = "jdbc:mysql://localhost:3306/ticngo";
private static final String USER = "ticngo";
private static final String PASSWORD = "TON_MOT_DE_PASSE";


La base ticngo doit exister au préalable (voir le script schema_ticngo.sql fourni pour créer les tables).

## Lancement


mvn javafx:run


Au premier lancement, si la base est vide, des données de démonstration sont insérées automatiquement (voir `DataInitializer.java`).

## Connexion par défaut

| Email | Mot de passe |
|----------------------|--------------|
| admin@ticngo.fr | Admin1234! |

## Fonctionnalités

- Tableau de bord : statistiques globales (billets, spectacles, clients, revenus)
- Billets : création, annulation, validation, export PDF individuel ou liste
- Spectacles : CRUD complet avec gestion du lieu et de la catégorie
- Clients : CRUD complet avec recherche
- Authentification : BCrypt, session locale

## Structure du projet


src/main/java/fr/ticngo/
├── TicnGoApplication.java # Point d'entrée JavaFX
├── model/ # Classes métier (POJO simples)
├── repository/ # Accès aux données en JDBC pur (DriverManager, PreparedStatement)
├── service/ # Logique métier
├── controller/ # Contrôleurs JavaFX (FXML)
├── config/ # DatabaseConnection, AppContext (DI manuelle), DataInitializer
└── util/ # FxmlLoader, NavigationService, SessionManager

src/main/resources/
├── fxml/ # Vues FXML
└── css/style.css # Thème dark sidebar


## Tech stack

| Technologie | Version |
|-----------------------|-----------|
| Java | 21 |
| JavaFX | 21.0.2 |
| JDBC (MySQL Connector)| 8.3.0 |
| iText 8 (export PDF) | 8.0.3 |

## Architecture

L'application suit une architecture MVC en couches, sans framework d'injection de dépendances ni d'ORM :

- model : classes métier simples (POJO), sans annotation
- repository : accès direct à la base via JDBC (`DriverManager`, `PreparedStatement`), mapping SQL → objet fait manuellement
- service : logique métier au-dessus des repositories
- controller : contrôleurs JavaFX/FXML, gèrent l'affichage et les interactions utilisateur
- config/AppContext : conteneur d'injection de dépendances "fait main" (instanciation manuelle des repositories/services), pas de framework DI
