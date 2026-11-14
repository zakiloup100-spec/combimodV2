# Combi Mod (Fabric, Minecraft 1.21.11)

**Pour installer facilement : lis INSTALLATION.md.**

## Versions
- Minecraft 1.21.11, Java 21, Fabric Loader 0.18.4, Fabric API 0.140.2+1.21.11, Loom 1.14-SNAPSHOT, mappings Mojang.
- API vérifiée d'après la doc Fabric 1.21.11 et 26.1.2 (registres, data components, armures, Identifier).

## Build automatique (sans PC)
Le dossier `.github/workflows/build.yml` compile le mod et lance les tests à chaque envoi sur GitHub. Le `.jar` est téléchargeable dans l'onglet Actions. Voir INSTALLATION.md.

## Statut de vérification
- ServerPlayConnectionEvents.DISCONNECT (Fabric) pour sauvegarder à la déconnexion. Non vérifié dans la doc 1.21.11.
- StandardCopyOption.ATOMIC_MOVE sur le système de fichiers du serveur : certains systèmes ne le supportent pas, auquel cas il faut retirer ATOMIC_MOVE.

Vérifié dans les mappings ou la doc 1.21.11 :
- Item.appendHoverText (TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)
- LivingEntity.hurtServer(ServerLevel, DamageSource, float)
- TamableAnimal.tame(Player) (utilisé pour les loups invoqués)
- EntitySpawnReason.SPAWN_ITEM_USE
- ServerLivingEntityEvents.AFTER_DAMAGE
- ServerBossEvent et BossEvent.BossBarColor / BossBarOverlay
- ItemAttributeModifiers (package net.minecraft.world.item.component)

Non vérifié, à contrôler au premier build :
- EntityType.Builder.fireImmune() (utilisé pour que les boss ne brûlent pas)
- EntityType.create(ServerLevel, EntitySpawnReason) pour créer les loups
- displayClientMessage(Component, true) pour la barre d'action du mana
- Mob.setTarget(LivingEntity) surchargé pour ignorer les alliés (esprits)
- Constructeur direct de l'entité SpiritEntity (sans EntityType.create)
- ServerLevel.sendParticles(ParticleOptions, x, y, z, count, dx, dy, dz, vitesse) (VisualFx)
- Level.playSound(null, x, y, z, SoundEvent, SoundSource, volume, pitch) (VisualFx)
- Noms des sons : EVOKER_CAST_SPELL, EVOKER_PREPARE_SUMMON, SOUL_ESCAPE, EXPERIENCE_ORB_PICKUP, CHEST_OPEN, PLAYER_ATTACK_CRIT
- ParticleTypes.SOUL
- Heightmap : level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) (apparition des colis)
- BlockState.is(Block) (détection des colis)
- SoundEvents.BELL_RESONATE
- PlayerList.getPlayerByName (commandes /ally ; présent dans la classe de service de même nom)
- Item.use(Level, Player, InteractionHand)
- MobEffectInstance(MobEffects.X, durée, niveau) sans Holder
- PlayerList.getPlayerByName et broadcastSystemMessage (guildes, événement)
- CommandSourceStack.hasPermission(2) pour /event start et /event stop (vérifier aussi la nouvelle API de permissions si elle a changé)
- BiomeModifications.addSpawn (API Fabric)
- Le constructeur ArmorMaterial et les imports d'armure (ArmorType, EquipmentAssets)

## Tests du cœur
Voir tests/README.md et CHANGELOG.md. Les deux suites de tests de la logique passent (fusions de toutes les paires, générateur, codec, économie, coffres, codex).

## Commandes
/balance, /shop list, /shop buy <id>, /sell, /sell junk, /fuse, /stats, /codex, /recipes, /reforge, /ally invite|accept|remove|list, /guild create|invite|accept|leave|disband|info|top, /event status (tous) | /event start|stop (op), /gamble <mise>, /chest prices, /chest buy <1-3>, /gamble <mise>

