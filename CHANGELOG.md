# Changelog

## 0.17.1 (build dans le cloud)
- Ajout d'un workflow GitHub Actions : tests de la logique, puis compilation Minecraft, puis publication du .jar.
- Le build se lance sans PC, depuis un téléphone (voir INSTALLATION.md).

## 0.17.0 (contenu)
- 8 nouvelles recettes secrètes, qui portent le total à 25 : Lanterne des ossements, Lame du crépuscule, Armure runique, Chef de meute, Hymne de l'éther, Panthéon animal (mythique), Éclat du tonnerre, Sceptre du néant pur.
- 4 nouveaux préfixes et 4 nouveaux suffixes pour les objets générés (Éclatant, Hanté, Forgé, Céleste ; de l'Orage, des Cendres, de l'Aurore, du Spectre).
- Tests : chaque recette reste non rentable (la revente du résultat coûte moins que les deux parents). Deux prix ont été ajustés pour respecter ce test.

## 0.16.2 (sauvegardes)
- Les 5 dernières sauvegardes sont gardées (config/combimod-data.json.1 à .5). Si le fichier principal est abîmé ou mal écrit, tu peux remettre une copie à la place.

## 0.16.1 (sauvegarde de l'argent)
- Les soldes sont sauvegardés à chaque déconnexion : les pièces restent acquises même si tu te déconnectes juste après un pari ou un coffre.
- Sauvegarde automatique toutes les minutes (avant 5 minutes), donc un crash fait perdre au plus une minute de progression.
- Écriture atomique : la sauvegarde passe par un fichier temporaire. Un arrêt brutal pendant l'écriture ne corrompt plus le fichier.

## 0.16.0 (pari)
- /gamble <mise> : pile ou face à 50/50. Gagner rend le double de la mise (gain net = mise), perdre fait perdre la mise.
- Sans avantage pour la maison : une chance sur deux exactement, espérance nulle. Vérifié sur 200 000 paris.
- Limites : mise de 1 à 1000 pièces, délai de 3 secondes entre deux paris, mise impossible au-delà du solde.
- Sons de victoire et de défaite.
- Tests : taux de victoire et espérance vérifiés statistiquement, et les refus de mise.

## 0.15.0 (colis du ciel et roue de rareté)
- Un colis tombe du ciel toutes les 8 à 15 minutes, à 30-60 blocs d'un joueur au hasard. Un rayon doré et un son signalent sa chute, et tout le serveur est prévenu avec ses coordonnées.
- Le colis disparaît après 3 minutes s'il n'est pas ouvert.
- Ouvrir un colis donne 5 à 25 pièces. Avec 10 % de chance, il déclenche la roue de rareté.
- Roue de rareté (pari) : Rien 30 %, Commun 35 %, Rare 20 %, Épique 10 %, Légendaire 4 %, Mythique 1 %. Rien = pas de récompense. Une rareté = un objet de cette rareté.
- Animation : 3 secondes de défilement dans la barre d'action, puis le résultat.
- Tests : intervalle, somme, chance de roue et probabilités de chaque segment, vérifiés statistiquement sur 200 000 tirages.

## 0.14.0 (effets visuels et sonores)
- Sorts : un rayon de particules part de toi vers la cible (couleur de l'effet de l'arme), une explosion de particules marque l'impact, et un son de sort. Sans cible, le rayon part devant toi.
- Coups d'arme : petite explosion de particules de la couleur de l'effet, à chaque coup sur un ennemi.
- Invocations : nuage d'âmes et son à l'apparition de chaque esprit.
- Esprits : anneau de particules de leur couleur et son à chaque pulsation.
- Soins de soutien : étincelles vertes et son sur chaque allié soigné.
- Coffres : explosion dorée et son d'ouverture.
- Tous les effets sont envoyés par le serveur : les autres joueurs les voient et les entendent.

