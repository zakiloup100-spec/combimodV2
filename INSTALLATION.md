# Installer Combi Mod (version simple)

## Option sans PC : compiler dans le cloud (GitHub)

Tu n'as besoin que d'un téléphone, et d'un ordinateur une seule fois pour mettre le projet en ligne (ou d'un ami).

1. Crée un compte gratuit sur https://github.com.
2. Crée un nouveau dépôt (New repository), nom `combimod`, en **Public** ou **Private**.
3. Ajoute le contenu du dossier `combimod` au dépôt (Add file > Upload files). Le fichier `.github/workflows/build.yml` doit être inclus.
4. Va dans l'onglet **Actions**. Le build se lance tout seul.
5. Quand il est vert : ouvre le dernier build, descends jusqu'à **Artifacts**, et télécharge **combimod-jar**. Dedans, il y a le `.jar` à installer.
6. Si le build est rouge : ouvre-le, clique sur l'étape **Build du mod** ou **Tests**, et envoie-moi les lignes qui contiennent `error:` ou `FAILED`.

La suite de la page explique l'installation du `.jar` sur Aternos et sur ton PC.

---


Tu n'as besoin d'aucune commande. Suis les étapes dans l'ordre.

## Étape 1 : installer deux logiciels gratuits

1. **Java 21** : va sur https://adoptium.net, clique sur **Temurin 21 (LTS)**, installe-le avec les options par défaut.
2. **IntelliJ IDEA Community** : va sur https://www.jetbrains.com/idea/download, choisis la version **Community** (gratuite), installe-la.

## Étape 2 : ouvrir le projet

1. Dézippe **combimod.zip**. Tu obtiens un dossier **combimod**.
2. Ouvre IntelliJ IDEA.
3. Clique sur **Open** (Ouvrir), choisis le dossier **combimod** (celui qui contient `build.gradle`), puis **OK**.
4. Si IntelliJ demande des confirmations, accepte **Trust Project**.
5. Attends que la barre en bas à droite finisse de charger (3 à 10 minutes la première fois, il télécharge tout seul).

## Étape 3 : construire le mod

1. Sur le côté droit de la fenêtre, ouvre l'onglet **Gradle** (icône d'éléphant).
2. Va dans **combimod > Tasks > build**.
3. **Double-clique sur build**.
4. Attends la fin. En bas, le message doit dire **BUILD SUCCESSFUL**.
5. Le mod est dans le dossier **combimod > build > libs**. Prends le fichier **combimod-0.17.1.jar**. **Ne prends pas** celui qui finit par `-sources`.

Si tu as une erreur rouge (`BUILD FAILED`), copie le message entier et envoie-le-moi.

## Étape 4 : installer sur Aternos

Pour Aternos, il faut trois fichiers `.jar` dans le dossier `mods` du serveur :
- **Fabric API 0.140.2+1.21.11** (à télécharger sur https://modrinth.com, en cherchant "Fabric API" pour la version 1.21.11)
- **combimod-0.17.1.jar** (celui de l'étape 3)

Et le serveur doit être configuré ainsi :
1. Aternos > ton serveur > **Logiciel** : **Fabric**.
2. **Version** : **1.21.11**.
3. Démarre une fois, arrête-le.
4. Aternos > **Fichiers** > dossier **mods** > **Upload** : ajoute les deux `.jar`.
5. Redémarre le serveur.

## Étape 5 : installer sur ton PC (pour voir les items)

1. Installe **Fabric** pour 1.21.11 : https://fabricmc.net/use (télécharge l'installeur, choisis **1.21.11**, clique **Install**).
2. Ouvre le launcher Minecraft, choisis le profil **fabric-loader-1.21.11**, puis lance.
3. Mets **Fabric API** et **combimod-0.17.1.jar** dans le dossier `.minecraft/mods` (tape `%appdata%` dans la barre Windows pour trouver `.minecraft`, ou utilise le bouton "Ouvrir le dossier mods" de ton launcher).
4. Lance le jeu, puis connecte-toi à ton serveur.

## Aide
- Mod non chargé : vérifie que Fabric API et combimod sont tous les deux dans `mods`, et que la version est bien 1.21.11.
- Erreur au lancement : envoie le fichier `logs/latest.log`.
