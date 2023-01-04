# Projet MOBG5 

## Application "OnlyBeer" | Slogan : "l'amour est dans la binouze" (nom provisoire)

### Description de l'application : 

Tinder like pour les bières. Les bières s'affichent une par une, on a la possibilité des les liker ou non. Si on les like, celles-ci sont ajoutées
dans nos favoris où on peut y voir une description plus détaillée ainsi que les bars aux alentours nous permettant de la consommer. Ceux-ci seront affichés sur une google map dans un rayon de X kilomètres aux alentours de notre position. Celle-ci sera affichée dessus.  

### Fonctionnalités de base : 

- Page de login/register via adresse mail (DONE)
- Page principale qui affiche une bière ainsi qu'une description brève. (DONE)
- Système de like/dislike via swipe et/ou par bouton (DONE)
- Page contenant les favoris (bières likées). (DONE)
- Page détaillées de la bière avec une description plus précise ainsi que la position des bars qui nous permettent d'y gouter. (DONE, EDIT -> bouton redirigeant vers google map avec les informations nécessaire)  
- Page avec les paramètres pour se déconnecter, les réglages du gps,... (itération 2) (REPLACE BY PROFILE PAGE) (DONE)
- Via les paramètres, pouvoir faire une demande d'ajout de bière (itération 2) -> (EDIT -> ON PROFIL PAGE) (DONE)

### Fonctionnalités technique : 

- Utilisation d'une database en ligne (via Firebase) (DONE)
- Gestion des utilisateurs (DONE)
- Utilisation d'une google map (DONE, EDIT -> utilisation de l'application google map)
- Utilisation du GPS (DONE)
- Utilisation de la gallerie de l'utilisateur (itération 2) (DONE)
- Utilisation de google ads / google play ads (itération 2)
- Utilisation de l'appareil photo (itération 2) (DONE)
- Login via compte Google (itération 2) (DONE)

### Réponses aux questions : 
Quelle version du `SDK` utilisez-vous ? 
- minSdk 28 : L'application cible au moins Android 9.0 (Pie). Les utilisateurs ne pourront pas l'installer sur des appareils qui exécutent une version inférieure d'Android.
- targetSdk 33 : L'application est conçue pour être exécutée sur Android 11. L'application doit être testée sur cette version et il faut utiliser les API et les fonctionnalités disponibles dans Android 11.
- compileSdk 33 : L'application est compilée sur la plateforme Android 11. Cela signifie qu'elle peut utiliser toutes les API et toutes les fonctionnalités disponibles dans Android 11 lors de la compilation de votre application.

Quel `système de persistance des données` avez-vous utilisé ?
- Firebase Authentification : est un service d'authentification qui vous permet de gérer l'authentification des utilisateurs de votre application.
- Firestore Database : est une base de données NoSQL en temps réel qui vous permet de stocker et de synchroniser des données en temps réel dans le cloud.
- Firestorage : est un service de stockage de fichiers qui vous permet de stocker et de gérer les fichiers de votre application dans le cloud.

Quelle est `l’architecture` de votre application ?
- L'architecture MVVM (Model-View-ViewModel): dans cette architecture, le ViewModel s'occupe de la logique métier de l'application et de la communication avec le Model, tandis que la View s'occupe de l'affichage des données à l'utilisateur. Le ViewModel utilise des observables pour mettre à jour la View en temps réel. Cette architecture est particulièrement utile pour les applications Android car elle prend en charge le cycle de vie des activités et des fragments. 
- L'avantage de cette architecture est qu'elle prend en charge le cycle de vie des fragments et qu'elle permet de séparer clairement les responsabilités de chaque couche de l'application. Cela rend le code plus facile à maintenir et à tester. Cependant, il peut être nécessaire de mettre en place des mécanismes de communication entre les différents ViewModel pour gérer les interactions complexes entre les fragments.

Comment avez-vous `implémenté le lien avec la base de données` ? 
- 1) Créez un projet Firebase et ajoutez votre application Android comme nouvelle application.
- 2) Dans Android Studio, ouvrez le fichier build.gradle de votre module app et ajoutez les dépendances Firebase suivantes: implementation "com.google.firebase:firebase-database"
    implementation platform('com.google.firebase:firebase-bom:31.1.0') //Needed for firebase
    implementation 'com.google.firebase:firebase-analytics-ktx' //Needed for firebase
    implementation 'com.google.firebase:firebase-auth-ktx' //Needed for firebase authentification
- 3) Initialisez Firebase dans votre application en suivant les instructions de la documentation officielle de Firebase. Cela implique généralement d'ajouter une ligne de code dans le fichier build.gradle de votre projet et de créer un fichier de configuration JSON fourni par Firebase.
- 4) Pour créer un compte : FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!) 
- 5) Pour accéder à des données (Autoriser la lecture/écriture dans firebase) : db.collection("NomDeLaconnexion").document(uid de l'utilisateur)

Comment avez-vous `décomposé votre vue` en différents fragments ? 
- Chaque fragment possède un fichier XML et certains élements de cet XML possède des drawables, font, color,... 
- Chaque page de l'application possède un fragment.
- Chaque fragment possède un viewModel qui s’occupe de la logique métier de l’application.

Quelle est `l’importance des remarques générées par Lint` lors de l’analyse de votre projet ?
- Elles vous permettent de détecter et de corriger des problèmes potentiels dans votre code.
- Par exmeple : 
    Code non optimisé ou inefficace
    Utilisation de méthodes obsolètes ou dépréciées
    Problèmes de style de code (formatage, nommage, etc.)
    Erreurs de syntaxe ou de typage
    Problèmes de sécurité
    Problèmes de compatibilité avec les différentes versions d'Android
- Corriger ces problèmes, permet améliorer la qualité et la performance de l'application, éviter les bugs et faciliter son maintien à long terme.

### Prototype itération 1: 

![alt text](https://cdn.discordapp.com/attachments/913918732647170078/1033008918705745930/prototype_iteration_1.png)

### Prototype itération 2: 

![alt text](https://media.discordapp.net/attachments/913918732647170078/1032994416455077898/unknown.png?width=936&height=670)

Les pages prototypes sont disponible en détails dans le fichier proto.pdf


