package com.example.combimod.core;

import java.util.*;

/**
 * Alliés : une invitation à la fois, acceptée par la personne invitée. La relation est mutuelle.
 * Les invitations ne sont pas sauvegardées (elles expirent à la déconnexion du serveur).
 */
public final class Allies {

    private final Map<UUID, Set<UUID>> allies = new HashMap<>();
    private final Map<UUID, UUID> invites = new HashMap<>(); // invité -> inviteur

    /** Renvoie false si c'est soi-même, déjà allié, ou si l'invitation est impossible. */
    public boolean invite(UUID from, UUID to) {
        if (from.equals(to) || isAlly(from, to)) return false;
        invites.put(to, from);
        return true;
    }

    public Optional<UUID> pendingFor(UUID to) {
        return Optional.ofNullable(invites.get(to));
    }

    /** Accepte l'invitation en attente. Renvoie l'inviteur, ou vide s'il n'y en a pas. */
    public Optional<UUID> accept(UUID to) {
        UUID from = invites.remove(to);
        if (from == null) return Optional.empty();
        link(from, to);
        return Optional.of(from);
    }

    private void link(UUID a, UUID b) {
        allies.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        allies.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    }

    public boolean remove(UUID a, UUID b) {
        boolean removed = unlink(a, b);
        removed |= unlink(b, a);
        return removed;
    }

    private boolean unlink(UUID a, UUID b) {
        Set<UUID> set = allies.get(a);
        if (set == null) return false;
        boolean removed = set.remove(b);
        if (set.isEmpty()) allies.remove(a);
        return removed;
    }

    public boolean isAlly(UUID a, UUID b) {
        return allies.getOrDefault(a, Set.of()).contains(b);
    }

    public Set<UUID> allyOf(UUID a) {
        return Collections.unmodifiableSet(allies.getOrDefault(a, Set.of()));
    }

    public Map<UUID, Set<UUID>> snapshot() {
        Map<UUID, Set<UUID>> copy = new HashMap<>();
        allies.forEach((k, v) -> copy.put(k, new HashSet<>(v)));
        return copy;
    }

    public void restore(UUID player, Collection<UUID> others) {
        for (UUID other : others) link(player, other);
    }
}
