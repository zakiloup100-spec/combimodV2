package com.example.combimod.core;

import java.util.*;
import java.util.regex.Pattern;

/** Guildes : une par joueur, un chef, des invitations. Les points de guilde ne baissent jamais. */
public final class Guilds {

    public record Result(boolean ok, String message) {
        static Result yes(String m) { return new Result(true, m); }
        static Result no(String m) { return new Result(false, m); }
    }

    public record Entry(String name, long points, int members) {}

    public static final int MAX_MEMBERS = 20;
    private static final Pattern NAME = Pattern.compile("[A-Za-z0-9_]{3,16}");

    private static final class Guild {
        final String name;
        UUID leader;
        final Set<UUID> members = new LinkedHashSet<>();
        long points;

        Guild(String name, UUID leader) {
            this.name = name;
            this.leader = leader;
            this.members.add(leader);
        }
    }

    private final Map<String, Guild> guilds = new HashMap<>();   // clé : nom en minuscules
    private final Map<UUID, String> membership = new HashMap<>(); // joueur -> clé
    private final Map<UUID, String> invites = new HashMap<>();    // invité -> clé de la guilde

    private static String key(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public Result create(UUID leader, String name) {
        if (membership.containsKey(leader)) return Result.no("Tu es déjà dans une guilde.");
        if (!NAME.matcher(name).matches()) return Result.no("Nom invalide : 3 à 16 lettres, chiffres ou _.");
        if (guilds.containsKey(key(name))) return Result.no("Ce nom est déjà pris.");
        guilds.put(key(name), new Guild(name, leader));
        membership.put(leader, key(name));
        return Result.yes("Guilde « " + name + " » créée.");
    }

    public Result invite(UUID inviter, UUID target) {
        String k = membership.get(inviter);
        if (k == null) return Result.no("Tu n'es dans aucune guilde.");
        Guild g = guilds.get(k);
        if (!g.leader.equals(inviter)) return Result.no("Seul le chef peut inviter.");
        if (membership.containsKey(target)) return Result.no("Ce joueur est déjà dans une guilde.");
        if (g.members.size() >= MAX_MEMBERS) return Result.no("La guilde est pleine.");
        invites.put(target, k);
        return Result.yes("Invitation envoyée.");
    }

    public Result accept(UUID target) {
        String k = invites.remove(target);
        if (k == null) return Result.no("Aucune invitation en attente.");
        Guild g = guilds.get(k);
        if (g == null) return Result.no("Cette guilde n'existe plus.");
        if (membership.containsKey(target)) return Result.no("Tu es déjà dans une guilde.");
        if (g.members.size() >= MAX_MEMBERS) return Result.no("La guilde est pleine.");
        g.members.add(target);
        membership.put(target, k);
        return Result.yes("Tu rejoins « " + g.name + " ».");
    }

    /** Le chef ne peut pas quitter une guilde qui a encore des membres : il doit la dissoudre. */
    public Result leave(UUID player) {
        String k = membership.get(player);
        if (k == null) return Result.no("Tu n'es dans aucune guilde.");
        Guild g = guilds.get(k);
        if (g.leader.equals(player) && g.members.size() > 1) {
            return Result.no("Tu es le chef : dissous la guilde ou attends que tout le monde soit parti.");
        }
        g.members.remove(player);
        membership.remove(player);
        if (g.members.isEmpty()) guilds.remove(k);
        return Result.yes("Tu as quitté « " + g.name + " ».");
    }

    public Result disband(UUID leader) {
        String k = membership.get(leader);
        if (k == null) return Result.no("Tu n'es dans aucune guilde.");
        Guild g = guilds.get(k);
        if (!g.leader.equals(leader)) return Result.no("Seul le chef peut dissoudre la guilde.");
        for (UUID m : g.members) membership.remove(m);
        guilds.remove(k);
        return Result.yes("Guilde dissoute.");
    }

    public Optional<String> guildOf(UUID player) {
        String k = membership.get(player);
        return k == null ? Optional.empty() : Optional.of(guilds.get(k).name);
    }

    /** Nom canonique d'une guilde, à partir d'un nom quelconque (insensible à la casse). */
    public Optional<String> nameOf(String anyCase) {
        Guild g = guilds.get(key(anyCase));
        return g == null ? Optional.empty() : Optional.of(g.name);
    }

    public Set<UUID> members(String name) {
        Guild g = guilds.get(key(name));
        return g == null ? Set.of() : Collections.unmodifiableSet(g.members);
    }

    public long points(String name) {
        Guild g = guilds.get(key(name));
        return g == null ? 0 : g.points;
    }

    public void addPoints(String name, long amount) {
        Guild g = guilds.get(key(name));
        if (g != null && amount > 0) g.points += amount;
    }

    public List<Entry> leaderboard(int limit) {
        List<Entry> all = new ArrayList<>();
        for (Guild g : guilds.values()) all.add(new Entry(g.name, g.points, g.members.size()));
        all.sort(Comparator.comparingLong(Entry::points).reversed().thenComparing(Entry::name));
        return all.subList(0, Math.min(limit, all.size()));
    }

    /** Pour la sauvegarde : nom, chef, membres, points. */
    public record Saved(String name, UUID leader, List<UUID> members, long points) {}

    public List<Saved> export() {
        List<Saved> out = new ArrayList<>();
        for (Guild g : guilds.values()) out.add(new Saved(g.name, g.leader, new ArrayList<>(g.members), g.points));
        return out;
    }

    public void restore(Saved s) {
        Guild g = new Guild(s.name(), s.leader());
        g.members.clear();
        g.members.addAll(s.members());
        g.points = s.points();
        guilds.put(key(s.name()), g);
        for (UUID m : g.members) membership.put(m, key(s.name()));
    }
}
