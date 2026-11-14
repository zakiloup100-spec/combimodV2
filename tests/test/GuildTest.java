package test;

import com.example.combimod.core.*;
import java.util.*;

public class GuildTest {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "OK   " : "FAIL ") + name);
        if (!ok) failures++;
    }

    public static void main(String[] args) {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID(), c = UUID.randomUUID();

        // ---- Guildes ----
        Guilds g = new Guilds();
        check("guilde : création valide", g.create(a, "Dragons").ok());
        check("guilde : nom en double refusé (casse ignorée)", !g.create(b, "dragons").ok());
        check("guilde : déjà dans une guilde = refus", !g.create(a, "Autre").ok());
        check("guilde : nom trop court refusé", !g.create(b, "ab").ok());
        check("guilde : caractère interdit refusé", !g.create(b, "a b!").ok());
        check("guilde : invitation par le chef", g.invite(a, b).ok());
        check("guilde : invitation refusée à un non-chef", !g.invite(b, c).ok());
        check("guilde : accepter sans invitation refusé", !g.accept(c).ok());
        check("guilde : accepter une invitation", g.accept(b).ok() && g.guildOf(b).isPresent());
        check("guilde : le chef ne peut pas quitter avec des membres", !g.leave(a).ok());
        check("guilde : un membre peut quitter", g.leave(b).ok() && g.guildOf(b).isEmpty());
        check("guilde : dissolution par le chef", g.disband(a).ok() && g.guildOf(a).isEmpty());
        check("guilde : nom libéré après dissolution", g.create(c, "Dragons").ok());

        g.addPoints("Dragons", 100);
        g.addPoints("Dragons", -50);
        check("guilde : points négatifs ignorés", g.points("Dragons") == 100);
        Guilds lb = new Guilds();
        UUID x = UUID.randomUUID(), y = UUID.randomUUID();
        lb.create(x, "Alpha");
        lb.create(y, "Beta");
        lb.addPoints("Alpha", 10);
        lb.addPoints("Beta", 50);
        List<Guilds.Entry> top = lb.leaderboard(10);
        check("classement : trié par points décroissants", top.get(0).name().equals("Beta"));
        check("classement : respecte la limite", lb.leaderboard(1).size() == 1);

        Guilds copy = new Guilds();
        for (Guilds.Saved s : lb.export()) copy.restore(s);
        check("sauvegarde : guildes et points restaurés", copy.points("Beta") == 50 && copy.guildOf(y).isPresent());

        // ---- Points de capture ----
        boolean layoutOk = CapturePoints.LAYOUT.size() == 6;
        for (CapturePoints.Point p : CapturePoints.LAYOUT) {
            double d = Math.hypot(p.x(), p.z());
            if (d < 300 || d > 500) layoutOk = false;
        }
        check("points : 6 points entre 300 et 500 blocs du spawn", layoutOk);

        CapturePoints cp = new CapturePoints();
        String p1 = CapturePoints.LAYOUT.get(0).id();
        Map<String, Set<String>> solo = Map.of(p1, Set.of("Dragons"));

        check("événement arrêté : aucune capture", cp.step(solo).captured().isEmpty() && cp.ownerOf(p1).isEmpty());
        cp.start();
        check("événement démarré", cp.isActive());

        int seconds = 0;
        List<String> got = new ArrayList<>();
        while (got.isEmpty() && seconds < 100) {
            got.addAll(cp.step(solo).captured());
            seconds++;
        }
        check("capture : une guilde seule capture le point après " + CapturePoints.CAPTURE_SECONDS + " s",
                seconds == CapturePoints.CAPTURE_SECONDS && cp.ownerOf(p1).equals(Optional.of("Dragons")));

        Map<String, Set<String>> contested = Map.of(p1, Set.of("Dragons", "Loups"));
        cp.step(contested);
        check("capture gelée quand deux guildes sont présentes", cp.ownerOf(p1).equals(Optional.of("Dragons")));

        Map<String, Set<String>> rival = Map.of(p1, Set.of("Loups"));
        int rivalSeconds = 0;
        while (cp.ownerOf(p1).equals(Optional.of("Dragons")) && rivalSeconds < 200) {
            cp.step(rival);
            rivalSeconds++;
        }
        check("un rival prend le point après le même délai", cp.ownerOf(p1).equals(Optional.of("Loups")) && rivalSeconds == CapturePoints.CAPTURE_SECONDS);

        // Revenu : seulement pour les points possédés
        CapturePoints.Step s = cp.step(Map.of());
        check("revenu : le propriétaire touche le revenu de ses points", s.income().getOrDefault("Loups", 0L) == CapturePoints.POINTS_PER_SECOND);

        // Décroissance : une capture interrompue repart de zéro
        CapturePoints dec = new CapturePoints();
        dec.start();
        for (int i = 0; i < 10; i++) dec.step(solo);
        for (int i = 0; i < 20; i++) dec.step(Map.of());
        int extra = 0;
        while (dec.ownerOf(p1).isEmpty() && extra < 100) { dec.step(solo); extra++; }
        check("capture interrompue : pas de reprise à 100 %", extra > CapturePoints.CAPTURE_SECONDS - 10);

        // Événement : classement et arrêt
        CapturePoints ev = new CapturePoints();
        ev.start();
        String p2 = CapturePoints.LAYOUT.get(1).id();
        // Dragons capture deux points, Loups n'en capture aucun : Dragons doit être en tête
        for (int i = 0; i < CapturePoints.CAPTURE_SECONDS; i++) ev.step(Map.of(p1, Set.of("Dragons"), p2, Set.of("Dragons")));
        for (int i = 0; i < 50; i++) ev.step(Map.of(p1, Set.of("Dragons")));
        check("événement : la guilde qui tient le plus de points est en tête",
                ev.eventLeader().equals(Optional.of("Dragons")) && ev.eventPointsOf("Dragons") > ev.eventPointsOf("Loups"));
        Optional<String> winner = ev.stop();
        check("événement : arrêt renvoie le vainqueur", winner.equals(Optional.of("Dragons")));
        check("événement : après arrêt, plus de points", ev.step(solo).income().isEmpty());

        // Égalité : le résultat est déterministe (même entrée, même vainqueur)
        CapturePoints tie1 = new CapturePoints();
        tie1.start();
        for (int i = 0; i < CapturePoints.CAPTURE_SECONDS; i++) tie1.step(Map.of(p1, Set.of("Dragons"), p2, Set.of("Loups")));
        CapturePoints tie2 = new CapturePoints();
        tie2.start();
        for (int i = 0; i < CapturePoints.CAPTURE_SECONDS; i++) tie2.step(Map.of(p1, Set.of("Dragons"), p2, Set.of("Loups")));
        check("événement : égalité résolue de façon déterministe", tie1.eventLeader().equals(tie2.eventLeader()) && tie1.eventLeader().isPresent());

        CapturePoints fresh = new CapturePoints();
        check("événement : sans point, pas de vainqueur", fresh.stop().isEmpty());

        System.out.println();
        System.out.println(failures == 0 ? "Tous les tests de guildes passent." : failures + " test(s) en échec.");
        System.exit(failures == 0 ? 0 : 1);
    }
}