## Compiler et lancer
1. Mettre la dernière version de fabric-loom (https://fabricmc.net/develop) dans build.gradle.
2. ./gradlew build  -> le jar est dans build/libs/
3. ./gradlew runClient pour tester en local.

## Fonctionnalités
- Items : un item physique générique (combimod:combi_item). Nom et stats viennent du composant ITEM_DEF.
- 20 items de base avec tags et modificateurs, plus des items fusionnés créés à la volée.
- /fuse : fusionne l'item de la main principale et celui de la main gauche.
- /sell : vend l'item tenu en main principale (moitié du prix).
- /shop list, /shop buy <id> : boutique (l'item est donné au joueur).
- /balance : solde.
- Coffre mystique (bloc combimod:mystic_chest) : un clic donne un item aléatoire.
- Drops : tables par boss (Wither, Ender Dragon, Elder Guardian dans MobDrops.bossPool), 2 % de drop sur les monstres.
- Effets passifs : HEAL_PER_SECOND (depuis l'inventaire).
- Combat : DAMAGE (dégâts bonus de l'arme en main), LIFESTEAL (vol de vie sur coup direct), THORNS (épines de l'armure).
- Persistance : soldes et items fusionnés dans config/combimod-data.json (sauvegarde à l'arrêt et toutes les 5 min).

## Boss zombies
- 3 zombies boss (Explosif, Glacé, Vampire) qui spawnent naturellement dans l'Overworld (poids 15, comme un zombie courant).
- Pouvoirs toutes les 5 s sur les joueurs à moins de 6 blocs : poussée, lenteur ou drain de vie.
- Drops de leur table de boss à la mort.
- Apparence : zombie vanille pour l'instant (renderer partagé dans client/CombiModClient).

## Descriptions d'items
- Au survol : rareté, types, stats (en bleu ou rouge), malédiction et valeur.
- Limite : sur un client, les items fusionnés n'ont pas de définition connue. Il faut synchroniser la définition complète dans le composant (à faire).

## Raretés et puissance
- Commun (x1), Rare (x1.35), Épique (x1.8), Légendaire (x2.5), Mythique (x3.5).
- La rareté multiplie les stats, la puissance des sorts et la recharge. Le nom est coloré selon la rareté.

## Sorts
- Un item avec le tag Magie lance un sort au clic droit : frappe la cible la plus proche dans le champ de vision (16 blocs).
- Dégâts = 4 + puissance des sorts (multipliée par la rareté). Recharge réduite par la puissance.

## Armures custom
- Cuir runique (commun), maille runique (rare), divin Zeus (légendaire).
- Défense bien au-dessus du vanilla : cuir runique, maille runique et Zeus valent respectivement 3/5/4/3, 6/10/8/5 et 10/16/14/10.
- Pièces Zeus : Casque, Cuirasse, Pantalon et Bottes du dieu Zeus.
- Bonus de set : 4 pièces Zeus = Vitesse II et Force II. 4 pièces de maille = Résistance I.
- Textures : unies, générées automatiquement. Remplace-les par de vrais dessins.

## Invocations
- Un item avec le tag Invocation (Totem du loup, Esprit du loup, Orbe nécrotique) invoque des loups alliés au clic droit.
- Nombre : 2 + SUMMON_COUNT (rareté incluse), 8 au maximum. Force : SUMMON_DAMAGE.
- Les loups sont apprivoisés par le joueur et disparaissent après 60 s ou à leur mort. Recharge : 30 s.

## Pas encore fait
- Attributs vanilla (via ATTRIBUTE_MODIFIERS) : MAX_HEALTH (tous), ATTACK_SPEED (main), DEFENSE -> armure (items non-armure).
- Non appliqué : MANA_REGEN (pas de système de mana). Les pièces d'armure gardent leur défense du matériau.
- Limite : les dégâts bonus ignorent la réduction d'armure (AFTER_DAMAGE).
- Textures et modèles propres : tous les items utilisent un modèle placeholder (bâton vanille).
- Recette du coffre : le bloc n'a pas de recette, à donner via /give ou une recette JSON.
- Aucune compilation ni test effectués : des erreurs d'API sont à prévoir. Vérifier surtout :
  useWithoutItem (MysticChestBlock), getName (CombiItem), networkSynchronized (ModComponents),
  et la sérialisation Gson des records (nécessite Gson 2.10+).

## Avant d'installer sur un serveur
Demande aux admins du serveur (Minewind ou autre). Un mod qui ajoute de l'économie et des items doit être accepté par eux.
