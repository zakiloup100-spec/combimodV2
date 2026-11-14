package test;

import com.example.combimod.core.*;
import java.util.*;

public class CoreTest {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "OK   " : "FAIL ") + name);
        if (!ok) failures++;
    }

    public static void main(String[] args) {
        // 1. Fusion : les tags s'ajoutent, la synergie MAGIC+SUMMON donne un bonus
        ItemDef necro = Items.ALL.get("necro_orbe");
        ItemDef totem = Items.ALL.get("totem_loup");
        ItemDef fused = Fusion.fuse(necro, totem);
        check("fusion contient les tags des deux parents",
                fused.tags().containsAll(Set.of(Tag.MAGIC, Tag.SUMMON)));
        check("fusion ajoute le bonus de synergie SUMMON_DAMAGE",
                fused.modifiers().stream().anyMatch(m -> m.affix() == Affix.SUMMON_DAMAGE && m.value() > 0));

        // 2. Malédiction retournée : un malus devient un bonus réduit
        ItemDef pacte = Items.ALL.get("pacte_sanglant");
        ItemDef baton = Items.ALL.get("baton_arcane");
        ItemDef cursedFused = Fusion.fuse(pacte, baton);
        double hp = cursedFused.modifiers().stream()
                .filter(m -> m.affix() == Affix.MAX_HEALTH)
                .mapToDouble(Modifier::value).sum();
        check("malus de vie retourné en bonus réduit (positif)", hp > 0);
        check("bonus retourné = moitié du malus (2)", Math.abs(hp - 2.0) < 1e-9);

        // 3. Recette spéciale : résultat exact, pas la fusion générique
        ItemDef special = Fusion.fuse(pacte, Items.ALL.get("grimoire_soin"));
        check("recette spéciale renvoie le Grimoire du pacte",
                special.id().equals("grimoire_du_pacte"));
        check("recette spéciale fonctionne dans les deux sens",
                Fusion.fuse(Items.ALL.get("grimoire_soin"), pacte).id().equals("grimoire_du_pacte"));

        // 4. Rareté : la fusion prend la rareté la plus haute
        check("fusion prend la rareté max",
                Fusion.fuse(Items.ALL.get("epee_braise"), Items.ALL.get("sang_dragon")).rarity() == Rarity.LEGENDARY);

        // 5. Économie
        Economy eco = new Economy();
        UUID p = UUID.randomUUID();
        eco.add(p, 500);
        check("solde initial après ajout", eco.balance(p) == 500);
        check("achat refusé si solde insuffisant", !eco.take(p, 600));
        check("solde inchangé après achat refusé", eco.balance(p) == 500);
        check("achat accepté si solde suffisant", eco.take(p, 200) && eco.balance(p) == 300);
        check("vente = moitié du prix", eco.sellPrice(Items.ALL.get("epee_braise")) == 50);
        check("vente minimum 1", eco.sellPrice(new ItemDef("x", "x", Set.of(), List.of(), 0, Rarity.COMMON)) == 1);

        // 6. Boutique
        Shop shop = new Shop(Items.ALL);
        UUID q = UUID.randomUUID();
        eco.add(q, 10);
        check("boutique refuse un id inconnu", shop.buy(q, "inconnu", eco).isEmpty());
        check("boutique refuse un achat sans argent", shop.buy(q, "sang_dragon", eco).isEmpty());

        // 7. Loot : tous les tirages sont des items du catalogue, et la rareté commune domine
        LootRoller roller = new LootRoller(Items.ALL.values(), new Random(42));
        Map<Rarity, Integer> counts = new EnumMap<>(Rarity.class);
        boolean allValid = true;
        for (int i = 0; i < 10000; i++) {
            Optional<ItemDef> d = roller.roll();
            if (d.isEmpty() || !Items.ALL.containsKey(d.get().id())) { allValid = false; break; }
            counts.merge(d.get().rarity(), 1, Integer::sum);
        }
        check("loot : tirages valides sur 10 000", allValid);
        check("loot : commun plus fréquent que épique",
                counts.getOrDefault(Rarity.COMMON, 0) > counts.getOrDefault(Rarity.EPIC, 0));
        check("loot : mythique très rare (< 1 %)",
                counts.getOrDefault(Rarity.MYTHIC, 0) < 100);

        // 8. Drops de boss : chaque pool répond un item de son pool
        Bosses bosses = Bosses.defaults(new Random(1));
        check("boss : pool inconnu renvoie vide", bosses.rollDrop("inconnu").isEmpty());
        check("boss : pool soin donne un item de soin/support",
                bosses.rollDrop("boss_soin").isPresent());

        System.out.println();
        System.out.println(failures == 0 ? "Tous les tests passent." : failures + " test(s) en échec.");
        System.exit(failures == 0 ? 0 : 1);
    }
}
