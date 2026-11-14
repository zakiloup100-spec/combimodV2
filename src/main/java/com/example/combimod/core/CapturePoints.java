package com.example.combimod.core;

import java.util.*;

/**
 * Points de capture éloignés du spawn (entre 300 et 500 blocs). Une guilde capture un point
 * en étant seule dessus pendant CAPTURE_SECONDS. Si plusieurs guildes sont présentes, la capture est gelée.
 * Un point possédé rapporte des points de guilde chaque seconde, et sert au classement de l'événement.
 */
public final class CapturePoints {

    public record Point(String id, int x, int z) {}

    /** Résultat d'une seconde : captures et points gagnés. */
    public record Step(List<String> captured, Map<String, Long> income) {}

    public static final int CAPTURE_SECONDS = 30;
    public static final long POINTS_PER_SECOND = 2;
    public static final long EVENT_WINNER_BONUS = 500;
    public static final double CAPTURE_RADIUS = 12.0;

    public static final List<Point> LAYOUT = buildLayout();

    private final Map<String, String> owner = new HashMap<>();
    private final Map<String, String> capturer = new HashMap<>();
    private final Map<String, Integer> progress = new HashMap<>();
    private final Map<String, Long> eventPoints = new HashMap<>();
    private boolean active;

    static List<Point> buildLayout() {
        List<Point> out = new ArrayList<>();
        int n = 6;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n + 0.3;
            int radius = 300 + (i * 73) % 200; // toujours entre 300 et 499
            out.add(new Point("p" + (i + 1), (int) Math.round(radius * Math.cos(angle)),
                    (int) Math.round(radius * Math.sin(angle))));
        }
        return Collections.unmodifiableList(out);
    }

    public boolean isActive() {
        return active;
    }

    /** Démarre l'événement : les points de l'événement repartent de zéro, les propriétaires restent. */
    public void start() {
        active = true;
        eventPoints.clear();
        capturer.clear();
        progress.clear();
    }

    /** Arrête l'événement. Renvoie la guilde en tête du classement de l'événement, si il y en a une. */
    public Optional<String> stop() {
        active = false;
        capturer.clear();
        progress.clear();
        return eventLeader();
    }

    public Optional<String> eventLeader() {
        return eventPoints.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .max(Map.Entry.<String, Long>comparingByValue().thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .map(Map.Entry::getKey);
    }

    public long eventPointsOf(String guild) {
        return eventPoints.getOrDefault(guild, 0L);
    }

    public Optional<String> ownerOf(String pointId) {
        return Optional.ofNullable(owner.get(pointId));
    }

    /**
     * Avance d'une seconde. presence : pour chaque point, les guildes qui ont au moins un membre dessus.
     * Ne fait rien si l'événement est arrêté.
     */
    public Step step(Map<String, Set<String>> presence) {
        if (!active) return new Step(List.of(), Map.of());

        List<String> captured = new ArrayList<>();
        Map<String, Long> income = new HashMap<>();

        for (Point p : LAYOUT) {
            Set<String> here = presence.getOrDefault(p.id(), Set.of());
            if (here.size() == 1) {
                String g = here.iterator().next();
                if (g.equals(owner.get(p.id()))) {
                    capturer.remove(p.id());
                    progress.remove(p.id());
                } else {
                    if (!g.equals(capturer.get(p.id()))) {
                        capturer.put(p.id(), g);
                        progress.put(p.id(), 0);
                    }
                    int v = progress.getOrDefault(p.id(), 0) + 1;
                    progress.put(p.id(), v);
                    if (v >= CAPTURE_SECONDS) {
                        owner.put(p.id(), g);
                        capturer.remove(p.id());
                        progress.remove(p.id());
                        captured.add(p.id() + ":" + g);
                    }
                }
            } else if (here.isEmpty()) {
                // Personne : la capture en cours recule doucement, le propriétaire garde son point
                Integer v = progress.get(p.id());
                if (v != null) {
                    if (v <= 1) {
                        progress.remove(p.id());
                        capturer.remove(p.id());
                    } else {
                        progress.put(p.id(), v - 1);
                    }
                }
            }
            // Plusieurs guildes présentes : capture gelée, rien ne change.

            String o = owner.get(p.id());
            if (o != null) {
                income.merge(o, POINTS_PER_SECOND, Long::sum);
                eventPoints.merge(o, POINTS_PER_SECOND, Long::sum);
            }
        }
        return new Step(captured, income);
    }

    /** Pour la sauvegarde des propriétaires. */
    public Map<String, String> owners() {
        return Collections.unmodifiableMap(owner);
    }

    public void restoreOwner(String pointId, String guild) {
        owner.put(pointId, guild);
    }
}