## 0.13.0 (esprits et effets d'arme)
- Les invocations ne sont plus des loups : ce sont des esprits. Ils sont passifs avec ton équipe (ils ne prennent jamais un allié pour cible) et hostiles aux monstres et aux joueurs non alliés.
- 3 types d'esprits selon l'objet : Spectre sombre (magie : aveugle et affaiblit les ennemis), Gardien (défense : ralentit), Esprit (poison). Ils frappent les ennemis proches toutes les 3 secondes.
- Chaque arme et baguette a une combinaison unique de 2 à 4 effets parmi 7 (lenteur, poison, faiblesse, cécité, flétrissement, lueur, lévitation). 50 armes du catalogue, toutes différentes. Les effets s'appliquent aux ennemis touchés et sont affichés au survol.
- Les objets générés ont une combinaison stable, dérivée de leur identifiant.
- Tests : 116 vérifications (dont l'unicité des combinaisons et le type d'esprit selon l'objet).

## 0.12.0 (mana et invocation)
- Mana : réserve par joueur. Maximum de 60, plus 10 par point de régénération des objets équipés. Régénère 2 par seconde, plus la régénération des objets. Affichée dans la barre d'action.
- Sorts : coûtent 8 mana. Invocations : 15 mana. Sans assez de mana, le sort ou l'invocation est refusé avec un message.
- 9 objets nouveaux : Sifflet de meute, Collier de meute, Flûte d'invocation, Os de lune, Totem ancestral, Sceptre astral (invocation), Cristal de mana, Pierre arcane, Orbe de mana pur (mana).
- Les loups invoqués gardent leur force (SUMMON_DAMAGE) et reçoivent la résistance si l'objet a de la défense (Os de lune).
- Le mana est remis à plein à chaque arrêt du serveur.
- Tests : maximum et régénération bornés, dépense exacte, refus sans mana, reset, et présence des nouveaux objets.

## 0.11.1 (permissions)
- Toutes les commandes joueur sont ouvertes à tous : /guild, /ally, /balance, /shop, /sell, /fuse, /reforge, /codex, /recipes, /stats, /chest.
- Commandes admin réservées aux opérateurs (niveau 2) : /event start et /event stop.
- /event status reste ouvert à tous, pour que les joueurs voient où en est la guerre.

## 0.11.0 (guildes et guerre)
- Guildes : /guild create|invite|accept|leave|disband|info|top. Une guilde par joueur, nom de 3 à 16 caractères, 20 membres maximum. Le chef invite. Le chef ne peut pas quitter une guilde avec des membres.
- Événement de guerre : /event start|stop|status (opérateurs uniquement). 6 points de capture entre 300 et 500 blocs du spawn.
- Capture : une guilde seule sur un point pendant 30 s le prend. Si plusieurs guildes sont présentes, la capture est gelée. Un point possédé rapporte 2 points de guilde par seconde.
- Fin de guerre : la guilde en tête de l'événement reçoit 500 points de guilde. Le classement /guild top est cumulatif.
- Sauvegarde : guildes, points et propriétaires des points.
- Tests : 97 vérifications de la logique (guildes, captures, événement), dans `sh tests/run.sh`.

## 0.10.2
- Vérification des API contre la doc et les mappings 1.21.11. Confirmées : appendHoverText, hurtServer, tame, SPAWN_ITEM_USE. Aucun changement de code nécessaire pour elles.
- README : statut de vérification corrigé. fireImmune et SpawnPlacements n'étaient pas confirmés, ce qui est maintenant dit explicitement.

## 0.10.1 (nettoyage)
- Arrêt du serveur : les loups invoqués sont retirés, et les cooldowns, barres de boss et cibles sont vidés. Avant, cet état pouvait survivre entre deux sessions.
- Un seul script de test : `sh tests/run.sh` (67 vérifications).
- Correction de deux parenthèses en trop dans /ally invite et /ally remove.
- README : liste explicite des API non vérifiées.

## 0.10.0 (PvP et coop)
- /ally invite <joueur>, /ally accept, /ally remove <joueur>, /ally list. Alliance mutuelle, une invitation à la fois, sauvegardée.
- Soin et buffs (Soin, Soutien, Défense) : touchent le lanceur et ses alliés à moins de 8 blocs. Jamais les autres joueurs.
- Sorts : les alliés ne sont pas touchés (pas de tir allié). Les ennemis le sont toujours.

## 0.9.1
- Correctif PvP : les capacités de soin et de soutien ne touchent plus que le lanceur (avant, elles affectaient aussi les joueurs ennemis proches).
- Le statut de vérification est clarifié : la logique du cœur est testée (tests Java purs). La couche Minecraft n'a jamais été compilée.

## 0.9.0
- Capacités actives au clic droit, selon les types de l'objet : Soin (soigne les joueurs à 8 blocs), Soutien (vitesse + force), Défense (résistance + absorption). Légendaire et mythique : palier supérieur. Recharge de 20 s (commun) à 10 s (mythique).
- /reforge : relance les stats d'un objet (même rareté, même orientation) contre des pièces. /reforge price donne le prix. Armures et objets de recette exclus.
- Le prix de relance dépend de la rareté, pas de l'objet : relancer ne rapporte jamais d'argent (testé).

## 0.8.0
- 23 objets de boutique en plus (communs à mythiques : épée de Zeus, trident de Poséidon, sceptre d'Hadès, égide d'Athéna, larme des dieux...).
- 14 recettes secrètes : des objets modestes qui donnent des objets très puissants. Résultats absents de la boutique, des coffres et des drops.
- /recipes : liste les recettes. Les recettes non découvertes sont affichées en « ??? » avec la rareté et les types.
- Catalogue : 81 objets, 17 recettes. Les tests vérifient que les recettes restent non rentables.

## 0.7.1
- Barre de boss colorée (nom, pouvoir, vie) qui apparaît à moins de 24 blocs d'un zombie boss.
- Les boss sont immunisés au feu : ils ne brûlent plus au soleil.

## 0.7.0
Corrections d'API pour Minecraft 1.21.11 (trouvées en vérifiant les vrais mappings) :
- Zombie est dans net.minecraft.world.entity.monster.zombie (BossZombie).
- Les dégâts passent par hurtServer(level, source, montant) (sorts, combat, boss).
- SpawnPlacements.register est privé en 1.21.11 : retiré (les boss spawnent via les biomes, sans règle de lumière).
- EquipmentAssets.ROOT_ID, SoundType (net.minecraft.world.level.block), ItemAttributeModifiers (package component).

Nouveautés :
- Objets stockés en entier dans l'item (ItemCodec) : fusions et objets générés gardent leurs stats et s'affichent côté client.
- Générateur : 12 types x 19 préfixes x 18 suffixes x 5 raretés (4548 combinaisons hors rareté) + bonus de signature légendaire.
- Fusion : 21 synergies (toutes les paires de tags), 8 bonus de trio, mutation surprise, plafonds, 1 chance sur 8 d'améliorer la rareté, jackpot à 6 tags.
- 8 zombies boss (au lieu de 3), chacun avec son pouvoir, ses stats et sa table de drop.
- Pièces gagnées en tuant (mobs 2-6, boss 150-300).
- 3 niveaux de coffres à usage unique, achetables : /chest prices, /chest buy <1-3>.
- /codex (collection), /stats (objet en main), /sell junk (vend les communs).
- Économie testée : revendre un coffre ouvert ne rapporte jamais plus que son prix, et aucune fusion n'est rentable.
- Nouveaux fichiers : noms en français (lang), tables de butin et recettes de blocs.

## 0.6.0
- 15 objets supplémentaires.
