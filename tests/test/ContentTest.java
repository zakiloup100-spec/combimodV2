package test;

import com.example.combimod.core.*;
import java.util.*;

public class ContentTest {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "OK   " : "FAIL ") + name);
        if (!ok) failures++;
    }

    static boolean sameItem(ItemDef a, ItemDef b) {
        return a.id().equals(b.id()) && a.name().equals(b.name()) && a.tags().equals(b.tags())
                && a.modifiers().equals(b.modifiers()) && a.price() == b.price() && a.rarity() == b.rarity();
    }

    public static void main(String[] args) {
        List<ItemDef> base = new ArrayList<>(Items.BASE.values());

        // ---- Codec ----
        boolean codecOk = true;
        for (ItemDef d : base) {
            ItemDef back = ItemCodec.decode(ItemCodec.encode(d));
            if (back == null || !sameItem(d, back)) { codecOk = false; break; }
        }
        Random rng = new Random(7);
        for (int i = 0; i < 2000 && codecOk; i++) {
            ItemDef g = Generator.generate(rng, Rarity.values()[rng.nextInt(5)], Set.of());
            ItemDef back = ItemCodec.decode(ItemCodec.encode(g));
            if (back == null || !sameItem(g, back)) codecOk = false;
        }
        check("codec : aller-retour exact (catalogue + 2000 générés)", codecOk);
        check("codec : texte invalide renvoie null", ItemCodec.decode("n'importe quoi") == null);

        // ---- Fusion : toutes les paires du catalogue ----
        boolean commut = true, noThrow = true, caps = true, rarityOk = true, never = true, noPipe = true;
        int pairs = 0;
        for (ItemDef a : base) {
            for (ItemDef b : base) {
                try {
                    ItemDef ab = Fusion.fuse(a, b);
                    ItemDef ba = Fusion.fuse(b, a);
                    pairs++;
                    if (!sameItem(ab, ba)) commut = false;
                    for (Modifier m : ab.modifiers()) if (Math.abs(m.value()) > m.affix().cap + 1e-9) caps = false;
                    if (ab.rarity().ordinal() < Math.max(a.rarity().ordinal(), b.rarity().ordinal())) rarityOk = false;
                    if (ab.name().contains("|")) noPipe = false;
                    boolean isRecipe = Fusion.recipes().containsKey(new HashSet<>(Arrays.asList(a.id(), b.id())));
                    if (!isRecipe && ab.price() / 2 >= a.price() + b.price()) never = false;
                } catch (Exception e) {
                    noThrow = false;
                    System.out.println("   exception : " + a.id() + " + " + b.id() + " : " + e);
                }
            }
        }
        System.out.println("   " + pairs + " fusions testées");
        check("fusion : jamais d'exception (y compris un objet avec lui-même)", noThrow);
        check("fusion : même résultat dans les deux sens", commut);
        check("fusion : plafonds respectés", caps);
        check("fusion : rareté jamais inférieure aux parents", rarityOk);
        check("fusion : revendre le résultat rapporte moins que les parents", never);
        check("fusion : noms sans caractère réservé", noPipe);
        check("fusion : 21 synergies de paires (toutes les paires de tags)", Fusion.synergyCount() == 21);
        check("fusion : 8 bonus de trio", Fusion.tripleCount() == 8);

        // Fusion répétée : les stats ne dépassent jamais les plafonds
        ItemDef cur = Items.ALL.get("sang_dragon");
        boolean stable = true;
        for (int i = 0; i < 40; i++) {
            cur = Fusion.fuse(cur, Items.ALL.get(i % 2 == 0 ? "livre_ombres" : "totem_loup"));
            for (Modifier m : cur.modifiers()) if (Math.abs(m.value()) > m.affix().cap + 1e-9) stable = false;
        }
        check("fusion en chaîne x40 : plafonds respectés, identifiant court (<= 20 car.)", stable && cur.id().length() <= 20);

        // Recettes spéciales : jamais rentables
        boolean recipesFair = true;
        for (Map.Entry<Set<String>, ItemDef> e : Fusion.recipes().entrySet()) {
            long cost = 0;
            for (String id : e.getKey()) cost += Items.BASE.get(id).price();
            long sell = Math.max(1, e.getValue().price() / 2);
            System.out.println("   recette " + e.getKey() + " : achat " + cost + ", revente " + sell);
            if (sell >= cost) recipesFair = false;
        }
        check("recettes spéciales : revente inférieure au coût des parents", recipesFair);
        check("boutique : ne vend pas les objets de recette uniquement", !Items.SHOP.containsKey("grimoire_du_pacte"));

        // Recettes secrètes : cohérence
        boolean parentsOk = true, capsOk = true, resultsListed = true;
        Set<String> resultIds = new HashSet<>();
        for (Map.Entry<Set<String>, ItemDef> e : Fusion.recipes().entrySet()) {
            resultIds.add(e.getValue().id());
            for (String id : e.getKey()) if (!Items.BASE.containsKey(id)) parentsOk = false;
            if (!Items.BASE.containsKey(e.getValue().id())) resultsListed = false;
            for (Modifier m : e.getValue().modifiers()) if (Math.abs(m.value()) > m.affix().cap + 1e-9) capsOk = false;
        }
        boolean onlyOk = true;
        for (String id : Items.RECIPE_ONLY) {
            if (Items.SHOP.containsKey(id) || !resultIds.contains(id)) onlyOk = false;
        }
        System.out.println("   " + Fusion.recipes().size() + " recettes secrètes, catalogue : " + Items.BASE.size() + " objets");
        check("recettes : au moins 17", Fusion.recipes().size() >= 17);
        check("recettes : parents et résultats existent dans le catalogue", parentsOk && resultsListed);
        check("recettes : résultats dans les plafonds", capsOk);
        check("recettes : objets réservés absents de la boutique et issus d'une recette", onlyOk);

        // Les coffres et les mobs ne donnent jamais un objet réservé aux recettes
        Random leak = new Random(9);
        boolean noLeak = true;
        for (int i = 0; i < 20000 && noLeak; i++) {
            for (int tier = 0; tier < Chests.tiers(); tier++) {
                if (Items.RECIPE_ONLY.contains(Chests.roll(tier, leak).id())) noLeak = false;
            }
            if (Items.RECIPE_ONLY.contains(Chests.rollMob(leak).id())) noLeak = false;
        }
        check("coffres et mobs : jamais d'objet réservé aux recettes", noLeak);

        // ---- Générateur ----
        System.out.println("   combinaisons du générateur : " + Generator.totalCombos());
        check("générateur : plus de 4000 combinaisons", Generator.totalCombos() > 4000);
        Random g = new Random(11);
        boolean valid = true, themeOk = true;
        Set<String> ids = new HashSet<>();
        Set<Tag> theme = EnumSet.of(Tag.HEAL, Tag.SUPPORT);
        for (int i = 0; i < 5000; i++) {
            Rarity r = Rarity.values()[g.nextInt(5)];
            ItemDef d = Generator.generate(g, r, Set.of());
            ids.add(d.id());
            if (d.rarity() != r || d.name().isBlank() || d.name().contains("|") || d.price() < 10 || d.tags().isEmpty() || d.modifiers().isEmpty()) valid = false;
            for (Modifier m : d.modifiers()) if (Math.abs(m.value()) > m.affix().cap + 1e-9) valid = false;
            ItemDef t = Generator.generate(g, r, theme);
            if (Collections.disjoint(t.tags(), theme)) themeOk = false;
        }
        check("générateur : 5000 objets valides (nom, prix, tags, plafonds)", valid);
        check("générateur : plus de 3000 identifiants distincts sur 5000 tirages", ids.size() > 3000);
        check("générateur : le thème est toujours respecté", themeOk);

        // Exemples lisibles
        Random ex = new Random(3);
        for (Rarity r : Rarity.values()) {
            ItemDef d = Generator.generate(ex, r, Set.of());
            System.out.println("   exemple " + r + " : " + d.name() + " " + d.modifiers() + " prix " + d.price());
        }

        // ---- Coffres : équilibrage de l'économie ----
        boolean chestsFair = true;
        for (int tier = 0; tier < Chests.tiers(); tier++) {
            Random c = new Random(100 + tier);
            double sum = 0;
            int n = 30000;
            for (int i = 0; i < n; i++) sum += Math.max(1, Chests.roll(tier, c).price() / 2);
            double avg = sum / n;
            System.out.printf("   %s : coût %d, revente moyenne %.0f (%.0f %% du coût)%n",
                    Chests.NAMES[tier], Chests.COST[tier], avg, 100 * avg / Chests.COST[tier]);
            if (avg >= Chests.COST[tier] * 0.7 || avg <= Chests.COST[tier] * 0.15) chestsFair = false;
        }
        check("coffres : revente moyenne entre 15 % et 70 % du coût", chestsFair);

        // ---- Relance (reforge) ----
        Random rf = new Random(21);
        boolean reforgeOk = true, themeKept = true;
        double[] sellSum = new double[5];
        long[] costs = new long[5];
        int[] counts = new int[5];
        for (int i = 0; i < 6000; i++) {
            Rarity r = Rarity.values()[rf.nextInt(5)];
            ItemDef old = Generator.generate(rf, r, Set.of());
            ItemDef fresh = Reforge.reroll(old, rf);
            if (fresh.rarity() != old.rarity()) reforgeOk = false;
            if (Collections.disjoint(fresh.tags(), old.tags())) themeKept = false;
            sellSum[r.ordinal()] += Math.max(1, fresh.price() / 2);
            costs[r.ordinal()] = Reforge.cost(old);
            counts[r.ordinal()]++;
        }
        boolean noProfit = true;
        for (int i = 0; i < 5; i++) {
            double avg = sellSum[i] / counts[i];
            System.out.printf("   relance %s : coût %d, revente moyenne du résultat %.0f%n", Rarity.values()[i], costs[i], avg);
            if (avg >= costs[i] * 0.8) noProfit = false;
        }
        check("relance : rareté conservée", reforgeOk);
        check("relance : orientation (tags) conservée", themeKept);
        check("relance : revendre le résultat rapporte bien moins que le prix", noProfit);
        check("relance : refusée pour une armure et un objet de recette",
                !Reforge.allowed(ArmorDefs.ALL.get(0)) && !Reforge.allowed(Items.ALL.get("grimoire_du_pacte")));
        check("relance : autorisée pour un objet du catalogue", Reforge.allowed(Items.ALL.get("epee_braise")));

        // ---- Alliés ----
        Allies al = new Allies();
        UUID a1 = UUID.randomUUID(), a2 = UUID.randomUUID(), a3 = UUID.randomUUID();
        check("alliés : pas d'invitation à soi-même", !al.invite(a1, a1));
        check("alliés : invitation envoyée", al.invite(a1, a2));
        check("alliés : pas encore alliés avant acceptation", !al.isAlly(a1, a2));
        check("alliés : accepter sans invitation échoue", al.accept(a3).isEmpty());
        check("alliés : acceptation crée une alliance", al.accept(a2).isPresent() && al.isAlly(a1, a2));
        check("alliés : alliance mutuelle", al.isAlly(a2, a1));
        check("alliés : pas d'invitation à un allié", !al.invite(a2, a1));
        check("alliés : retirer casse les deux côtés", al.remove(a2, a1) && !al.isAlly(a1, a2) && !al.isAlly(a2, a1));
        check("alliés : retirer un non-allié ne fait rien", !al.remove(a1, a3));
        Allies copy = new Allies();
        copy.restore(a1, List.of(a2));
        check("alliés : restauration sauvegardée", copy.isAlly(a2, a1));

        // ---- Mana ----
        ManaPool mp = new ManaPool();
        UUID mu = UUID.randomUUID();
        double maxA = ManaPool.maxFor(0), maxB = ManaPool.maxFor(5);
        check("mana : maximum augmente avec la régénération des objets", maxB > maxA);
        check("mana : un joueur commence plein", mp.get(mu, maxA) == maxA);
        check("mana : sort refusé quand il n'y en a plus assez", mp.consume(mu, maxA, maxA) && !mp.consume(mu, maxA, ManaPool.SPELL_COST));
        mp.regen(mu, maxA, 1000);
        check("mana : la régénération ne dépasse jamais le maximum", mp.get(mu, maxA) == maxA);
        mp.consume(mu, maxA, 30);
        check("mana : dépense exacte", mp.get(mu, maxA) == maxA - 30);
        check("mana : retirer un objet borne la réserve au nouveau maximum", mp.get(mu, 20) == 20);
        check("mana : régénération de base positive", ManaPool.regenFor(0) > 0);
        mp.reset();
        check("mana : reset remet à plein", mp.get(mu, maxA) == maxA);

        // ---- Objets d'invocation et de mana ----
        List<String> summonerIds = List.of("sifflet_meute", "flute_invocation", "os_de_lune", "collier_meute",
                "totem_ancestral", "sceptre_astral");
        List<String> manaIds = List.of("cristal_mana", "pierre_arcane", "orbe_mana_pur", "sceptre_astral", "flute_invocation");
        boolean summonerOk = true, manaOk = true;
        for (String id : summonerIds) {
            ItemDef d = Items.BASE.get(id);
            if (d == null || !d.tags().contains(Tag.SUMMON)) summonerOk = false;
        }
        for (String id : manaIds) {
            ItemDef d = Items.BASE.get(id);
            if (d == null || d.modifiers().stream().noneMatch(m -> m.affix() == Affix.MANA_REGEN && m.value() > 0)) manaOk = false;
        }
        check("objets d'invocation : présents avec le tag Invocation", summonerOk);
        check("objets de mana : présents avec une régénération positive", manaOk);
        check("objets de mana : au moins 4 objets dédiés (hors invocation)", Items.BASE.values().stream()
                .filter(d -> d.tags().contains(Tag.MAGIC) && d.modifiers().stream().anyMatch(m -> m.affix() == Affix.MANA_REGEN)).count() >= 4);

        // ---- Effets par arme (trios) et types d'esprits ----
        check("traits : 91 combinaisons possibles (2 à 4 effets)", Traits.trioCount() == 91);
        int weapons = Traits.catalogWeaponCount();
        check("traits : le catalogue tient dans les combinaisons uniques (" + weapons + " armes)", weapons <= Traits.trioCount());
        Set<Set<Traits.Trait>> seen = new HashSet<>();
        boolean catalogOk = true;
        for (ItemDef d : Items.BASE.values()) {
            if (!Traits.isWeapon(d)) continue;
            Set<Traits.Trait> t = Traits.of(d);
            if (t.size() < 2 || t.size() > 4) catalogOk = false;
            if (!seen.add(t)) catalogOk = false;
        }
        check("traits : chaque arme du catalogue a une combinaison unique de 2 à 4 effets", catalogOk);
        boolean genOk = true;
        Random tr = new Random(4);
        for (int i = 0; i < 2000; i++) {
            ItemDef gen = Generator.generate(tr, Rarity.values()[tr.nextInt(5)], Set.of());
            Set<Traits.Trait> gt = Traits.of(gen);
            if (Traits.isWeapon(gen) ? (gt.size() < 2 || gt.size() > 4) : !gt.isEmpty()) genOk = false;
        }
        check("traits : objets générés : combinaison pour les armes, rien pour le reste", genOk);
        check("traits : même objet, même trio (stable)", Traits.of(Items.ALL.get("epee_braise")).equals(Traits.of(Items.ALL.get("epee_braise"))));
        check("spectres : magie + invocation = spectre sombre", Spirits.kindOf(Items.ALL.get("totem_loup")) == Spirits.Kind.SPIRIT
                && Spirits.kindOf(Items.ALL.get("sceptre_astral")) == Spirits.Kind.DARK);
        check("spectres : défense = gardien", Spirits.kindOf(Items.ALL.get("os_de_lune")) == Spirits.Kind.GUARD);
        check("spectres : chaque type a au moins un effet", java.util.Arrays.stream(Spirits.Kind.values()).allMatch(k -> k.pulse.length > 0));

        // ---- Colis du ciel et roue de rareté ----
        Random sk = new Random(31);
        boolean intervalOk = true, moneyOk = true;
        for (int i = 0; i < 5000; i++) {
            int t = SkyDrops.nextIntervalTicks(sk);
            if (t < SkyDrops.MIN_INTERVAL_TICKS || t > SkyDrops.MAX_INTERVAL_TICKS) intervalOk = false;
            long m = SkyDrops.moneyReward(sk);
            if (m < SkyDrops.MONEY_MIN || m > SkyDrops.MONEY_MAX) moneyOk = false;
        }
        check("colis : intervalle entre 8 et 15 minutes", intervalOk);
        check("colis : somme entre 5 et 25 pièces", moneyOk);

        int triggered = 0, n = 200000;
        for (int i = 0; i < n; i++) if (SkyDrops.wheelTriggered(sk)) triggered++;
        double rate = triggered / (double) n;
        System.out.printf("   chance de roue : %.3f (attendu %.2f)%n", rate, SkyDrops.WHEEL_CHANCE);
        check("colis : chance de déclencher la roue proche de 10 %", Math.abs(rate - SkyDrops.WHEEL_CHANCE) < 0.01);

        java.util.Map<SkyDrops.Segment, Integer> hits = new java.util.EnumMap<>(SkyDrops.Segment.class);
        for (int i = 0; i < n; i++) hits.merge(SkyDrops.spin(sk), 1, Integer::sum);
        boolean wheelOk = true;
        for (SkyDrops.Segment seg : SkyDrops.Segment.values()) {
            double expected = seg.weight / (double) SkyDrops.totalWeight();
            double got = hits.getOrDefault(seg, 0) / (double) n;
            System.out.printf("   roue %-12s : %.3f (attendu %.3f)%n", seg.label, got, expected);
            if (Math.abs(got - expected) > 0.01) wheelOk = false;
        }
        check("roue : chaque segment sort selon son poids", wheelOk);
        check("roue : le pari peut ne rien donner (segment Rien)", hits.getOrDefault(SkyDrops.Segment.NOTHING, 0) > 0);
        check("roue : le total des poids vaut 100", SkyDrops.totalWeight() == 100);
        check("roue : chaque rareté (sauf Rien) a un segment", java.util.Arrays.stream(Rarity.values())
                .allMatch(r -> java.util.Arrays.stream(SkyDrops.Segment.values()).anyMatch(s -> s.rarity == r)));

        // ---- Pari pile ou face (sans avantage pour la maison) ----
        Random gr = new Random(77);
        int wins = 0, trials = 200000;
        long totalNet = 0;
        for (int i = 0; i < trials; i++) {
            Gamble.Outcome o = Gamble.flip(gr, 10);
            if (o.won()) wins++;
            totalNet += o.net();
        }
        double winRate = wins / (double) trials;
        System.out.printf("   pari : %.4f de victoires, gain moyen par pari %.4f pièces%n", winRate, totalNet / (double) trials);
        check("pari : une chance sur deux de gagner (écart < 0,5 %)", Math.abs(winRate - 0.5) < 0.005);
        // Écart-type de la moyenne d'un pari de 10 pièces : 10 / racine(n). On tolère 4 écarts-types.
        double standardError = 10.0 / Math.sqrt(trials);
        check("pari : espérance nulle à 4 écarts-types près (aucun avantage pour la maison)",
                Math.abs(totalNet / (double) trials) < 4 * standardError);
        check("pari : victoire rend la mise en gain net, défaite la perd", Gamble.flip(new Random(1), 10).net() == 10 || Gamble.flip(new Random(1), 10).net() == -10);
        check("pari : mise trop petite refusée", Gamble.validate(0, 100, 1000) != null);
        check("pari : mise trop grande refusée", Gamble.validate(Gamble.MAX_BET + 1, 10000, 1000) != null);
        check("pari : mise au-delà du solde refusée", Gamble.validate(50, 20, 1000) != null);
        check("pari : délai entre deux paris respecté", Gamble.validate(10, 100, 5) != null);
        check("pari : pari valide accepté", Gamble.validate(10, 100, 1000) == null);

        // ---- Codex ----
        Codex codex = new Codex();
        UUID p = UUID.randomUUID();
        ItemDef epee = Items.ALL.get("epee_braise");
        check("codex : première découverte", codex.discover(p, epee));
        check("codex : deuxième fois pas nouvelle", !codex.discover(p, epee));
        ItemDef g1 = Generator.build(Generator.TYPES.get(0), 0, 0, Rarity.COMMON, -1);
        ItemDef g2 = Generator.build(Generator.TYPES.get(0), 0, 0, Rarity.EPIC, -1);
        check("codex : même combinaison, raretés différentes = une seule découverte",
                codex.discover(p, g1) && !codex.discover(p, g2));
        codex.discover(p, Fusion.fuse(epee, Items.ALL.get("totem_loup")));
        Codex.Counts c = codex.counts(p);
        check("codex : compte catalogue/générés/fusions = 1/1/1", c.base() == 1 && c.generated() == 1 && c.fusions() == 1);

        Codex other = new Codex();
        other.restore(p, codex.snapshot().get(p));
        check("codex : sauvegarde puis restauration identique", other.counts(p).equals(c));

        // ---- Boss ----
        Bosses bosses = Bosses.defaults(new Random(5));
        boolean bossOk = true;
        Map<String, Set<Tag>> themes = Map.of("boss_soin", EnumSet.of(Tag.HEAL, Tag.SUPPORT), "boss_ombre", EnumSet.of(Tag.CURSED));
        int generated = 0;
        for (int i = 0; i < 2000; i++) {
            Optional<ItemDef> d = bosses.rollDrop("boss_ombre");
            if (d.isEmpty()) bossOk = false;
            else if (d.get().id().startsWith("g:")) generated++;
        }
        check("boss : toujours un drop", bossOk);
        check("boss : environ la moitié des drops sont générés", generated > 700 && generated < 1300);
        check("boss : table inconnue = vide", bosses.rollDrop("zzz").isEmpty());

        // Coins
        Random cr = new Random(1);
        boolean coinsOk = true;
        for (int i = 0; i < 1000; i++) {
            long m = Coins.mob(cr), b = Coins.boss(cr);
            if (m < 2 || m > 6 || b < 150 || b > 300) coinsOk = false;
        }
        check("pièces : mobs 2-6, boss 150-300", coinsOk);

        System.out.println();
        System.out.println(failures == 0 ? "Tous les tests de contenu passent." : failures + " test(s) en échec.");
        System.exit(failures == 0 ? 0 : 1);
    }
}
